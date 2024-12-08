import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class Client {
	private static SecretKey secretKey = null;
    private static PublicKey serverPublicKey = null;
    public static Fenetre_discution Fenetre;
    public static ArrayList<String> chaine_Text = new ArrayList<>();
    private static final int port = 3346;
    private boolean envoie_valide = false;

    public static void main(String[] args) throws Exception {
        startClient(chaine_Text, port);
    }

    public static void startClient(ArrayList<String> chaine_Text2, int port) throws Exception {
        Socket clientSocket;
        BufferedReader in;
        PrintWriter out;
        String pseudo = "Reyan";

        try {
            // Initialisation de la connexion
        	 Socket clientSocket1 = new Socket("127.0.0.1", port);
             System.out.println("Connecté au serveur.");

             in = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
             out = new PrintWriter(clientSocket1.getOutputStream(), true);

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
             out.flush(); // Important pour s'assurer que le message est envoyé immédiatement
             System.out.println("Clé AES chiffrée envoyée au serveur.");

             // 5. Envoyer le pseudo au serveur
             String encryptedPseudo = encrypt(pseudo, secretKey);
             out.println(encryptedPseudo);
             out.flush(); 
             System.out.println("Pseudo chiffré envoyé au serveur : " + encryptedPseudo);
             
            Fenetre = new Fenetre_discution(chaine_Text2);
            Fenetre.addMessage("Connection au serveur", false, false);
            
            
            
            // Instancier le client avec la gestion de la clé
            Client client = new Client();

            // Thread pour envoyer des messages
            Thread envoi = new Thread(() -> client.handleSending(out, pseudo, clientSocket1));

            // Thread pour recevoir des messages
            Thread recevoir = new Thread(() -> {
				try {
					client.handleReceiving(in, pseudo);
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

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
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */
    private void handleReceiving(BufferedReader in, String pseudo) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String msg;

        try {
            while ((msg = in.readLine()) != null) {
            	
                
                try {
                    // Déchiffrement du message
                	
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
    public static synchronized String getNextMessage() {
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
