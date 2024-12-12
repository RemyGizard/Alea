import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * Classe principale du serveur de discussion sécurisé.
 * Gère les connexions des clients, les échanges de clés et la diffusion des messages.
 */
public class Serveur {

    private static final int PORT = 3346; // Port du serveur
    private static Map<String, PrintWriter> clients = new HashMap<>(); // Liste des clients connectés
    private static Map<String, SecretKey> clientKeys = new HashMap<>(); // Clés AES associées aux clients
    private static List<String> messageHistory = new ArrayList<>(); // Historique des messages
    private static PrivateKey privateKey; // Clé privée RSA du serveur
    private static PublicKey publicKey; // Clé publique RSA du serveur

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Génération de la paire de clés RSA pour le serveur
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        System.out.println("Clé publique générée :\n" + Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        // Lancement du serveur
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port : "+PORT+", en attente de connexions...");

            // Boucle principale pour accepter les connexions des clients
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start(); // Création d'un thread pour chaque client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Classe interne pour gérer les interactions avec un client.
     */
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private SecretKey secretKey;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);

                // Envoi de la clé publique RSA au client
                out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
                System.out.println("Clé publique envoyée au client.");

                // Réception de la clé AES chiffrée
                String encryptedAESKey = in.readLine();
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decodedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));
                secretKey = new SecretKeySpec(decodedKey, "AES");
                System.out.println("Clé AES déchiffrée pour le client.");

                // Réception et déchiffrement du pseudo
                String encryptedPseudo = in.readLine();
                if (encryptedPseudo != null && !encryptedPseudo.isEmpty()) {
                    username = decrypt(encryptedPseudo, secretKey);
                    System.out.println("Pseudo du client : " + username);

                    // Gestion des doublons de pseudonymes
                    synchronized (clients) {
                        int counter = 1;
                        String originalUsername = username;
                        while (clients.containsKey(username)) {
                            username = originalUsername + counter++;
                        }
                        clients.put(username, out);
                        clientKeys.put(username, secretKey);
                    }
                    System.out.println("Client enregistré : " + username);
                } else {
                    System.err.println("Erreur : Pseudo non reçu ou vide.");
                    return;
                }

                // Envoi du pseudo confirmé au client
                out.println(encrypt(username, secretKey));

                // Envoi de l'historique des messages
                sendHistory();

                // Boucle principale pour gérer les messages du client
                String message;
                while ((message = in.readLine()) != null) {
                	System.out.println("Message reçus de"+username);
                	System.out.println("Message reçu crypter:"+ message);
                    message = decrypt(message, secretKey);
                    System.out.println("Message reçu decrypter:"+ message);

                    if (message.contains(":")) {
                        // Gestion des messages privés
                        handlePrivateMessage(message);
                    } else {
                        // Diffusion des messages à tous les clients
                        broadcastMessage(username + " : " + message);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur avec le client " + username + " : " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        /**
         * Diffuse un message à tous les clients connectés.
         *
         * @param message Le message à diffuser.
         */
        private void broadcastMessage(String message) {
            synchronized (clients) {
                messageHistory.add(message); // Ajout à l'historique
                for (String user : clients.keySet()) {
                    try {
                        PrintWriter writer = clients.get(user);
                        SecretKey userKey = clientKeys.get(user);
                        writer.println(encrypt(message, userKey));
                    } catch (Exception e) {
                        System.err.println("Erreur d'envoi pour l'utilisateur " + user);
                    }
                }
            }
        }

        /**
         * Gère l'envoi d'un message privé à un client spécifique.
         *
         * @param message Le message au format "DESTINATAIRE:MESSAGE".
         */
        private void handlePrivateMessage(String message) throws Exception {
            String[] parts = message.split(":", 2);
            if (parts.length == 2) {
                String targetUser = parts[0].trim();
                String privateMessage = parts[1].trim();

                synchronized (clients) {
                    PrintWriter targetWriter = clients.get(targetUser);
                    if (targetWriter != null) {
                        SecretKey targetKey = clientKeys.get(targetUser);
                        targetWriter.println(encrypt(username + " (privé) : " + privateMessage, targetKey));
                        out.println(encrypt(username + " (privé à "+targetUser+") : " + privateMessage, secretKey));
                    } else {
                        out.println(encrypt("Utilisateur " + targetUser + " introuvable.", secretKey));
                    }
                }
            } else {
                out.println(encrypt("Format invalide. Utilisez : DESTINATAIRE:MESSAGE", secretKey));
            }
        }

        /**
         * Envoie l'historique des messages au client connecté.
         */
        private void sendHistory() throws Exception {
            synchronized (messageHistory) {
                for (String msg : messageHistory) {
                    out.println(encrypt(msg, secretKey));
                }
                out.println(encrypt("Bienvenue " + username, secretKey));
                out.println(encrypt("Commande des messages privé -> pseudo : message", secretKey));
            }
        }

        /**
         * Nettoyage après la déconnexion du client.
         */
        private void cleanup() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (clients) {
                clients.remove(username);
                clientKeys.remove(username);
            }
            System.out.println("Utilisateur déconnecté : " + username);
        }

        /**
         * Chiffrement d'un message avec AES.
         *
         * @param plainText  Le texte en clair.
         * @param secretKey  La clé AES.
         * @return Le texte chiffré.
         * @throws Exception En cas d'erreur de chiffrement.
         */
        public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        /**
         * Déchiffrement d'un message avec AES.
         *
         * @param encryptedText Le texte chiffré.
         * @param secretKey     La clé AES.
         * @return Le texte en clair.
         * @throws Exception En cas d'erreur de déchiffrement.
         */
        public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        }
    }
}
