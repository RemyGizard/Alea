import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;


public class Serveur implements MouseListener {
	
	public static void main(String[] args) {
		
		JFrame window = new javax.swing.JFrame("Serveur");;
		ImageIcon board = new ImageIcon("images/board.png");
		Image icone = Toolkit.getDefaultToolkit().getImage("images/logo.png");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int hauteur = (int) screenSize.getHeight();
		int largeur = (int) screenSize.getWidth();
			
		window.setSize(new Dimension(800,800)); //(int)largeur*2/3,(int)hauteur*2/3));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(new JLabel(board));
		window.setVisible(true);
		window.setLocationRelativeTo(null);
		window.setIconImage(icone);
		window.pack();
					
		final ServerSocket serveurSocket ;
		final Socket clientSocket ;
		final BufferedReader in;
		final PrintWriter out;
		final Scanner sc = new Scanner(System.in);
		
		try {
			
			serveurSocket = new ServerSocket(6666);
			clientSocket = serveurSocket.accept();
			MulticastSocket socket = new MulticastSocket(4446);
			
			out = new PrintWriter(clientSocket.getOutputStream());
			in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
			
			Thread envoi = new Thread(new Runnable() {
				
				String msg;
				
				@Override
				public void run() {
					while (true) {
						msg = sc.nextLine();
						out.println(msg);
						out.flush();
					}
				}
			});
			
			envoi.start();
			
			Thread recevoir = new Thread(new Runnable() {
				String msg ;
				@Override
				public void run() {
					try {
							msg = in.readLine();
							
							// Tant que le client est connecté
							
							while (msg!=null) {
								System.out.println("Client: "+msg);
								out.flush();
								msg = in.readLine();
								if (msg.equals("bye")) { 
									out.close();
									clientSocket.close();
								}
							}
							
							// Sortir de la boucle si le client a déconecté
							
							System.out.println("Client déconecté");
							
							// Fermer le flux et la session socket
							
							out.close();
							clientSocket.close();
							serveurSocket.close();
					} catch (IOException e) {e.printStackTrace();}
				}
			});
			
			recevoir.start();
			
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}