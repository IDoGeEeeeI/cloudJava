package com.nio;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

@Slf4j

public class NioUtils {
    @SneakyThrows
    public static void main(String[] args) {
        Path path = Paths.get("server-sep-2021", "root");
        System.out.println(Files.exists(path));
        System.out.println(Files.size(path));

        Path copy = Paths.get("server-sep-2021","root","copy.txt");
        System.out.println(path.getParent().resolve("copy.txt"));;
        System.out.println(path.toAbsolutePath());

        WatchService watchService = FileSystems.getDefault()
                .newWatchService();
        new Thread(()-> {
            while (true){
                WatchKey key = null;
                try {
                    key = watchService.take();
                    if(key.isValid()){
                        List<WatchEvent<?>> events = key.pollEvents();
                        for (WatchEvent<?> event : events) {
                            log.debug("kind {}, context {}",event.kind(), event.context());
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }).start();
        path.register(watchService, ENTRY_MODIFY, ENTRY_CREATE,ENTRY_DELETE);

       Files.write(
               copy,
               " message".getBytes(StandardCharsets.UTF_8),
               StandardOpenOption.APPEND

       );

       Files.copy(
               copy,
               Paths.get("server-sep-2021","root","f1.txt"),
               StandardCopyOption.REPLACE_EXISTING
       );

       Files.walk(path).forEach(System.out::println);
    }
}
