package ru.training.jf.io.http;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Вениамин on 7/26/2017.
 */
@Log4j2
public class HttpServer {

    @SneakyThrows
    public static void main(String... args)  {
        Executor executor = Executors.newFixedThreadPool(100);
        try (ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]))) {
            log.info("Server started,please visit: http://localhost:"+ args[0]);
            while (!Thread.currentThread().isInterrupted()) {

                Socket s = ss.accept();
                log.info("Client accepted");
                executor.execute(new SocketProcessor(s));
            }
        }

    }

}
