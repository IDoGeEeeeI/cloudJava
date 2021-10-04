package com;



public class PathResponse extends  Command{
    //в какой директории сейчас находится сервер
    private final String path;

    public PathResponse(String path){
        this.path=path;
    }
    public String getPath(){
        return path;
    }

    public CommandType getType(){
        return CommandType.PATH_RESPONSE;
    }
}
