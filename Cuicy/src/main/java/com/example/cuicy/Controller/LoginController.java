package com.example.cuicy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginController {
    public TextField address_field;
    public Text error_text;
    public PasswordField password_field;
    private Socket socket;
    private InputStreamReader  reader;
    private PrintWriter writer;
    public Button login_button;
    int i=0;

    public void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3002);
                    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                    reader = new InputStreamReader(socket.getInputStream());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @FXML
    public void onLogin(ActionEvent event) throws IOException {
        String b = getMd5(address_field.getText()) + getMd5(password_field.getText());
        writer.println(b);
        System.out.println(b);
        char[] buffer = new char[1024];
        int bytesRead = reader.read(buffer);
        String receivedData = new String(buffer, 0, bytesRead);
        String trimmedData = receivedData.trim();
            if(trimmedData.equals("success")) {
                error_text.setText("");
                showClient();
                address_field.setText("");
                password_field.setText("");
        }
        else error_text.setText("Wrong password");
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
    public void showClient() throws IOException{
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
