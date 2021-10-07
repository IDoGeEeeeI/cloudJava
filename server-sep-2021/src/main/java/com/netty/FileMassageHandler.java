package com.netty;

import com.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import  com.ListResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Slf4j

public class FileMassageHandler extends SimpleChannelInboundHandler<Command> {
    private static Path ROOT;
    public FileMassageHandler() throws IOException {
         ROOT = Paths.get("server-sep-2021", "root");
        if (!Files.exists(ROOT)) {
            Files.createFile(ROOT);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)throws Exception{
        ctx.writeAndFlush(new ListResponse(ROOT));
        ctx.writeAndFlush(new PathResponse(ROOT.toString()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {



        switch (cmd.getType()){
            case FILE_MESSAGE:
                FileMassage fileMassage = (FileMassage) cmd;
                Files.write(ROOT.resolve(fileMassage.getName()),fileMassage.getBytes());
                ctx.writeAndFlush(new ListResponse(ROOT));
                break;
            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) cmd;
                FileMassage msg = new FileMassage(ROOT.resolve(fileRequest.getfName()));
                ctx.writeAndFlush(msg);
                break;
            case LIST_REQUEST:
                ctx.writeAndFlush(new ListResponse(ROOT));
                break;
            case PATH_IN_REQUEST:
                PathInRequest pathinrequest = (PathInRequest) cmd;
                Path newPath = ROOT.resolve(pathinrequest.getDir());
                if(Files.isDirectory(newPath)){
                    ctx.writeAndFlush(new PathResponse(ROOT.toString()));
                    ctx.writeAndFlush(new ListResponse(ROOT));
                }
                break;
            case  PATH_UP_REQUEST:
                if(ROOT.getParent() != null){
                    ROOT = ROOT.getParent();
                }
                ctx.writeAndFlush(new PathResponse(ROOT.toString()));
                ctx.writeAndFlush(new ListResponse(ROOT));
                break;
            default:
                log.debug("Invalid command {}", cmd.getType());
                break;
        }

    }
}
