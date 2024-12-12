import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Classe principale pour l'interface graphique du client de discussion.
 * Permet d'envoyer et de recevoir des messages dans un environnement de chat.
 */
public class Fenetre_discution extends JFrame {

    private static final long serialVersionUID = 1L;

    // Panneau pour afficher les messages
    private JPanel messagePanel;

    // Champ pour saisir un message
    private JTextField messageField;

    // Bouton pour envoyer un message
    private JButton sendButton;

    // Vue de défilement pour le panneau des messages
    private JScrollPane scrollPane;

    // Historique des messages partagé
    public static ArrayList<String> chaine_Text;

    // Pseudo du client actuel
    public String pseudo_client = "";

    /**
     * Constructeur de la classe Fenetre_discution.
     * Initialise l'interface utilisateur et demande un pseudo au client.
     * 
     * @param chaine_Text2 Liste des messages partagés (synchronisée avec le serveur).
     */
    public Fenetre_discution(ArrayList<String> chaine_Text2) {
        chaine_Text = chaine_Text2;

        // Demande du pseudo au client tant qu'il n'est pas saisi
        while (pseudo_client.isEmpty()) {
            pseudo_client = JOptionPane.showInputDialog(null, "Entrez votre pseudo :", "Bienvenue", JOptionPane.PLAIN_MESSAGE);
        }

        // Configuration de la fenêtre principale
        setTitle("Chat App");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran

        // Panneau pour afficher les messages
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

        // Actions pour le bouton "Envoyer"
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Action pour la touche "Entrée" dans le champ de saisie
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);
    }

    /**
     * Méthode pour envoyer un message.
     * Ajoute le message à l'historique partagé si le champ de texte n'est pas vide.
     */
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chaine_Text.add(message); // Ajout du message à l'historique partagé
            messageField.setText(""); // Efface le champ de saisie
        }
    }

    /**
     * Méthode pour afficher un message dans l'interface graphique.
     * Différencie les messages en fonction de l'émetteur et du type (privé ou public).
     * 
     * @param message Le message à ajouter à l'interface.
     */
    public void addMessage(String message) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setOpaque(true); // Nécessaire pour changer la couleur de fond

        // Analyse du message pour déterminer le type (privé, public, système)
        String[] tete = message.split(" :");
        if (tete.length == 1 || message.equals("Commande des messages privé -> pseudo : message")) {
            messageLabel.setBackground(new Color(120, 200, 140)); // Messages système ou aide
        } else {
            if (tete[0].contains("privé")) {
                messageLabel.setBackground(new Color(220, 120, 140)); // Messages privés
            } else {
                if (tete[0].equals(pseudo_client)) {
                    messageLabel.setBackground(new Color(140, 140, 160)); // Messages envoyés par ce client
                } else {
                    messageLabel.setBackground(new Color(80, 80, 90)); // Messages des autres utilisateurs
                }
            }
        }

        messageLabel.setForeground(Color.WHITE); // Couleur du texte

        // Configuration de la largeur maximale pour s'adapter au panneau
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, messageLabel.getPreferredSize().height));

        // Ajout du message au panneau de messages
        messagePanel.add(messageLabel);

        // Rafraîchissement de l'affichage
        messagePanel.revalidate();
        messagePanel.repaint();

        // Défilement automatique vers le bas pour afficher le dernier message
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }
}
