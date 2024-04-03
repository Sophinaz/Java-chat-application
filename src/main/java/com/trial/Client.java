package com.trial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Client implements MessageCallback{
    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    String username;
    ChatGUI gui;
    static ArrayList<String> users = new ArrayList<>();

    
    Client(Socket socket, String username, ChatGUI gui){
        try {
            this.gui = gui;
            // Set itself as a callback in the GUI
            gui.setMessageCallback(this);

            this.socket = socket;
            this.username = username;
            users.add(username);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            closeEverthing(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage(String messageToSend){
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"error at sendMessage, client " + e.getMessage());
            closeEverthing(socket, bufferedReader, bufferedWriter);
        }
    }
    
    public void sendMessage(boolean joined){
        // i think this part would be changed to impliment GUI
        if (joined){
            try {
                bufferedWriter.write(  "SERVER:JOINED");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,"error at send rejoin message, client " + e.getMessage());
                closeEverthing(socket, bufferedReader, bufferedWriter);
            }
        }else{
            try {
                bufferedWriter.write(  "SERVER:LEFT");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,"error at send disconnect message, client " + e.getMessage());
                closeEverthing(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable()
        {
            @Override
            public void run(){
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        gui.appendToPane(gui.discussionField, msgFromGroupChat);
                        //every client waits for the msg sent form client handler and print it out on their consol
                    } catch (Exception e) {
                        closeEverthing(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public ChatGUI geChatGUI(){
        return this.gui;
    }

    public void closeEverthing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (socket != null){
                socket.close();
            }
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageSent(String message) {
        // The callback from the GUI
        sendMessage(message);
    }
    @Override
    public void onDisconnect() {
        sendMessage(false);
    }

    @Override
    public void onReconnect(){
        sendMessage(true);
    }

    public String getUsername() {
        return this.username;
    }

}
