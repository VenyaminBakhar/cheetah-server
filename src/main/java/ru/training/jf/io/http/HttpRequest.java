package ru.training.jf.io.http;

import java.util.Map;

public interface HttpRequest {
    //в запросе приходит тип запроса
    HttpMethod getMethod();

    //строка запроса
    String getPath();

    Map<String,String> getParams();


    // достаём host и Port из хедера по ключу "Host"
    default String getHostAndPort() {
        return getHeaders().get("Host");
    }

    // заголовки
    Map<String,String> getHeaders();

    // тело
    String getBody();

    // статическая инициализация как альтернатива конструктору
    //  возвращаем объект анонимного класса
    static HttpRequest from(HttpMethod httpMethod,String path,
                            Map<String,String> params,
                            Map<String,String> headers,
                            String body) {
        return new HttpRequest() {
            @Override
            public HttpMethod getMethod() {
                return httpMethod;
            }

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            public String getBody() {
                return body;
            }
        };
    }


}
