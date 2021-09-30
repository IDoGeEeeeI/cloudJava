package com.netty;

import com.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import  com.ListResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Slf4j

public class FileMassageHandler extends SimpleChannelInboundHandler<Command> {

    private  static Path ROOT = Paths.get("server-sep-2021", "root");
    private  ListResponse lr ;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        // TODO: 29.09.2021  
//        Files.write(
//                ROOT.resolve(fileMassage.getName()),
//                fileMassage.getBytes());

       // ctx.writeAndFlush("ok");
        switch (cmd.getType()){
            case FILE_MESSAGE:
                FileMassage fileMassage = (FileMassage) cmd;
                ctx.writeAndFlush(Files.write(
                        ROOT.resolve(fileMassage.getName()),
                        fileMassage.getBytes()
                ));
                break;
            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) cmd;
                ctx.writeAndFlush(fileRequest);
                break;
            case LIST_REQUEST:
                ListRequest listRequest  = (ListRequest) cmd;
                ctx.writeAndFlush(listRequest);
                break;
            case LIST_RESPONSE:
                ListResponse listResponse = (ListResponse) cmd;
                ctx.writeAndFlush(listResponse);
                break;
            case PATH_REQUEST:
//не дописал
                break;
            case PATH_RESPONSE:
//еще не дописал
                break;
            default:
                log.debug("Invalid command {}", cmd.getType());
                break;
        }

    }
}
