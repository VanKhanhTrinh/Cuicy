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
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ResourceBundle;

public class LoginController{
    public TextField address_field;
    public Text error_text;
    public PasswordField password_field;
    private DataInputStream dataInputStream;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private String clientName = "Client";

    public Button login_button;

    public void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3001);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Client connected");
                    com.example.cuicy.ServerController.receiveMessage(clientName+" joined.");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @FXML
    public void onLogin(ActionEvent event) throws IOException {
       showClient();
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
