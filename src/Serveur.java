import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Serveur {
    private static final int PORT = 3346;
    private static Map<String, PrintWriter> clients = new HashMap<>();
    private static Map<String, SecretKey> clientKeys = new HashMap<>(); // Associe chaque utilisateur à sa clé
    private static List<String> messageHistory = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Le serveur est prêt à recevoir des connexions...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private SecretKey secretKey;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Étape 1 : Demander un nom d'utilisateur unique et associer une clé
                while (true) {
                    String[] information = in.readLine().split("@code@");
                    byte[] decodedKey = Base64.getDecoder().decode(information[0]);
                    secretKey = new SecretKeySpec(decodedKey, "AES");
                    username = information[1];

                    if (username == null || username.trim().isEmpty()) continue;

                    synchronized (clients) {
                        if (!clients.containsKey(username)) {
                            clients.put(username, out);
                            clientKeys.put(username, secretKey);
                            out.println(encrypt("Bienvenue " + username + " !", secretKey));
                            System.out.println("Utilisateur connecté : " + username);
                            break;
                        } else {
                            out.println(encrypt("Ce nom d'utilisateur est déjà pris. Essayez un autre.", secretKey));
                        }
                    }
                }

                // Étape 2 : Lire les messages et les diffuser
                String message;
                while ((message = in.readLine()) != null) {
                    message = decrypt(message, secretKey);

                    if (message.equals("HISTORIQUE")) {
                        sendHistory();
                    } else if (message.contains(":")) { // Message privé
                        String[] parts = message.split(":", 2);
                        if (parts.length == 2) {
                            String targetUser = parts[0].trim();
                            String privateMessage = parts[1].trim();

                            synchronized (clients) {
                                PrintWriter targetWriter = clients.get(targetUser);
                                if (targetWriter != null) {
                                    SecretKey targetKey = clientKeys.get(targetUser);
                                    targetWriter.println(encrypt(username + " (privé) : " + privateMessage, targetKey));
                                } else {
                                    out.println(encrypt("Utilisateur " + targetUser + " introuvable.", secretKey));
                                }
                            }
                        } else {
                            out.println(encrypt("Format de message invalide. Utilisez : DESTINATAIRE:MESSAGE", secretKey));
                        }
                    } else { // Message diffusé à tous les utilisateurs
                        broadcastMessage(username + " : " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connexion perdue avec " + username);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }

        // Méthode pour diffuser un message à tous les clients
        private void broadcastMessage(String message) {
            synchronized (clients) {
                messageHistory.add(message); // Ajouter à l'historique
                System.out.println("Diffusé : " + message); // Afficher dans le terminal

                for (String user : clients.keySet()) {
                    try {
                        PrintWriter writer = clients.get(user);
                        SecretKey userKey = clientKeys.get(user);
                        if (userKey != null) {
                            String encryptedMessage = encrypt(message, userKey);
                            writer.println(encryptedMessage);
                        } else {
                            System.err.println("Erreur : Clé non trouvée pour l'utilisateur " + user);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur de chiffrement pour l'utilisateur : " + user);
                        e.printStackTrace();
                    }
                }
            }
        }

        // Méthode pour envoyer l'historique des messages à un client
        private void sendHistory() {
            try {
                out.println(encrypt("Historique des messages :", secretKey));
                synchronized (messageHistory) {
                    for (String msg : messageHistory) {
                        out.println(encrypt(msg, secretKey));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Nettoyage après déconnexion
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

        // Méthode de chiffrement
        public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        // Méthode de déchiffrement
        public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        }
    }
}
