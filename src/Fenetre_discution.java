import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.util.logging.*;

public class Fenetre_discution extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel messagePanel; // Le panneau pour afficher les messages
	private JTextField messageField; // Champ pour entrer les messages
	private JButton sendButton; // Bouton pour envoyer le message
	private JScrollPane scrollPane; // La vue de défilement
	private boolean canLoadMoreMessages = true;
	private ArrayList<String> loadedMessages; // contient les informations des anciens
	private static String filePath = "fichier/memoire"; // chemin du fichier
	private int compteur_memoire = 0;
	public String idClient="200023";
	public static ArrayList<String> chaine_Text;

	public Fenetre_discution(ArrayList<String> chaine_Text2) {
		chaine_Text=chaine_Text2;
		loadMessagesFromFile();
		compteur_memoire = loadedMessages.size() - 1;

		setTitle("Chat App");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran

		// Panneau principal (panneau de message)
		messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS)); // Alignement vertical des messages
		scrollPane = new JScrollPane(messagePanel);
		add(scrollPane, BorderLayout.CENTER);

		// Panneau pour saisir un message
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());

		messageField = new JTextField();
		sendButton = new JButton("Envoyer");

		inputPanel.add(messageField, BorderLayout.CENTER);
		inputPanel.add(sendButton, BorderLayout.EAST);

		add(inputPanel, BorderLayout.SOUTH);

		// Action du bouton "Envoyer"
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		// Action lorsque la touche "Entrée" est pressée
		messageField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		scrollPane.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// TODO Auto-generated method stub
				if (e.getWheelRotation() < 0 && canLoadMoreMessages) {
					addOldMessage(); // Charger plus de messages
				}
			}
		});

		// Détecter si la souris est sur messagePanel
		messagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// Activer la possibilité de charger plus de messages uniquement si la souris
				// entre dans messagePanel
				if (!canLoadMoreMessages) {
					canLoadMoreMessages = true; // Réactiver la possibilité de charger des messages
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// On pourrait désactiver la possibilité de charger des messages lorsque la
				// souris quitte messagePanel
				canLoadMoreMessages = false;
			}
		});

		setVisible(true);
	}

	// Méthode pour envoyer un message
	private void sendMessage() {
		String message = messageField.getText();
		if (!message.isEmpty()) {
			addMessage(message); // Ajouter le message
			Ecriture_append(message);
			messageField.setText(""); // Réinitialiser le champ de texte
			messagePanel.revalidate();
			messagePanel.repaint();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

				}
			});

		}
	}

	// Méthode pour ajouter un message au bas du panneau
	public  void addMessage(String message) {
		chaine_Text.add(message);
		JLabel messageLabel = new JLabel(message);
		messageLabel.setOpaque(true); // Nécessaire pour changer la couleur de fond
		messageLabel.setBackground(generateRandomColor());
		messageLabel.setForeground(Color.WHITE); // Changer la couleur du texte

		// Faire en sorte que le JLabel occupe toute la largeur du panneau de messages
		messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		messageLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, messageLabel.getPreferredSize().height));

		// Ajouter le JLabel au panneau de messages
		messagePanel.add(messageLabel);

		// Rafraîchir l'affichage
		messagePanel.revalidate();
		messagePanel.repaint();
	}

	// Méthode pour ajouter un message "Bonjour" en haut
	public  void addOldMessage() {
		// Créer un JLabel avec le message
		if (compteur_memoire >= 0) {
			JLabel messageLabel = new JLabel(loadedMessages.get(compteur_memoire));
			compteur_memoire--;
			messageLabel.setOpaque(true); // Nécessaire pour changer la couleur de fond
			messageLabel.setBackground(generateRandomColor());
			messageLabel.setForeground(Color.WHITE); // Changer la couleur du texte

			// Faire en sorte que le JLabel occupe toute la largeur du panneau de messages
			messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			messageLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, messageLabel.getPreferredSize().height));

			// Ajouter le JLabel en haut du panneau de messages
			messagePanel.add(messageLabel, 0); // Le "0" permet de l'ajouter en haut de la liste

			// Rafraîchir l'affichage pour montrer le nouveau message en haut
			messagePanel.revalidate();
			messagePanel.repaint();

			// Déplacer le défilement vers le haut pour montrer le nouveau message
			JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
			verticalScrollBar.setValue(verticalScrollBar.getMinimum());
		}
	}

	/**
	 * Ouvrir le fichier et ajouter une ligne de texte
	 * 
	 * @param filename
	 * @param text
	 */
	public static void Ecriture_append(String text) {
		BufferedWriter bufWriter = null;
		FileWriter fileWriter = null;
		try {
			
			fileWriter = new FileWriter(filePath, true);
			bufWriter = new BufferedWriter(fileWriter);
			
			// Insérer un saut de ligne
			bufWriter.newLine();
			bufWriter.write(text);
			bufWriter.close();
		} catch (IOException ex) {
			 System.err.println("Erreur lors de l'écriture dans le fichier : " + ex.getMessage());
			    ex.printStackTrace();
		} finally {
			try {
				bufWriter.close();
				fileWriter.close();
			} catch (IOException ex) {
				 System.err.println("Erreur lors de l'écriture dans le fichier : " + ex.getMessage());
				    ex.printStackTrace();
			}
		}
	}

	// Méthode pour générer une couleur de fond aléatoire
	private Color generateRandomColor() {
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		return new Color(r, g, b);
	}

	public static void main(String[] args) {
		// Exécuter l'application dans le thread de l'interface graphique
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Fenetre_discution();
			}
		});
	}

	private void loadMessagesFromFile() {
		loadedMessages = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				loadedMessages.add(line); // Ajouter chaque ligne (message) dans la liste
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}
