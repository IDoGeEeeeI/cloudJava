package com;

import java.nio.file.Path;

public class FileRequest extends  Command{
    //верни файл с именем
    private final String fName;

    public FileRequest(Path path){
        fName = path.getFileName().toString();
    }

    public String getfName(){
        return fName;
    }

    public CommandType getComType(){
        return CommandType.FILE_REQUEST;
    }
}
