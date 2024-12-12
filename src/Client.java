import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Classe principale pour le client de l'application de chat.
 * Gère la connexion au serveur, le chiffrement/déchiffrement des messages et les interactions utilisateur.
 */
public class Client {

    // Clé secrète AES pour chiffrer les messages
    private static SecretKey secretKey = null;

    // Clé publique RSA du serveur pour échanger la clé AES
    private static PublicKey serverPublicKey = null;

    // Fenêtre graphique pour la discussion
    public static Fenetre_discution Fenetre;

    // Historique des messages partagés
    public static ArrayList<String> chaine_Text = new ArrayList<>();

    // Port de connexion au serveur
    private static int port = 3346;

    // Pseudo du client
    private static String pseudo;

    // Socket pour communiquer avec le serveur
    private static Socket clientSocket;

    /**
     * Point d'entrée principal de l'application client.
     * 
     * @param args Arguments passés au programme (non utilisés ici).
     * @throws Exception En cas d'erreur lors de l'exécution du client.
     */
    public static void main(String[] args) throws Exception {
        startClient(chaine_Text);
    }

    /**
     * Initialise et démarre le client.
     * 
     * @param chaine_Text2 Liste partagée des messages entre le client et l'interface graphique.
     * @throws Exception En cas d'erreur lors de l'initialisation.
     */
    public static void startClient(ArrayList<String> chaine_Text2) throws Exception {

        BufferedReader in;
        PrintWriter out;

        try {
            boolean connected = false;

            // Boucle pour tenter la connexion au serveur jusqu'à réussite
            while (!connected) {
                try {
                    System.out.println("Tentative de connexion sur le port : " + port);
                    clientSocket = new Socket("127.0.0.1", port);
                    connected = true; // Si la connexion réussit
                    System.out.println("Connecté au serveur sur le port : " + port);
                } catch (IOException e) {
                    // En cas d'échec, demander un nouveau port à l'utilisateur
                    String portInput = JOptionPane.showInputDialog(
                            null,
                            "Connexion échouée sur le port " + port + ". Entrez un nouveau numéro de port :",
                            "Configuration",
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (portInput == null || portInput.isEmpty()) {
                        System.out.println("Aucun port saisi. Fermeture.");
                        System.exit(0);
                    }

                    try {
                        port = Integer.parseInt(portInput);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Veuillez entrer un numéro de port valide (entier).",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }

            // Initialisation des flux de communication
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 1. Recevoir la clé publique RSA du serveur
            String publicKeyBase64 = in.readLine();
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            serverPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            System.out.println("Clé publique du serveur reçue.");

            // 2. Générer une clé AES
            secretKey = generateAESKey();
            System.out.println("Clé AES générée : " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));

            // 3. Chiffrer la clé AES avec la clé publique RSA
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] encryptedAESKey = cipher.doFinal(secretKey.getEncoded());
            String encryptedAESKeyBase64 = Base64.getEncoder().encodeToString(encryptedAESKey);

            // 4. Envoyer la clé AES chiffrée au serveur
            out.println(encryptedAESKeyBase64);
            out.flush();
            System.out.println("Clé AES chiffrée envoyée au serveur.");

            // 5. Envoyer le pseudo au serveur
            Fenetre = new Fenetre_discution(chaine_Text);
            while (Fenetre.pseudo_client.isEmpty()) {
                // Attendre que l'utilisateur entre son pseudo
            }
            pseudo = Fenetre.pseudo_client;
            String encryptedPseudo = encrypt(Fenetre.pseudo_client, secretKey);
            out.println(encryptedPseudo);
            out.flush();

            // Recevoir le pseudo final (si modifié par le serveur)
            pseudo = decrypt(in.readLine(), secretKey);
            Fenetre.pseudo_client = pseudo;
            System.out.println("Pseudo confirmé : " + pseudo);

            // Initialisation des threads pour la gestion des messages
            Client client = new Client();

            Thread envoi = new Thread(() -> client.handleSending(out, pseudo, clientSocket));
            Thread recevoir = new Thread(() -> {
                try {
                    client.handleReceiving(in, pseudo);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            });

            // Démarrage des threads
            envoi.start();
            recevoir.start();

        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    /**
     * Gère l'envoi des messages avec chiffrement AES.
     * 
     * @param out Flux de sortie pour envoyer les messages au serveur.
     * @param pseudo Pseudo du client.
     * @param clientSocket Socket de connexion.
     */
    private void handleSending(PrintWriter out, String pseudo, Socket clientSocket) {
        String msg;

        try {
            while (true) {
                msg = getNextMessage();
                if (!msg.isEmpty()) {
                    System.out.println("Message à envoyer : " + msg);
                    String encryptedMsg = encrypt(msg, secretKey);
                    out.println(encryptedMsg);
                    out.flush();

                    if (msg.equals("bye")) {
                        out.close();
                        clientSocket.close();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi : " + e.getMessage());
        }
    }

    /**
     * Gère la réception des messages avec déchiffrement AES.
     * 
     * @param in Flux d'entrée pour recevoir les messages du serveur.
     * @param pseudo Pseudo du client.
     * @throws NoSuchAlgorithmException En cas d'erreur dans l'algorithme de génération de clé.
     * @throws InvalidKeySpecException En cas d'erreur de spécification de la clé.
     */
    private void handleReceiving(BufferedReader in, String pseudo) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String msg;

        try {
            while ((msg = in.readLine()) != null) {
                try {
                    String decryptedMsg = decrypt(msg, secretKey);
                    System.out.println("Message reçu et déchiffré : " + decryptedMsg);
                    Fenetre.addMessage(decryptedMsg);
                } catch (Exception e) {
                    System.err.println("Erreur de déchiffrement : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la réception des messages : " + e.getMessage());
        }
    }

    /**
     * Obtient le prochain message depuis la liste partagée.
     * 
     * @return Le prochain message, ou une chaîne vide si aucun message n'est présent.
     */
    public static synchronized String getNextMessage() {
        if (!chaine_Text.isEmpty()) {
            return chaine_Text.remove(0);
        }
        return "";
    }

    /**
     * Chiffre un message en utilisant la clé secrète AES.
     * 
     * @param plainText Le texte en clair à chiffrer.
     * @param secretKey La clé secrète AES.
     * @return Le texte chiffré encodé en Base64.
     * @throws Exception En cas d'erreur de chiffrement.
     */
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Déchiffre un message chiffré en utilisant la clé secrète AES.
     * 
     * @param encryptedText Le texte chiffré encodé en Base64.
     * @param secretKey La clé secrète AES.
     * @return Le texte déchiffré en clair.
     * @throws Exception En cas d'erreur de déchiffrement.
     */
    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    /**
     * Génère une nouvelle clé secrète AES.
     * 
     * @return Une instance de {@link SecretKey} générée.
     * @throws NoSuchAlgorithmException En cas d'erreur de génération de clé.
     */
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    /**
     * Convertit un tableau de bytes en clé secrète AES.
     * 
     * @param keyBytes Le tableau de bytes représentant la clé.
     * @return Une instance de {@link SecretKey} créée à partir des bytes donnés.
     */
    public static SecretKey getKeyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }
}
