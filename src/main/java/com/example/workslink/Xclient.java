package com.example.workslink;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Xclient extends Application {
    private PrintWriter out;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        TextArea chatTextArea = new TextArea();
        chatTextArea.setPrefWidth(374);
        chatTextArea.setPrefHeight(569);
        chatTextArea.setEditable(false);

        ScrollPane scrollPane = new ScrollPane(chatTextArea);
        root.setCenter(scrollPane);

        TextField messageInputField = new TextField();
        messageInputField.setPrefWidth(335);
        messageInputField.setPrefHeight(36);
        messageInputField.setPromptText("Type your message here...");




        Button sendButton = new Button(">");
        sendButton.setPrefHeight(35);
        sendButton.setOnAction(event -> sendMessage(messageInputField));

        BorderPane bottomPane = new BorderPane();
        bottomPane.setLeft(messageInputField);
        bottomPane.setRight(sendButton);
        root.setBottom(bottomPane);

        Scene scene = new Scene(root, 377, 597);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Connect to the server
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    Scanner in = new Scanner(socket.getInputStream());
                    while (in.hasNext()) {
                        String receivedMessage = in.nextLine();
                        Platform.runLater(() -> chatTextArea.appendText(receivedMessage + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setOnCloseRequest(event -> {
            if (out != null) {
                out.close();
            }
        });
    }

    private void sendMessage(TextField messageInputField) {
        String message = messageInputField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            messageInputField.clear();
        }
    }





}

