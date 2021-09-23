package com.test.client;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Controller implements Initializable {
    public ListView<String> listView;
    public TextField input;
    private DataInputStream is;
    private DataOutputStream os;
    private  static  final byte [] buffer = new byte[1024];
    private static    String DIR_CLIENT = "client-sep-2021/rootForClient";

    protected void sendFile(String name) throws IOException {
        Path file = Paths.get(DIR_CLIENT, name);
        long size = Files.size(file);
        os.writeUTF(name);
        os.writeLong(size);
        InputStream fileStream =Files.newInputStream(file);
        int read;
        while ((read = fileStream.read(buffer))!=-1){
            os.write(buffer,0,read);
            os.flush();
        }
    }

    public void send(ActionEvent actionEvent) throws  Exception{
        String fileName = input.getText();
        input.clear();
        sendFile(fileName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            fileIn();
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread daemon = new Thread(() -> {
                try {
                    while (true) {
                        String msg = is.readUTF();
                        log.debug("received: {}", msg);
                        Platform.runLater(() -> listView.getItems().add(msg));
                    }
                } catch (Exception e) {
                    log.debug("exeption while read from imput stream");
                }
            });
            daemon.setDaemon(true);
            daemon.start();
        } catch (Exception e) {
            e.printStackTrace();
        };
    }


    private void fileIn()throws IOException{
        listView.getItems().clear();
        listView.getItems().addAll(
                Files.list(Paths.get(DIR_CLIENT))
                        .map(s->s.getFileName().toString())
                        .collect(Collectors.toList())
        );
        listView.setOnMouseClicked(e ->{
            if(e.getClickCount()==2) {
                String item = listView.getSelectionModel().getSelectedItem();
                input.setText(item);
            }
        });
    }
}
