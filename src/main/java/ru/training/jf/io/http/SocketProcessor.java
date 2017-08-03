package ru.training.jf.io.http;

import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Вениамин on 7/26/2017.
 */
@Log4j2
public  class SocketProcessor implements Runnable {

    public static final String PATHRESOURCE = "C:\\Users\\Вениамин\\Desktop\\cheetah-server\\src\\main\\resources\\";

    public static final String FILENOTFOUND = "HTTP/1.1 404 Not Found\r\n"+
                                    "Content-Length: 0";

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
            writeResponse(getHttpRequest());

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



    private void writeResponse(HttpRequest request) throws Throwable {
            if (request.getMethod().toString().equals("GET"))doGet(request);
            else doPost(request);
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
                getParams(contentAndTail[1].split("\\s",2)[0]);
        for (Map.Entry<String, String> entry: params.entrySet())
            System.err.println(entry.getKey() + " = " + entry.getValue());


        val headers = new HashMap<String,String>();
        //тело от заголовков отделяется строкой
        while((s = br.readLine()) != null   &&  !s.trim().isEmpty()) {
            String[] header = s.split(":\\s", 2);
            headers.put(header[0],header[1]);
            for (Map.Entry<String, String> entry: headers.entrySet())
                System.out.println(entry.getKey() + " = " + entry.getValue());
            log.info(s);
        }

        val body = new StringBuilder();
        if(httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT)
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

    private void doGet (HttpRequest request) {
        if (request.getParams().isEmpty()) {
            try {
                os.write(String.format(RESPONSE,
                        htmlToString("index.html").
                                length(), htmlToString("index.html"))
                        .getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String filePath = "\\"+request.getParams().get("contenttype")+"\\"+request.getParams().get("content");
            getFile(filePath);
        }
        //переделать отталкиваясь от того,что первая группа параметров type=image content = pic, switch оператор
    }

    private void doPost(HttpRequest request) {

        //todo POST method
    }

    private void getFile(String fileName){
        File file = new File(PATHRESOURCE, fileName);
        try (FileInputStream fis = new FileInputStream(file)){
            byte[] data = new byte[64 * 1024];
            for (int read; (read = fis.read(data)) > -1; ) os.write(data, 0, read);
            os.flush();
        } catch (FileNotFoundException e) {

            try {
                os.write(FILENOTFOUND.getBytes());
                os.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //todo 404page
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String htmlToString(String fileName){
        StringBuilder result = new StringBuilder();
        try(FileInputStream fstream = new FileInputStream(PATHRESOURCE+fileName)){
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null){
                result.append(strLine);
            }
        }catch (IOException e){
            System.out.println("Ошибка");
        }
        return result.toString();
    }
}


