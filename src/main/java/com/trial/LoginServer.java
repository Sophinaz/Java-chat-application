package com.trial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class LoginServer {

    private ServerSocket serverSocket;
    private MongoCollection<Document> userCollection;

    public LoginServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.userCollection = initializeMongoDB();
    }

    private MongoCollection<Document> initializeMongoDB() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("chatdb");
        return database.getCollection("users");
    }

    public void startServer() {
        try {
            System.out.println("Login server is now open on port " + serverSocket.getLocalPort() + ".");
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new LoginHandler(clientSocket, userCollection)).start();
            }
        } catch (IOException e) {
            System.out.println("error creating login server");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        LoginServer loginServer = new LoginServer(9090);
        loginServer.startServer();
    }
}
