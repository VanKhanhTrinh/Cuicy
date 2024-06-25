package com.example.cuicy;
import com.example.cuicy.Client.ClientHandler;
import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Server {
    private BufferedReader reader;
    private PrintWriter writer;
    private DataOutputStream dataOutputStream;

    private ServerSocket serverSocket1;
    private ServerSocket serverSocket2;
    private Socket socket;
    private InputStreamReader inputStreamReader;
    private static Server server;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server() throws IOException {
        serverSocket1 = new ServerSocket(3001);
        serverSocket2 = new ServerSocket(3002);
    }
    public static Server getInstance() throws IOException {
        return server != null ? server : (server = new Server());
    }
    public void makeSocket2() {
        while (!serverSocket2.isClosed()) {
            try {
                socket2 = serverSocket2.accept();
                writer = new PrintWriter(new OutputStreamWriter(socket2.getOutputStream(), StandardCharsets.UTF_8), true);
                Connection();
                inputStreamReader = new InputStreamReader(socket2.getInputStream());
                while (true) {
                    sendData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Socket socket2;
    public void makeSocket1() {
        while (!serverSocket1.isClosed()) {
            try {
                socket = serverSocket1.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void makeSocket(){
        Thread serverThread1 = new Thread(() -> makeSocket2());
        serverThread1.start();
        Thread serverThread2 = new Thread(() -> makeSocket1());
        serverThread2.start();
    }
    int i=0;
    public String sql_user;
    public String sql_pass;
    private String[] messages = new String[100];
    public void Connection() {
        try {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Statement statement = databaseConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM chat.users;");
            while (resultSet.next()){
            sql_user= resultSet.getString(2).trim();
            sql_pass= resultSet.getString(3).trim();
            messages[i] = getMd5(sql_user) + getMd5(sql_pass);
            i++;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    public void sendData() throws IOException {
        boolean p = true;
        char[] buffer = new char[1024];
        int bytesRead = inputStreamReader.read(buffer);
        String receivedData = new String(buffer, 0, bytesRead);
        String trimmedData = receivedData.trim();
             for(int j=0; j<=i; j++) {
                 if (trimmedData.equals(messages[j])) {
                     writer.println("success");
                     p = false;
                     break;
                 }
             }
             if (p) writer.println("failure");
        }
    public static String getMd5(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}



