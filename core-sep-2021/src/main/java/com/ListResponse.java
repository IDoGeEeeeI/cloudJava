package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListResponse extends  Command{
    //список файлов на сервере
    private final List<String> name;

    public ListResponse(Path path) throws IOException{
        name = Files.list(path)
                .map(p->p.getFileName().toString())
                .collect(Collectors.toList());
    }

    public  List<String> getName(){
        return name;
    }


    public CommandType getComType(){
        return CommandType.LIST_RESPONSE;
    }
}
