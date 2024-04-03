package com.trial;

import javax.swing.*;

import org.bson.Document;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SignUp extends JFrame {
    private JLabel greating;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton back;
    private JButton signUpButton;
    private JTextField userName;
    private JPasswordField passwordField;
    
    
    SignUp(SignIn signIn){
        this.setTitle("Sign Up");
        this.setSize(400, 500);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

         // Welcome label panel
         JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         titlePanel.setPreferredSize(new Dimension(400, 120)); // Set preferred size
         greating = new JLabel("Hello There!");
         greating.setFont(new Font("Arial", Font.BOLD, 30));
         titlePanel.add(greating);
         this.add(titlePanel);
 
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
         signUpButton = new CustomButton("Sign Up",new Color(78, 153, 245),Color.WHITE,20);
         signUpButton.setFocusPainted(false);
 
         back = new CustomButton("Back to Sign In",new Color(78, 153, 245),Color.WHITE,20);
         back.setFocusPainted(false);

         // Add ActionListener to Sign Up button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            String enteredUsername = userName.getText();
            String enteredPassword = new String(passwordField.getPassword());
            try {
                if ((enteredPassword.length() < 8)  || (enteredPassword.indexOf("*") == -1)){
                    JOptionPane.showMessageDialog(SignUp.this, "Password cannot be less than 8 and it has to contain atleast one * character.");
                }else{
                String checked = signIn.toServerConnector("NULL",enteredUsername, enteredPassword);
                if (checked.equals("DOESN'T EXIST")){
                    signIn.toServerConnector("REGISTER", enteredUsername, enteredPassword);
                    JOptionPane.showMessageDialog(SignUp.this, "Signed Up successfully. procced to login");
                    dispose();
                    signIn.setVisible(true);
                }else if (checked.equals("EMPTY")){
                    JOptionPane.showMessageDialog(SignUp.this, "Invalid input, enter a valid one.");
                }else if (checked.equals("SUCCESS") || checked.equals("FAILURE")){
                    JOptionPane.showMessageDialog(SignUp.this, "That username is already taken.");
                }}
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            }
        });
        back.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                signIn.setVisible(true);
                dispose();
            }
        });

         buttonsPanel.add(signUpButton);
         buttonsPanel.add(back);
         this.add(buttonsPanel);
    }
}