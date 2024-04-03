package com.trial;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import org.bson.Document;

public class SignIn extends JFrame {
    JLabel welcomeLabel;
    JLabel usernameLabel;
    JLabel passwordLabel;
    CustomButton signInButton;
    CustomButton signUpButton;
    JTextField userName;
    JPasswordField passwordField;
    private Socket signSocket;
    private BufferedReader reader;
    private BufferedWriter writer;


    public SignIn(Socket signinSocket) {
        this.signSocket = signinSocket;
        this.setTitle("Sign In");
        this.setSize(400, 500);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        try {
            this.reader = new BufferedReader(new InputStreamReader(signSocket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(signSocket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("error creating reader and writer for sign in page");
            e.printStackTrace();

        }

        // Welcome label panel
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setPreferredSize(new Dimension(400, 120)); // Set preferred size
        welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomePanel.add(welcomeLabel);
        this.add(welcomePanel);

         // Username panel
         JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         usernamePanel.setPreferredSize(new Dimension(400, 20)); // Set preferred size
         userName = new JTextField(15);
         usernamePanel.add(new JLabel("Username"));
         usernamePanel.add(userName);
         this.add(usernamePanel);

        // Password panel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setPreferredSize(new Dimension(400, 50)); // Set preferred size
        passwordField = new JPasswordField(15);
        passwordPanel.add(new JLabel("Password"));
        passwordPanel.add(passwordField);
        this.add(passwordPanel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1,2,5,0));
        buttonsPanel.setPreferredSize(new Dimension(250, 30)); // Set preferred size
        signInButton = new CustomButton("Sign In",new Color(78, 153, 245),Color.WHITE,20);
        signInButton.setFocusPainted(false);

        
        signUpButton = new CustomButton("Sign Up", new Color(78, 153, 245),Color.WHITE,20);
        signUpButton.setFocusPainted(false);
        
         // Add ActionListener to Sign In button
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = userName.getText();
                String enteredPassword = new String(passwordField.getPassword());

                try {
                    String response = toServerConnector("NULL",enteredUsername, enteredPassword);
                    // Handle different responses
                    if ("SUCCESS".equals(response)) {
                        // Successful login, perform actions here
                        ChatGUI gui = new ChatGUI(enteredUsername);
                        Client client = new Client(new Socket("192.168.210.2", 8080), enteredUsername, gui);
                        client.listenForMessage();
                        JOptionPane.showMessageDialog(SignIn.this, "Login Successful");
                        dispose();
                    } else if ("FAILURE".equals(response) || "DOESN'T EXIST".equals(response)) {
                        JOptionPane.showMessageDialog(SignIn.this, "Wrong Credentials");
                    } else if ("EMPTY".equals(response)) {
                        JOptionPane.showMessageDialog(SignIn.this, "Username or password is empty");
                    }
                }catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(SignIn.this, "Couldn't connect to the server");
                        closeEverthing(signinSocket, reader, writer);
                        dispose();
                    }
            }
        });

        // Add ActionListener to Sign Up button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to switch to the Sign Up page
                setVisible(false);
                SignUp signUp = new SignUp(SignIn.this);
                signUp.setVisible(true);
            }
        });

        
        buttonsPanel.add(signInButton);
        buttonsPanel.add(signUpButton);
        this.add(buttonsPanel);
        
        setVisible(true);
    }

    public String toServerConnector(String register, String name, String password) throws IOException{
        // Send credentials to the server
        writer.write(register);
        writer.newLine();
        writer.flush();
        writer.write(name);
        writer.newLine();
        writer.flush();
        writer.write(password);
        writer.newLine();
        writer.flush();

        // Receive response from the server
        return reader.readLine();

    }

    public void closeEverthing(Socket socket, BufferedReader bufferedReader, BufferedWriter printWriter){
        try {
            if (socket != null){
                socket.close();
            }
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (printWriter != null){
                printWriter.close();
            }
        } catch (Exception e) {
            System.out.println("error at close everything");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws UnknownHostException, IOException {
        SignIn signIn = new SignIn( new Socket("192.168.210.2",9090));
    }

}
    