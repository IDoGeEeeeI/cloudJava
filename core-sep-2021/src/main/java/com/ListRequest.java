package com;

public class ListRequest extends  Command{
    //верни мне список
    public CommandType getComType(){
        return CommandType.LIST_REQUEST;
    }
}
