import java.io.*;
import java.net.*;
import java.util.*;

public class Serveur {
	
	// Initialization des variables importantes
	
    private static final int PORT = 3214;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Le serveur est prêt à recevoir des connexions...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { 						// Creation d'un serveur socket qui permet d'accepter les clients
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {e.printStackTrace();}
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {this.socket = socket;} 							// Construction d'un client qui s'est connecté

        public void run() {
        	
            try {
            	
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));		// Reception des informations du client		
                out = new PrintWriter(socket.getOutputStream(), true);							// Envoyeur des informations vers le client

                /**
                 * Demande de pseudo unique :
                 * - username : Lecture du nom d'utilisateur
                 * 		- Si le nom d'utilisateur est nul, on recommence la boucle et on redemande le nom d'utilisateur.
                 * 		- Sinon on 
                 */

                while (true) {
                    out.println("Veuillez entrer votre pseudo : ");
                    username = in.readLine();													// Lecture du nom d'utilisateur
                    if (username == null || username.trim().isEmpty()) continue;				// Si le nom d'utilisateur est vide, on recommence la boucle et on redemande la saisie
                    // Ajout d'un client dans le dictionnaire des clients
                    synchronized (clients) {
                        if (!clients.containsKey(username)) {
                            clients.put(username, out);
                            out.println("Bienvenue " + username + " !");
                            System.out.println("Utilisateur connecté : " + username);
                            break;
                        } else {out.println("Ce nom d'utilisateur est déjà pris. Essayez un autre.");}
                    }
                }

                // Lire les messages et les envoyer au bon destinataire

                String message;
                while ((message = in.readLine()) != null) {
                	
                	/**
                	 * Verifier si le message est un message privé.
                	 * Si il contient ":" , donc on le sépare par deux Strings:
                	 * - Le premier est le pseudo "targetUser" qu'on vise a lui envoyer un message
                	 * - Le deuxieme est le message "privateMessage" a envoyer
                	 */
                	
                    if (message.contains(":")) {
                        String[] parts = message.split(":", 2);
                        
                        /**
                         * Verifier si le message suit le bon format:
                         * S'il sagit d'un message separe par un seul ":" , On peut effectuer l'envoi en prive.
                         * Sinon on renvoie une erreure qui demande le bon format.
                         */
                        
                        if (parts.length == 2) {
                            String targetUser = parts[0].trim();
                            String privateMessage = parts[1].trim();

                            synchronized (clients) {
                            	
                            	/**
                            	 * Recherche de l'utilisateur a parler en prive:
                            	 * - targetWriter : le client avec le pseudo targetUser
                            	 * Si on trouve le client, on lui envoi le message
                            	 * Sinon on renvoie un message d'erreure qui indique que l'utilisateur est introuvable.
                            	 */
                            	
                                PrintWriter targetWriter = clients.get(targetUser);
                                if (targetWriter != null) {
                                    targetWriter.println(username + " (privé) : " + privateMessage);
                                } else {
                                    out.println("Utilisateur " + targetUser + " introuvable.");
                                }
                            }
                            
                        // Demande du bon format
                        } else {out.println("Format de message invalide. Utilisez : DESTINATAIRE:MESSAGE");}
                    } else {
                        // Diffuser à tout le monde si pas de destinataire specifique
                        synchronized (clients) {for (PrintWriter writer : clients.values()) {writer.println(username + " : " + message);}}
                    }
                }

            } catch (IOException e) {System.out.println("Connexion perdue avec " + username);

            } finally {
            	
                // Nettoyage après déconnexion
                try {socket.close();} catch (IOException e) {e.printStackTrace();}
                synchronized (clients) {clients.remove(username);}
                System.out.println("Utilisateur déconnecté : " + username);
                
            }
        }
    }
}