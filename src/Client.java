import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.SwingUtilities;

public class Client {
	public static Fenetre_discution Fenetre ;
	public static ArrayList<String> chaine_Text = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		SecretKey secretKey = generateAESKey();
		final Socket clientSocket ;
		final BufferedReader in;
		final PrintWriter out;
		final Scanner sc = new Scanner(System.in);
		
		
		 
		try {
			Fenetre = new Fenetre_discution(chaine_Text); 
			Fenetre.addMessage("Conection au serveur");
			
			clientSocket = new Socket("127.0.0.69",4444);
			
			out = new PrintWriter(clientSocket.getOutputStream());
			in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
			Thread envoi= new Thread(new Runnable() {
				String msg;
				@Override
				public void run() {
					while(true) {
						msg = getNextMessage();;
						if (msg.length()>0) {
							System.out.println("Envoi du message au serveur : " + msg);
							try {
								out.println( encrypt(msg,secretKey));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							out.flush();
							System.out.println("Message envoyé avec succès.");
						}
						
					}
				}
			});
			
			envoi.start();
			
			Thread recevoir= new Thread(new Runnable() {
				String msg ;
				@Override
				public void run() {
					try {

						msg = in.readLine();
						
						// Tant que le client est connecté
						
						while (msg!=null) {
							System.out.println(msg);
							Fenetre.addMessage(msg);
							msg = in.readLine();
						}
						
						// Sortir de la boucle si le client a déconecté
						
						System.out.println("Serveur déconecté");
						
						// Fermer le flux et la session socket
						
						out.close();
						clientSocket.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			recevoir.start();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	public static String getNextMessage() {
		if (chaine_Text.size()!=0 ) {
			return chaine_Text.remove(0);
		}
		return "";
		
	}
	public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
	
	  // Décrypter un message
    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    // Générer une clé AES aléatoire
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    // Convertir une clé AES à partir d'un tableau de bytes
    public static SecretKey getKeyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }
}
