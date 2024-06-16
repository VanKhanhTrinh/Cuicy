package com.example.cuicy;

import com.example.cuicy.ClientController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.ResourceBundle;


public class LoginController{
    public TextField address_field;
    public Text error_text;
    public PasswordField password_field;
    private DataInputStream dataInputStream;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private BufferedReader reader;
    private PrintWriter writer;

    public Button login_button;
    int i=0;
    private String heresy[] = new String[100];

    public void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3002);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    boolean f = true;
    Server server;
    @FXML
    public void onLogin(ActionEvent event) throws IOException {
        if(f){
            String a=reader.readLine();
            while(!a.equals("Hi"))
            {
                heresy[i]=a;
                i++;
                a=reader.readLine();
            }
            f=false;
        }
        boolean q = true;
        String b = getMd5(address_field.getText() + password_field.getText());
        for(int j=0; j<i; j++){
            if(b.equals(heresy[j])) {
                showClient();
                q = false;
                address_field.setText("");
                password_field.setText("");
            }
        }
        if(q) error_text.setText("Wrong password"); else error_text.setText("");
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
        ClientController controller = new ClientController();
        fxmlLoader.setController(controller);
        stage.setOnCloseRequest(windowEvent -> {
            controller.shutdown();
        });
        stage.show();
    }
}
