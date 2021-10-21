import java.awt.event.ActionEvent;
import java.io.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.*;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Controller implements Initializable {
    public ListView<String> listView;
    public  ListView<String> listView1;
    public  TextField clientPath;
    public  TextField  servePath;
    public Button buttSend;
    public Button buttDown;
    public TextField input;
    private Path DIR_CLIENT= Paths.get("client-sep-2021", "rootForClient");
    private  Net net ;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            refreshClientView();
            addNavigationListeners();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
            net = Net.getInstance(cmd -> {
                switch (cmd.getType()) {
                    case FILE_MESSAGE:
                        FileMassage fileMessage = (FileMassage) cmd;
                        Files.write(
                                DIR_CLIENT.resolve(fileMessage.getName()),
                                fileMessage.getBytes()
                        );
                        refreshClientView();
                        break;
                    case LIST_RESPONSE:
                        ListResponse listResponse = (ListResponse) cmd;
                        refreshServerView(listResponse.getName());
                        break;
                    case PATH_RESPONSE:
                        PathResponse response = (PathResponse) cmd;
                        servePath.setText(response.getPath());
                        break;
                    default:
                        log.debug("Invalid command {}", cmd.getType());
                        break;
                }
            });
        }
    private void refreshClientView() throws  IOException{
        listView.getItems().clear();
//        clientPath.setText(DIR_CLIENT.toString());
        List<String> name = Files.list(DIR_CLIENT)
                .map(p->p.getFileName().toString())
                .collect(Collectors.toList());
            listView.getItems().addAll(name);
    }
    private void refreshServerView(List<String> name){
            listView1.getItems().clear();
            listView1.getItems().addAll(name);
    }

    public void sendFile(javafx.event.ActionEvent actionEvent) throws IOException {
        String fileName = input.getText();
        input.clear();
        Path file = Paths.get(String.valueOf(DIR_CLIENT.resolve(fileName)));
        net.sendCommand(new FileMassage(file));
    }
    public void download(javafx.event.ActionEvent actionEvent) throws IOException {
        String fileName = listView1.getSelectionModel().getSelectedItem();
        net.sendCommand(new FileRequest(fileName));
    }
    public void clientPathUp(javafx.event.ActionEvent actionEvent) throws IOException {
        DIR_CLIENT = DIR_CLIENT.getParent();
        clientPath.setText(DIR_CLIENT.toString());
        refreshClientView();
        net.sendCommand(new PathUpRequest());
    }
    public void serverPathUp(javafx.event.ActionEvent actionEvent) throws IOException {
        net.sendCommand(new PathUpRequest());
    }
    private void addNavigationListeners(){
        //клиентский list
        listView.setOnMouseClicked(e->{
            if(e.getClickCount()==2){
                String  item = listView.getSelectionModel().getSelectedItem();
                Path newpath = DIR_CLIENT.resolve(item);
                if(Files.isDirectory(newpath)){
                    DIR_CLIENT = newpath;
                    try {
                        refreshClientView();
                        clientPath.setText(DIR_CLIENT.toString());
                    }catch (IOException ioException){
                        ioException.printStackTrace();
                    }
                }
            }
        });
        //серверный list
        listView1.setOnMouseClicked(e->{
            String  item = listView1.getSelectionModel().getSelectedItem();
            if(e.getClickCount()==2){
                net.sendCommand(new PathInRequest(item));
                    } else {
                input.setText(item);
                    }
        });
    }
}
