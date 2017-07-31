package ru.training.jf.io.http;

import java.net.Socket;

public class HelloWorldServer extends SocketProcessor {

    public static final String HTML =
            "<html><body><h1>Welcome to cheetah server!</h1></body></html>";




    HelloWorldServer(Socket s) throws Throwable {
        super(s);
    }

    @Override
    protected String mapRequest(HttpRequest httpRequest) {
        return HTML;

    }
}
