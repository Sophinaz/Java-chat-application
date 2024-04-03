package com.trial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class LoginHandler implements Runnable {

    private Socket clientSocket;
    private MongoCollection<Document> userCollection;
    public static ArrayList<LoginHandler> loginers = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public LoginHandler(Socket clientSocket, MongoCollection<Document> userCollection) {
        try {
            this.clientSocket = clientSocket;
            this.userCollection = userCollection;
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            loginers.add(this);
        } catch (IOException e) {
            System.out.println("error on initalizing loginhandler");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String register;
        String username;
        String password;
        
        while (clientSocket.isConnected()){
            try {
                register = bufferedReader.readLine();
                username = bufferedReader.readLine();
                password = bufferedReader.readLine();
                if (register.equals("NULL")){
                    if (username.equals("") || password.equals("")){
                        this.bufferedWriter.write("EMPTY");//change to all capital EMPTY #lastchanges
                        this.bufferedWriter.newLine();
                        this.bufferedWriter.flush();
                    }else if (authenticate(username, password).equals("DOESN'T EXIST")){
                        this.bufferedWriter.write("DOESN'T EXIST");
                        this.bufferedWriter.newLine();
                        this.bufferedWriter.flush();
                    }else if (authenticate(username, password).equals("FAILURE") ){
                        this.bufferedWriter.write("FAILURE");
                        this.bufferedWriter.newLine();
                        this.bufferedWriter.flush();
                    }else{
                        this.bufferedWriter.write("SUCCESS");
                        this.bufferedWriter.newLine();
                        this.bufferedWriter.flush();
                    }
                }else{
                    Document newUser = new Document("username",username)
                                        .append("password", password);
                userCollection.insertOne(newUser);
                    bufferedWriter.write("DONE");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                }

            } catch (IOException e) {
                System.out.println("error at login check");
                closeEverthing(clientSocket,bufferedReader,bufferedWriter);
                break;
            }
        }   
        }

    private String authenticate(String username, String password) {
        Document result = userCollection.find(new Document("username", username)).first();
        if (result == null){
            return "DOESN'T EXIST";
        }else if (result.getString("password").equals(password)){
            return "SUCCESS";
        }else{
            return "FAILURE";
        }
    }

    public void removeloginhandler(){
        loginers.remove(this);
    }

    public void closeEverthing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeloginhandler();
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
            System.out.println("error from close everything");
            e.printStackTrace();
        }
    }
}
