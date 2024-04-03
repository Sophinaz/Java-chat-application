package com.trial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection messageCollection;

    ClientHandler(Socket socket){
        try{
            this.mongoClient = MongoClients.create("mongodb://localhost:27017");
            this.database = mongoClient.getDatabase("chatdb");
            this.messageCollection = database.getCollection("messages");

            this.socket = socket;
            //socket.getInputStream gives us a byte stream so we wrapp it with InputStreamReader to character stream, the same with the output stream
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();//this would be accepted from the user as they sign in  
            clientHandlers.add(this);
            broadcastMessage("<b>SERVER: </b>" +this.clientUsername+ "<b> has entered the chat! </b>");
        }catch (IOException e){
            System.out.println("error at ClientHandler constructor");
            closeEverthing(socket, bufferedReader, bufferedWriter);
        }
    }
    
    @Override
    public void run(){
        //Everything in here is going to be run on a separate thread
        String messageFromClient;

        ArrayList<Document> sortedMessages = (ArrayList<Document>) messageCollection.find().into(new ArrayList<>());
        Collections.sort(sortedMessages, Comparator.comparingLong(doc -> doc.getLong("timestamp"))); // 1 for ascending, -1 for descending

        for (Document document : sortedMessages) {
              String msg = document.getString("message");
              String sender = document.getString("sender");
              try {
                this.bufferedWriter.write("<b> "+sender+": </b>" + msg );
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            } catch (IOException e) {
                System.out.println("error at retirving old messages, clienthandler");
                e.getMessage();
            }
        }

        while (socket.isConnected()) {
            try{
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.equals("SERVER:JOINED")){
                    broadcastMessage("<b>SERVER: </b>" + clientUsername + "<b> has joined the chat.</b>");
                }else if(messageFromClient.equals("SERVER:LEFT")){
                    broadcastMessage("<b>SERVER: </b>" + clientUsername + "<b> has left the chat.</b>");
                }else{
                    // Store the message in MongoDB
                    Document document = new Document("sender",this.clientUsername)
                    .append("message", messageFromClient)
                    .append("timestamp", System.currentTimeMillis());
                        messageCollection.insertOne(document);
                    broadcastMessage("<b>" + clientUsername + ":</b> " + messageFromClient);
                }
            } catch(IOException e){
                closeEverthing(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        // if (messageToSend.charAt(0)==('@')){
        //     String[] splitedStrings = messageToSend.split(" ");
        //     StringBuilder resultBuilder = new StringBuilder();
        //     for (int i = 1; i < splitedStrings.length; i++) {
        //         resultBuilder.append(splitedStrings[i]);
        //     }
        //     String nowuser = splitedStrings[0].substring(1, splitedStrings[0].length()+1);
        //     for (ClientHandler clientHandler : clientHandlers){
        //         try{
        //             if (clientHandler.clientUsername.equals(nowuser)){
        //                 clientHandler.bufferedWriter.write(messageToSend + " "+ nowuser);
        //                 clientHandler.bufferedWriter.newLine();// newline character: this is to finish the line because the clients would be expecting a newline()
        //                 clientHandler.bufferedWriter.flush(); // to small to fill the buffer so we flush it
        //             }
        //         }catch(IOException e){
        //             System.out.println("error at broadcastMessage");
        //             closeEverthing(socket, bufferedReader, bufferedWriter);
        //         }
        //     }

        // }else{
        for (ClientHandler clientHandler : clientHandlers){
            try{
                if (!clientHandler.clientUsername.equals(this.clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();// newline character: this is to finish the line because the clients would be expecting a newline()
                    clientHandler.bufferedWriter.flush(); // to small to fill the buffer so we flush it
                }
            }catch(IOException e){
                System.out.println("error at broadcastMessage");
                closeEverthing(socket, bufferedReader, bufferedWriter);
            }
        }}
    

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + this.clientUsername + " has left the chat!");
    }

    public void closeEverthing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
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
            System.out.println("error at close everything, clienthandler");
            e.getMessage();
        }
    }

}
