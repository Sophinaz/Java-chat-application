package com.trial;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class ChatGUI {
    JFrame mainFrame;
    JTextPane discussionField = new JTextPane();
    JTextField textArea = new JTextField();
    private JPanel buttons = new JPanel();
    JButton joinButton;
    JButton sendButton;
    JButton disconnectButton;
    private String oldMsg = "";
    private MessageCallback messageCallback;    
    private String usernameString;
    
    public ChatGUI(String usernameString){
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);
        this.usernameString = usernameString;

        mainFrame = new JFrame("Chat");  
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setSize(700, 500);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Discussion thread module
        discussionField.setBounds(15, 15, 660, 320);
        discussionField.setFont(font);
        discussionField.setMargin(new Insets(6, 6, 6, 6));
        discussionField.setEditable(false);
        JScrollPane discussionScroll = new JScrollPane(discussionField);
        discussionScroll.setBounds(15, 15, 660, 320);

        discussionField.setContentType("text/html");
        discussionField.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // Field message user input
        textArea.setBounds(25, 350, 510, 50);
        textArea.setFont(font);
        textArea.setMargin(new Insets(6, 6, 6, 6));
        final JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBounds(25, 350, 510, 50);

        // button send
        sendButton = new CustomButton("Send", new Color(78, 153, 245),Color.WHITE,20);
        sendButton.setFont(font);
        sendButton.setBounds(575, 410, 100, 35);

        // button Disconnect
        disconnectButton = new CustomButton("Disconnect", new Color(78, 153, 245),Color.WHITE,20);
        disconnectButton.setFont(font);
        disconnectButton.setBounds(25, 410, 130, 35);

        joinButton = new CustomButton("Join Back", new Color(78, 153, 245),Color.WHITE,20);
        joinButton.setFont(font);
        joinButton.setBounds(25, 410, 130, 35);

        buttons.setBounds(550,350,120,90);
        buttons.setLayout(new GridLayout(2,1,3,4));
        buttons.add(sendButton);
        buttons.add(disconnectButton);

        textArea.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
              if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                sendMessage();
              }
      
              // Get last message typed
              if (e.getKeyCode() == KeyEvent.VK_UP) {
                String currentMessage = textArea.getText().trim();
                textArea.setText(oldMsg);
                oldMsg = currentMessage;
              }
      
              if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                String currentMessage = textArea.getText().trim();
                textArea.setText(oldMsg);
                oldMsg = currentMessage;
              }
            }
          });

          sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
              sendMessage();
            }
          });

          disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                messageCallback.onDisconnect();
                // Disable discussion field, users list, and text area
                discussionField.setEnabled(false);
                textArea.setEnabled(false);

                discussionField.setBackground(Color.LIGHT_GRAY);
                textArea.setBackground(Color.LIGHT_GRAY);
        
                // Replace disconnect button with join back button
                buttons.remove(disconnectButton);
                buttons.add(joinButton);
        
                // Repaint the panel to reflect changes
                buttons.revalidate();
                buttons.repaint();
        
                // Inform the callback about the disconnect
            }
        });
        
        joinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
              messageCallback.onReconnect();
              // Enable discussion field, users list, and text area
              discussionField.setEnabled(true);
              textArea.setEnabled(true);

              discussionField.setBackground(Color.WHITE);
              textArea.setBackground(Color.WHITE);

              // Replace join back button with disconnect button
              buttons.remove(joinButton);
              buttons.add(disconnectButton);

              // Repaint the panel to reflect changes
              buttons.revalidate();
              buttons.repaint();
            }
        });        


          Container board = mainFrame.getContentPane();
          board.add(discussionScroll, board);
          board.add(textScroll, board);
          board.add(buttons, board);

          mainFrame.setVisible(true);
          setMessageCallback(messageCallback);
    }
    public void sendMessage() {
      String message = textArea.getText().trim();
      if (message.equals("")) {
        return;
      }
      if (message != null){
        messageCallback.onMessageSent(message);
      }
      this.oldMsg = message;
      textArea.requestFocus();
      textArea.setText(null);
      appendToPane(discussionField, "<b> YOU </b>" + ": " +message);
  }

  void appendToPane(JTextPane tp, String msg) {
    SwingUtilities.invokeLater(() -> {
      Document doc = tp.getDocument();
    if (doc instanceof HTMLDocument) {
        HTMLDocument htmlDoc = (HTMLDocument) doc;
        HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
        try {
            editorKit.insertHTML(htmlDoc, htmlDoc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(htmlDoc.getLength());
        } catch (Exception e) {
            System.out.println("Error at appendToPane");
            e.printStackTrace();
        }
    } else {
        // Handle plain text
        try {
            doc.insertString(doc.getLength(), msg + "\n", null);
            tp.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            System.out.println("Error at appendToPane");
            e.printStackTrace();
        }
    }
  });
}
  
  public void setMessageCallback(MessageCallback callback){
    messageCallback = callback;
  }

  

  public String getName(){
    return this.usernameString;
  }
  public static void main(String[] args) {
    ChatGUI chatGUI = new ChatGUI("null");
  }
}

