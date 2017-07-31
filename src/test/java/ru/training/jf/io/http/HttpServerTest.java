package ru.training.jf.io.http;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.stream.Collectors;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static ru.training.jf.io.http.HelloWorldServer.HTML;
import static ru.training.jf.io.http.SocketProcessor.RESPONSE;




/**
 * Created by Вениамин on 7/28/2017.
 */
@Log4j2
public class HttpServerTest {

    public static final int PORT = 8080;
    public static final String REQUEST = "GET / HTTP/1.1\r\n" +
            "Host: localhost:8080\r\n" +
            "Connection: keep-alive\r\n" +
            "Cache-Control: max-age=0\r\n" +
            "Upgrade-Insecure-Requests: 1\r\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36\r\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n" +
            "Accept-Encoding: gzip, deflate, br\r\n" +
            "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4\r\n" +
            "X-Compress: null\r\n\r\n";

    static Thread serverThread;

    @BeforeAll
    static void setUp() {
        serverThread = new Thread(()->HttpServer.main(String.valueOf(PORT)));
        serverThread.start();
    }

    @AfterAll
    static void tearDown() {
        serverThread.interrupt();
    }

    @Test
    @SneakyThrows
    void ping() throws Throwable  {

        try (val socket = new Socket("localhost", PORT);
             val outputStream = socket.getOutputStream();
             val reader = new BufferedReader(new InputStreamReader(
                     socket.getInputStream()))) {

            outputStream.write(REQUEST.getBytes());
//            outputStream.close();
            String response = reader.lines()
//                    .filter(s -> s.trim().length() > 0)
                    .collect(Collectors.joining("\r\n"));

            String s = HTML;
            assertThat(response,is(String.format(RESPONSE,s.length(),s)));


//            String line;
//            while((line = reader.readLine()) != null && !line.trim().isEmpty()) {
//                log.info(line);
//            }
        }
    }

}