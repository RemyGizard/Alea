import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class Client {
    private SecretKey secretKey = null; // Variable d'instance partagée entre threads
    public static Fenetre_discution Fenetre;
    public static ArrayList<String> chaine_Text = new ArrayList<>();
    private static final int port = 3346;
    private boolean envoie_valide = false;

    public static void main(String[] args) {
        startClient(chaine_Text, port);
    }

    public static void startClient(ArrayList<String> chaine_Text2, int port) {
        Socket clientSocket;
        BufferedReader in;
        PrintWriter out;
        String pseudo = "Remy";

        try {
            // Initialisation de la connexion
            clientSocket = new Socket("127.0.0.69", port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            Fenetre = new Fenetre_discution(chaine_Text2);
            Fenetre.addMessage("Connection au serveur", false, false);

            // Instancier le client avec la gestion de la clé
            Client client = new Client();

            // Thread pour envoyer des messages
            Thread envoi = new Thread(() -> client.handleSending(out, pseudo, clientSocket));

            // Thread pour recevoir des messages
            Thread recevoir = new Thread(() -> client.handleReceiving(in, pseudo));

            // Démarrer les threads
            envoi.start();
            recevoir.start();

        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    /**
     * Gestion de l'envoi des messages (chiffrement inclus)
     */
    private void handleSending(PrintWriter out, String pseudo, Socket clientSocket) {
        String msg;

        try {
            while (true) {
                synchronized (this) {
                    if (secretKey == null) {
                        // Générer la clé AES et l'envoyer au serveur
                        secretKey = generateAESKey();
                        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
                        out.println(encodedKey + "@code@" + pseudo);
                        out.flush();
                    }
                }

                // Envoyer les messages de la fenêtre de discussion
                msg = getNextMessage();
                if (msg.length() > 0) {
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
     * Gestion de la réception des messages (déchiffrement inclus)
     */
    private void handleReceiving(BufferedReader in, String pseudo) {
        String msg;

        try {
            while ((msg = in.readLine()) != null) {
                synchronized (this) {
                    if (secretKey == null) {
                       
                        continue;
                    }
                }

                try {
                    // Déchiffrement du message
                	System.out.println(msg);
                    String decryptedMsg = decrypt(msg, secretKey);
                    System.out.println("Message reçu et déchiffré : " + decryptedMsg);
                    Fenetre.addMessage(decryptedMsg, false, pseudo.equals(decryptedMsg.split(":")[0]));
                } catch (Exception e) {
                    System.err.println("Erreur de déchiffrement : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la réception des messages : " + e.getMessage());
        }
    }

    /**
     * Obtenir le prochain message depuis la fenêtre de discussion
     */
    public static String getNextMessage() {
        if (!chaine_Text.isEmpty()) {
            return chaine_Text.remove(0);
        }
        return "";
    }

    /**
     * Chiffrement des messages
     */
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // Réinitialisation
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey); // Réinitialisation
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    /**
     * Générer une clé AES aléatoire
     */
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Longueur de clé AES
        return keyGenerator.generateKey();
    }

    /**
     * Convertir une clé AES à partir d'un tableau de bytes
     */
    public static SecretKey getKeyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }
}
