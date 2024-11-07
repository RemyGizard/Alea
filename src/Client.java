import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		final Socket clientSocket ;
		final BufferedReader in;
		final PrintWriter out;
		final Scanner sc=new Scanner(System.in);
		 
		try {
			clientSocket = new Socket("127.0.0.69",4444);
			out = new PrintWriter(clientSocket.getOutputStream());
			in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
			Thread envoi= new Thread(new Runnable() {
				String msg;
				@Override
				public void run() {
					while(true) {
						msg = sc.nextLine();
						out.println(msg);
						out.flush();
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
	
	public void envoi(String s) {
		
	}

}
