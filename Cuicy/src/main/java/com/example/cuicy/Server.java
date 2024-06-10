package com.example.cuicy;

import com.example.cuicy.Client.ClientHandler;
import com.example.cuicy.LoginController;
import com.example.cuicy.ServerController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;

import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private static Server server;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server() throws IOException {
        serverSocket = new ServerSocket(3001);
    }
    public static Server getInstance() throws IOException {
        return server != null ? server : (server = new Server());
    }
    public void initialize(){
    }
    public void makeSocket() {
        while (!serverSocket.isClosed()) {
            try {
                socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                System.out.println("client socket accepted " + socket.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public DatabaseConnection databaseConnection = new DatabaseConnection();
    public String sql_user;
    public String sql_pass;
    public void Connection() {
        try {
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM chat.users;");
            while (resultSet.next()) {
                 sql_user = resultSet.getString(2).trim();
                 sql_pass = resultSet.getString(3).trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }



