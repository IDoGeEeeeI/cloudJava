import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
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
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private Path DIR_CLIENT;
    private  Net net ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


//          net = Net.getInstance(cmd-> {
//        switch (cmd.getType()) {
//            case FILE_MESSAGE:
//                FileMassage fileMessage = (FileMassage) cmd;
//                Files.write(
//                        DIR_CLIENT.resolve(fileMessage.getName()),
//                        fileMessage.getBytes()
//                );
//                fileIn();
//                break;
//            case LIST_RESPONSE:
//                ListResponse listResponse = (ListResponse) cmd;
//                List<String> name = listResponse.getName();
//                fileInS(name);
//                break;
//            case PATH_RESPONSE:
//                PathResponse response = (PathResponse) cmd;
//                String path = response.getPath();
//                Platform.runLater(()-> servePath.setText(path));
//                break;
//            default:
//                log.debug("Invalid command {}", cmd.getType());
//                break;
//        }
//         } );


        try {
            DIR_CLIENT = Paths.get("client-sep-2021", "rootForClient");
            Socket socket = new Socket("localhost",8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            fileIn();
            addNavigationListeners();

        Thread thread = new Thread(() -> {
            try {
                while (true) {
//тут он сразу проскакивает на catch и в консоли выводит exception while......
                    Command cmd = (Command) is.readObject();
                    log.debug("received {}", cmd);
                    switch (cmd.getType()) {
                        case FILE_MESSAGE:
                            FileMassage fileMessage = (FileMassage) cmd;
                            Files.write(
                                    DIR_CLIENT.resolve(fileMessage.getName()),
                                    fileMessage.getBytes()
                            );
                            fileIn();
                            break;
                        case LIST_RESPONSE:
                            ListResponse listResponse = (ListResponse) cmd;
                            List<String> name = listResponse.getName();
                            fileInS(name);
                            break;
                        case PATH_RESPONSE:
                            PathResponse response = (PathResponse) cmd;
                            String path = response.getPath();
                            Platform.runLater(()-> servePath.setText(path));
                            break;
                        default:
                            log.debug("Invalid command {}", cmd.getType());
                            break;
                    }
                }
            } catch (Exception e) {
                log.debug("exception while read from input stream");
            }
        });
        thread.setDaemon(true);
        thread.start();

            
    }catch(IOException ioException){
        log.debug("exception", ioException);
    }
}

    private void refreshClientView() throws  IOException{
        clientPath.setText(DIR_CLIENT.toString());
        List<String> name = Files.list(DIR_CLIENT)
                .map(p->p.getFileName().toString())
                .collect(Collectors.toList());
        Platform.runLater(()-> {
            listView.getItems().clear();
            listView.getItems().addAll(name);
        });
    }
    private void refreshServerView(List<String> name){
        Platform.runLater(()->{
            listView1.getItems().clear();
            listView1.getItems().addAll(name);
                });
    }


    public void download(javafx.event.ActionEvent actionEvent) throws IOException {
        String fileName = listView1.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(fileName));
        os.flush();
//////+
        net.sendCommand(new FileRequest(fileName));
    }

    public void upload(javafx.event.ActionEvent actionEvent) throws IOException {
        String fileName = listView.getSelectionModel().getSelectedItem();
        FileMassage massage = new FileMassage(DIR_CLIENT.resolve(fileName));
        os.writeObject(massage);
        os.flush();
//////+
        net.sendCommand(new FileMassage(DIR_CLIENT.resolve(fileName)));
    }
    // выскакивает exception при нажатии + также не отображает path server (пуслой лист1)
    public void clientPathUp(javafx.event.ActionEvent actionEvent) throws IOException {
        DIR_CLIENT = DIR_CLIENT.getParent();
        clientPath.setText(DIR_CLIENT.toString());
        refreshClientView();
    }
    public void serverPathUp(javafx.event.ActionEvent actionEvent) throws IOException {
        os.writeObject(new PathUpRequest());
        os.flush();
    }
//на клиенте
    private void fileIn()throws IOException {
        listView.getItems().clear();
        listView.getItems().addAll(
                Files.list(Paths.get(String.valueOf(DIR_CLIENT)))
                        .map(s -> s.getFileName().toString())
                        .collect(Collectors.toList())
        );
    }
    private  void fileInS(java.util.List<String> name)throws  IOException{
        Platform.runLater(()->{
            listView1.getItems().clear();
            listView1.getItems().addAll(name);
        });
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
                        fileIn();
                    }catch (IOException ioException){
                        ioException.printStackTrace();
                    }
                }
            }
        });
        //серверный list
        listView1.setOnMouseClicked(e->{
                    if(e.getClickCount()==2){
                        String  item = listView1.getSelectionModel().getSelectedItem();
                        try {
                            os.writeObject(new PathInRequest(item));
                            os.flush();
                        }catch (IOException ioException){
                            ioException.printStackTrace();
                        }

                    }
        });
    }




//        listView.setOnMouseClicked(e ->{
//            if(e.getClickCount()==2) {
//                String item = listView.getSelectionModel().getSelectedItem();
//                input.setText(item);
//            }
//        });



////если файл отправили на сервер, то он выводится на лис у серв(он добавляется в папку)
//        //наверное нужно его потом удалять, но я еще хз будет ли он потом пропадать с лис серва

//        listView1.getItems().clear();
//        listView1.getItems().addAll(
//                Files.list(Paths.get(DIR_SEND_TO_SERV))
//                        .map(s->s.getFileName().toString())
//                        .collect(Collectors.joining())
//        );
//        listView1.setOnMouseClicked(e ->{
//            if(e.getClickCount()==2) {
//                String item = listView1.getSelectionModel().getSelectedItem();
//                input.setText(item);
//            }
//        });
//
//    }
}
