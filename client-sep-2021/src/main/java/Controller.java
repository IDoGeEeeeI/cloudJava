import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Controller implements Initializable {
    public ListView<String> listView;
    public  ListView<String> listView1;
    public Button butSend;
    public Button butDown;
    public TextField input;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private  static  final byte [] buffer = new byte[1024];
    private static   String DIR_CLIENT = "client-sep-2021/rootForClient";
    private Net net;

    protected void receivedFile(ActionEvent actionEvent){
       String fileName = input.getText();
       input.clear();
       Path path = Paths.get(fileName);
       net.sendCommand(new FileRequest(path));

    }

    public void send(ActionEvent actionEvent) throws  Exception{
        String fileName = input.getText();
        input.clear();
        Path path = Paths.get((DIR_CLIENT), fileName);
        net.sendCommand(new FileMassage(path));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        net = Net.getInstance(s-> Platform.runLater(()-> listView.getItems().add(String.valueOf(s))));
            try{
                fileIn();
            }catch (IOException e){
              e.printStackTrace();
            }



//        try {
//            fileIn();
//            Socket socket = new Socket("localhost", 8189);
//            is = new ObjectDecoderInputStream(socket.getInputStream());
//            os = new ObjectEncoderOutputStream(socket.getOutputStream());
//            Thread daemon = new Thread(() -> {
//                try {
//                    while (true) {
//                        Command msg =(Command) is.readObject();
//                        // TODO: 29.09.2021
//                        switch (msg.getType()){
//                            // TODO: 29.09.2021
//                        }
//                        log.debug("received: {}", msg);
////                        Platform.runLater(() -> listView.getItems().add(msg));
//                    }
//                } catch (Exception e) {
//                    log.debug("exception while read from input stream");
//                }
//            });
//            daemon.setDaemon(true);
//            daemon.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        };
    }

//на клиенте
    private void fileIn()throws IOException{
        listView.getItems().clear();
        listView.getItems().addAll(
                Files.list(Paths.get(((DIR_CLIENT))))
                        .map(s->s.getFileName().toString())
                        .collect(Collectors.toList())
        );
        listView.setOnMouseClicked(e ->{
            if(e.getClickCount()==2) {
                String item = listView.getSelectionModel().getSelectedItem();
                input.setText(item);
            }
        });
        listView1.setOnMouseClicked(e ->{
            if(e.getClickCount()==2) {
                String item = listView1.getSelectionModel().getSelectedItem();
                input.setText(item);
            }
        });

    }
}
