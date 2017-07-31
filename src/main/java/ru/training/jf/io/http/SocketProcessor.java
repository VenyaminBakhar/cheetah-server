package ru.training.jf.io.http;

import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Вениамин on 7/26/2017.
 */
@Log4j2
public abstract class SocketProcessor implements Runnable {

    public static final String RESPONSE = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: %d\r\n" +
            "Connection: close\r\n\r\n%s";

    private Socket s;
    private InputStream is;
    private OutputStream os;

    SocketProcessor(Socket s) throws Throwable {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    public void run() {
        try {
            writeResponse(
                    mapRequest(getHttpRequest())
            );
        } catch (Throwable t) {
                /*do nothing*/
        } finally {
            try {
                s.close();
            } catch (Throwable t) {
                    /*do nothing*/
            }
        }
        log.info("Client processing finished");
    }

    abstract protected String mapRequest(HttpRequest httpRequest);

    private void writeResponse(String s) throws Throwable {
        os.write(String.format(RESPONSE, s.length(),s).getBytes());
        os.flush();
    }

    private HttpRequest getHttpRequest() throws Throwable {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s = br.readLine().trim();


        String[] contentAndTail = s.split("\\s",2);
        HttpMethod httpMethod = HttpMethod.valueOf(contentAndTail[0]);
        // regexp отрежет до вопросительного знака,либо до пробела
        System.out.println("HttpMethod:" + contentAndTail[0]);
        contentAndTail = contentAndTail[1].split("[?\\s]",2);
        String path = contentAndTail[0];
        System.out.println("path: "+contentAndTail[0]);

        Map<String,String> params = contentAndTail[1].startsWith("HTTP") ?
                Collections.emptyMap():
                getParams(contentAndTail[1].split("\\s")[2]);
        for (Map.Entry<String, String> entry: params.entrySet())
            System.out.println(entry.getKey() + " = " + entry.getValue());


        val headers = new HashMap<String,String>();
        //тело от заголовков отделяется строкой
        while((s = br.readLine()) != null   &&  !s.trim().isEmpty()) {
            String[] header = s.split(":\\s", 2);
            headers.put(header[0],header[1]);
            for (Map.Entry<String, String> entry: headers.entrySet())
                System.out.println(entry.getKey() + " = " + entry.getValue());
//            log.info(s);
        }

        val body = new StringBuilder();
//        if(httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT)
        while(br.ready() &&(s = br.readLine()) != null)
            body.append(s);

        System.out.println("body:"+body);
        return HttpRequest.from(httpMethod,path,params,headers,body.toString());
    }

    private Map<String,String> getParams(String s) {
        val params = new HashMap<String,String>();
        for (String param : s.split("&")) {
            String[] split = param.split("=",2);
            params.put(split[0],split[1]);
        }
        return params;
    }
}
