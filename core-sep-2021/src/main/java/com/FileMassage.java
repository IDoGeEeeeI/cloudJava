package com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMassage extends  Command {
    //файл
    private final String name;
    private final byte[] bytes;

    public FileMassage(Path path) throws IOException {
        name = path.getFileName().toString();
        bytes = Files.readAllBytes(path);

        }

    public String getName(){
        return name;
    }
    public byte[] getBytes(){
        return bytes;
    }


    public CommandType getComType(){
        return CommandType.FILE_MESSAGE;
    }

}
