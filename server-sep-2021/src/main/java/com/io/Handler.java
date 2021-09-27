package com.io;

import java.io.*;
import java.net.Socket;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Handler implements  Runnable{

    private static final int BUFFER_SIZE = 1024;
    private  final Socket socket;
    public  Handler(Socket socket){
        this.socket = socket;
    }
    public  Socket getSocket(){
        return  socket;
    }
    private static   byte[] buffer = new byte[BUFFER_SIZE];
    private static  final String ROOT_DIR = "server-sep-2021/root";


    @Override
    public void run() {
        try(DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());
        ){
            while (true){
                String fileName = is.readUTF();
                log.debug("Received: {}", fileName);
                long size = is.readLong();
                log.debug("Received: {}",size);
                int read;
                 try(OutputStream fos = Files.newOutputStream(Paths.get(ROOT_DIR, fileName))){
                     for(int i = 0; i<(size+BUFFER_SIZE-1)/ BUFFER_SIZE;i++) {
                         read = is.read(buffer);
                         fos.write(buffer, 0, read);
                     }
                 }catch (Exception e){
                     log.debug("problem with file system or memory");
                 }
            }
        }
        catch (Exception e){
            log.error("stacktrace: ", e);
        }
    }
}
