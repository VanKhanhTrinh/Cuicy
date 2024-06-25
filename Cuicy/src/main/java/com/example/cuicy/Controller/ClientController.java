package com.example.cuicy;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class ClientController {
    public Label lbname;
    public VBox vbox;
    private Socket socket;
    public Button sendButton;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    public  String clientName = "askhakg";
    public TextField inputField;

    public void initialize() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3001);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    Platform.runLater(() -> {
                        readXML();
                    });
                    while (socket.isConnected()){
                        String receivingMsg = dataInputStream.readUTF();
                        receiveMessage(receivingMsg, ClientController.this.vbox);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void shutdown() {
        com.example.cuicy.ServerController.receiveMessage(clientName+" left.");
    }
    @FXML
    public void sendButtonOnAction(ActionEvent actionEvent) {
        sendMsg(inputField.getText());
    }

    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()) {
            if (!msgToSend.matches(".*\\.(png|jpe?g|gif)$")) {
                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_LEFT);
                hBox.setPadding(new Insets(5, 5, 0, 10));
                Text text = new Text(clientName + ": " + msgToSend);
                text.setStyle("-fx-font-size: 14");
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-background-color: #9B8D93; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(1, 1, 1));
                hBox.getChildren().add(textFlow);
                HBox hBoxTime = new HBox();
                hBoxTime.setAlignment(Pos.CENTER_LEFT);
                hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                Text time = new Text(stringTime);
                time.setStyle("-fx-font-size: 8");
                hBoxTime.getChildren().add(time);
                vbox.getChildren().add(hBox);
                vbox.getChildren().add(hBoxTime);
                writeXML(clientName,msgToSend,stringTime);
                try {
                    dataOutputStream.writeUTF(clientName + "-" + msgToSend);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputField.clear();
            }
        }
    }
    private void getMsg(String name, String msg, String stringTime){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));
        Text text = new Text(name + ": " + msg);
        text.setStyle("-fx-font-size: 14");
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #9B8D93; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(1, 1, 1));
        HBox hBoxTime = new HBox();
        hBoxTime.setAlignment(Pos.CENTER_LEFT);
        hBoxTime.setPadding(new Insets(0, 5, 5, 10));
        Text time = new Text(stringTime);
        time.setStyle("-fx-font-size: 8");
        hBoxTime.getChildren().add(time);
        hBox.getChildren().add(textFlow);
        vbox.getChildren().add(hBox);
        vbox.getChildren().add(hBoxTime);
    }
    private static void writeXML(String clientName, String msgToSend, String stringTime){
        getElement("name", clientName);
        getElement("content", msgToSend);
        getElement("time", stringTime);
    }
    private void readXML(){
        int count = 1;
        String name=null; String msg=null; String stringTime=null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("chat.xml");
            Element rootElement = document.getDocumentElement();
            NodeList nodeList = rootElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    String tagName = element.getTagName();
                    String textContent = element.getTextContent();
                    if(tagName.equals("name")) name=textContent;
                    if(tagName.equals("content")) msg=textContent;
                    if(tagName.equals("time")) stringTime=textContent;
                    count++;
                }
                if(count==3){
                    getMsg(name,msg,stringTime);
                    count=1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void receiveMessage(String msg, VBox vbox) throws IOException {
            String name = msg.split("-")[0];
            String msgFromServer = msg.split("-")[1];
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));
            Text text = new Text(name + ": " + msgFromServer);
            text.setStyle("-fx-font-size: 14");
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #9B8D93; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(1, 1, 1));
            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_LEFT);
            hBoxTime.setPadding(new Insets(0, 5, 5, 10));
            String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Text time = new Text(stringTime);
            time.setStyle("-fx-font-size: 8");
            hBoxTime.getChildren().add(time);
            hBox.getChildren().add(textFlow);
             writeXML(name,msgFromServer,stringTime);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vbox.getChildren().add(hBox);
                    vbox.getChildren().add(hBoxTime);
                }
            });
        }
        public static void getElement(String name, String content){
            try {
                File inputFile = new File("chat.xml");
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(inputFile);
                Element rootElement = doc.getDocumentElement();
                Element newElement = doc.createElement(name);
                newElement.setTextContent(content);
                org.w3c.dom.Text whitespace = doc.createTextNode("\n");
                rootElement.appendChild(newElement);
                rootElement.appendChild(whitespace);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new FileOutputStream(inputFile));
                transformer.transform(source, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

