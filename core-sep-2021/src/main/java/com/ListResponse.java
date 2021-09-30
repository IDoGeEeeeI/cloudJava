package com;

import java.io.File;

public class ListResponse extends  Command{
    //список файлов на сервере
    public File[] getResourceFiles(){
        File dir = new File(getClass().getClassLoader().getResource("").getFile());
        File[] files = dir.listFiles();
        return  files;
    }

    public CommandType getComType(){
        return CommandType.LIST_RESPONSE;
    }
}
