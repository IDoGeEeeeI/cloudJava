package com.nio;

import javafx.util.converter.LocalDateTimeStringConverter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;


public class Server {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer;
    private static Path ROOT = Paths.get("server-sep-2021", "root");

    public Server() throws IOException {
        buffer = ByteBuffer.allocate(256);
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(8189));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);


        while (serverChannel.isOpen()) {
            selector.select();

            Set<SelectionKey> Keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = Keys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isAcceptable()){
                   handeleAccept(key);
                }
                if(key.isReadable()){
                    handeleRead(key);
                }
                iterator.remove();
            }
        }
    }

    private void handeleRead(SelectionKey key) throws IOException {
        SocketChannel channel =(SocketChannel) key.channel();
        buffer.clear();
        int read;
        StringBuilder msg = new StringBuilder();
        while (true){
           read =  0;
           if(read == -1){
               channel.close();
               return;
           }
           read = channel.read(buffer);
           if(read==0){
               break;
            } else {
               buffer.flip();
               while (buffer.hasRemaining()){
                   msg.append((char) buffer.get());
               }
               buffer.clear();
           }
        }
//        String massage = msg.toString();
//        channel.write(ByteBuffer.wrap(("[" + LocalDateTime.now() + "] "+ massage).getBytes(StandardCharsets.UTF_8)));

        String massage  = msg.toString().trim();
        if(massage.equals("ls")){
            Path path = Paths.get("server-sep-2021", "root");
            try(DirectoryStream<Path> files = Files.newDirectoryStream(path)){
                for(Path paths : files)channel.write(ByteBuffer.wrap((paths.toString() + "\n").getBytes(StandardCharsets.UTF_8)));
            }
        } else if (massage.startsWith("cat")){
            Path path = Paths.get("server-sep-2021", "root");
            try{
                String fileName = massage.split(" ")[1];
                channel.write(ByteBuffer.wrap(getFileDataAsString(fileName).getBytes(StandardCharsets.UTF_8)));
            }catch (Exception e){
                channel.write(ByteBuffer.wrap(("Command wrong"+ "\n").getBytes(StandardCharsets.UTF_8)));
            }
        } else{
            channel.write(ByteBuffer.wrap(("Command wrong"+ "\n").getBytes(StandardCharsets.UTF_8)));
        }
    }

    private void handeleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);

    }
    private String getFileDataAsString(String fileName) throws IOException {
        if (Files.isDirectory(ROOT.resolve(fileName))) {
            return "[ERROR] Command Cat cannot be applied to " + fileName + "\n";
        } else {
            return new String(Files.readAllBytes(ROOT.resolve(fileName))) + "\n";
        }
    }
    public static void main(String[] args) throws  IOException {
        new Server();
    }

}
