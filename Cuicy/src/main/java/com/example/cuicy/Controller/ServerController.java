package com.example.cuicy;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerController {

    public Button LoginButton;
    private ScrollPane scrollPain = new ScrollPane();

    private static VBox staticVBox;
    private Server server;
    public javafx.scene.layout.VBox VBox;

    public void initialize() {
        staticVBox = VBox;
        receiveMessage("Sever Starting..");
        VBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPain.setVvalue((Double) newValue);
            }
        });
        new Thread(() -> {
            try {
                server = Server.getInstance();
                server.makeSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        receiveMessage("Sever Running..");
        receiveMessage("Waiting for User..");
    }
    public static void receiveMessage(String msgFromClient){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(msgFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 15px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));

        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                staticVBox.getChildren().add(hBox);
            }
        });
    }
    @FXML
    public void onButton(ActionEvent event) throws IOException{
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
