import gals.*;
import structures.Symbol;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author eas
 */
public class MainWindow extends javax.swing.JFrame {

    Lexico lexico;
    Sintatico sintatico;
    Semantico semantico;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        sourceInput = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        console = new javax.swing.JTextArea();
        buttonCompile = new javax.swing.JButton();
        JMenuBar menubar = getMenubar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IDE");
        setFont(new java.awt.Font("Andale Mono", 0, 14)); // NOI18N
        setJMenuBar(menubar);


        sourceInput.setColumns(20);
        sourceInput.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        sourceInput.setRows(5);
        jScrollPane1.setViewportView(sourceInput);

        console.setEditable(false);
        console.setColumns(20);
        console.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        console.setLineWrap(true);
        console.setRows(5);
        console.setTabSize(4);
        jScrollPane2.setViewportView(console);

        buttonCompile.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        buttonCompile.setText("Compilar");
        buttonCompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCompileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                                        .addComponent(jScrollPane2)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(buttonCompile)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(buttonCompile))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private JMenuBar getMenubar() {
        JMenuBar menubar = new JMenuBar();

        JMenu menuFile = new JMenu("Arquivo");
        JMenuItem newFile = new JMenuItem("Novo");
        JMenuItem openFile = new JMenuItem("Abrir");
        JMenuItem saveFile = new JMenuItem("Salvar");

        newFile.addActionListener(evt -> {
            sourceInput.setText("");
        });

        openFile.addActionListener(evt -> {
            sourceInput.setText(openNewFileAndRead());
        });

        saveFile.addActionListener(evt -> saveFile(sourceInput.getText()));

        menuFile.add(newFile);
        menuFile.add(openFile);
        menuFile.add(saveFile);

        JMenuItem bipCodeMenu = new JMenuItem("Código BIP");
        bipCodeMenu.addActionListener(evt -> {
            showBipCode();
        });
        JMenuItem symbolsTableMenu = new JMenuItem("Tabela de Símbolos");
        symbolsTableMenu.addActionListener(evt -> {
            showSymbolicTable();

        });
        menubar.add(menuFile);
        menubar.add(symbolsTableMenu);
        menubar.add(bipCodeMenu);

        return menubar;
    }

    private void showBipCode() {
        if (semantico == null) {
            JOptionPane.showMessageDialog(null, "Primeiro compile o código", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFrame frame = new JFrame("Código BIP");

        // Create column names for the table
        String text = ".data\n\t";
        text += String.join("\n\t", semantico.codesData);
        text += "\n\n.text\n\t";
        text += semantico.codesText.stream().map(it -> !it.startsWith("R") ? "\n\t" + it : "\n" + it).collect(Collectors.joining());

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setFont(new Font("Helvetica Neue", 0, 14)); // NOI18N
        textArea.setLineWrap(true);
        textArea.setRows(30);
        textArea.setTabSize(4);

        // Optionally, create a JScrollPane and add the table to it for scrolling
        JScrollPane scrollPane = new JScrollPane();

        scrollPane.setViewportView(textArea);
        // Add the JScrollPane (or table directly) to the JFrame's content pane
        frame.getContentPane().add(scrollPane);
        JMenuBar menuBarBipCode = new JMenuBar();
        JMenuItem menuItemSave = new JMenuItem("Salvar Código BIP");
        String finalText = text;
        menuItemSave.addActionListener(e -> saveFile(finalText));

        menuBarBipCode.add(menuItemSave);
        frame.setJMenuBar(menuBarBipCode);

        // Set JFrame properties
        frame.setSize(1000, 400); // Set size
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE); // Close operation
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true); // Set visibility
    }

    private void showSymbolicTable() {
        if (semantico == null) {
            JOptionPane.showMessageDialog(null, "Primeiro compile o código", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFrame frame = new JFrame("Tabela de Símbolos");

        // Create column names for the table
        String[] columnNames = {"Id", "Tipo Declaração", "Tipo", "Escopo", "Inicializado", "Usado", "Tipo de Id", "Valor", "Tamanho Array", "Valores Array"};

        Object[][] data = semantico.symbolsTable.stream()
                .map(symbol -> {
                    Class<?> cls = symbol.getClass();
                    Field[] fields = cls.getDeclaredFields();
                    return Stream.of(fields)
                            .map(field -> {
                                field.setAccessible(true);
                                try {
                                    return field.get(symbol);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            })
                            .toArray(Object[]::new);
                })
                .toArray(Object[][]::new);

        // Create a DefaultTableModel to hold the data
        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        // Create a JTable with the DefaultTableModel
        JTable table = new JTable(model);

        // Optionally, create a JScrollPane and add the table to it for scrolling
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the JScrollPane (or table directly) to the JFrame's content pane
        frame.getContentPane().add(scrollPane);

        // Set JFrame properties
        frame.setSize(1000, 400); // Set size
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE); // Close operation
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true); // Set visibility
    }

    private void buttonCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCompileActionPerformed

        lexico = new Lexico();
        sintatico = new Sintatico();
        semantico = new Semantico();

        lexico.setInput(sourceInput.getText());
        console.setText("");
        try {
            sintatico.parse(lexico, semantico);
            if (!semantico.warnings.isEmpty()) {
                console.setText("WAR:" + String.join("\nWAR:", semantico.warnings));
            }
            semantico.codesData.addAll(semantico.symbolsTable.stream().map(symbol -> {
                if (symbol.getIdType().equals(Symbol.IdType.ARRAY)) {
                    ArrayList<String> zeroArray = new ArrayList<>(Collections.nCopies(symbol.getArraySize(), "0"));
                    for (int i = 0; i < symbol.getValues().size(); i++) {
                        zeroArray.set(i, symbol.getValues().get(i).toString());
                    }
                    return symbol.getId() + ": .word " + String.join(", ", zeroArray);
                } else {
                    return symbol.getId() + ": 0";
                }
            }).toList());
            console.setText(console.getText() + "Compiled successfully!");

        } catch (LexicalError e) {
            console.setText("Lexical Error: " + e.getMessage());
        } catch (SyntaticError e) {
            console.setText("Syntax Error: " + e.getMessage());
        } catch (SemanticError e) {
            console.setText("Semantic Error: " + e.getMessage());
        } catch (Error e) {
            console.setText("Error: " + e);
        } catch (Exception e) {
            console.setText("Exception: " + e);
            throw new RuntimeException(e);
        } finally {
            // TODO: Show warnings in cosole
        }

    }//GEN-LAST:event_buttonCompileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }

    public String openNewFileAndRead() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                return content.toString();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao ler o arquivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        return "";
    }

    public void saveFile(String content) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(selectedFile);
                writer.write(content);
                writer.close();
                JOptionPane.showMessageDialog(null, "Arquivo " + selectedFile.getName() + " salvo com sucesso!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCompile;
    private javax.swing.JTextArea console;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea sourceInput;
    private MenuBar menuBar;
    // End of variables declaration//GEN-END:variables
}
