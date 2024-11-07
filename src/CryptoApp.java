import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class CryptoApp {
    public static JTextArea keyArea = new JTextArea(2, 20);
    public static JTextArea messageArea = new JTextArea();
    static String clef_chiffer= "";

    public static void main(String[] args) {
        // Configuration de la fenêtre principale
        JFrame frame = new JFrame("Cryptage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        // Texte supplémentaire au-dessus des cadres
        JLabel instructionLabel = new JLabel("Entrez le message et la clé a crypter ou décrypter");
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 30));
        frame.add(instructionLabel, BorderLayout.NORTH);

        // Panneau principal pour le texte et les zones de saisie
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        // Zone de texte pour le message
        messageArea.setBackground(Color.LIGHT_GRAY);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Message",
                TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);
        messagePanel.setBackground(Color.DARK_GRAY);

        // Panneau contenant le titre et la zone de message
        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.setBackground(Color.DARK_GRAY);
        messageContainer.add(messagePanel, BorderLayout.CENTER);

        // Zone de texte pour la clé avec un panneau titre
        keyArea.setBackground(Color.LIGHT_GRAY);
        keyArea.setLineWrap(true);
        keyArea.setWrapStyleWord(true);
        JScrollPane keyScrollPane = new JScrollPane(keyArea);

        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Clé",
                TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
        keyPanel.add(keyScrollPane, BorderLayout.CENTER);
        keyPanel.setBackground(Color.DARK_GRAY);

        JPanel keyContainer = new JPanel(new BorderLayout());
        keyContainer.setBackground(Color.DARK_GRAY);
        keyContainer.add(keyPanel, BorderLayout.CENTER);

        mainPanel.add(messageContainer, BorderLayout.CENTER);
        mainPanel.add(keyContainer, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);

        JButton encryptButton = new JButton("Crypter");
        JButton decryptButton = new JButton("Décrypter");
        JButton saveButton = new JButton("Enregistrer");
        JButton loadButton = new JButton("Charger Message");
        JButton loadKeyButton = new JButton("Charger Clé");

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(loadKeyButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Actions pour les boutons
       
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = messageArea.getText();
                if (!input.isEmpty()) {
                	Des encodage = new  Des();
                    int[] message_crypter =  encodage.crypte(input);
                    keyArea.setText(ToString(encodage.recollage_bloc(encodage.tab_cles)));
                    clef_chiffer=encodage.bitsToString(encodage.recollage_bloc(encodage.tab_cles));

                  ;
                    messageArea.setText(encodage.bitsToString(message_crypter));
                    
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!keyArea.getText().isEmpty() && !messageArea.getText().isEmpty()) {
                	Des encodage = new  Des();
                	encodage.tab_cles=encodage.decoupage(Totab(    keyArea.getText()),48);
                	int[] messageCrypt = encodage.stringToBits(messageArea.getText());
                	
                	 messageArea.setText(encodage.decrypte(messageCrypt));
                }
            }
        });

        // Ajout d'un filtre pour afficher tous les fichiers dans JFileChooser
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Enregistrer le message crypté");
                

                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (FileWriter writer = new FileWriter(file + "_Message.txt")) {
                        writer.write(messageArea.getText());
                        JOptionPane.showMessageDialog(frame, "Message enregistré avec succès !");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Erreur lors de l'enregistrement.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    File file2 = fileChooser.getSelectedFile();
                    try (FileWriter writer = new FileWriter(file2 + "_Clef.txt")) {
                        writer.write(keyArea.getText());
                        JOptionPane.showMessageDialog(frame, "Clé enregistrée avec succès !");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Erreur lors de l'enregistrement.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Charger le fichier crypté");

                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder message = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            message.append(line).append("\n");
                        }
                        messageArea.setText(message.toString().trim());
                        JOptionPane.showMessageDialog(frame, "Message chargé avec succès !");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Erreur lors du chargement du fichier.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        loadKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Charger la clé");

                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder keyContent = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            keyContent.append(line).append("\n");
                        }
                        keyArea.setText(keyContent.toString().trim());
                        JOptionPane.showMessageDialog(frame, "Clé chargée avec succès !");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Erreur lors du chargement de la clé.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        frame.setVisible(true);
    }
    


    public static String ToString(int[] tab) {
        StringBuilder code = new StringBuilder();
        for (int bit : tab) {
            code.append(bit);
        }
        return code.toString();
    }

    public static int[] Totab(String message) {
        int[] code = new int[message.length()];
        for (int i = 0; i < message.length(); i++) {
            code[i] = Character.getNumericValue(message.charAt(i));
        }
        return code;
    }

  
 
}
