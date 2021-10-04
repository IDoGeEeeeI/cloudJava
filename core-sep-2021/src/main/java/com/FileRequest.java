package com;

import java.nio.file.Path;

public class FileRequest extends  Command{
    //верни файл с именем
    private final String fName;

    public FileRequest(String name){
        this.fName=name;
    }

    public String getfName(){
        return fName;
    }

    public CommandType getComType(){
        return CommandType.FILE_REQUEST;
    }
}
