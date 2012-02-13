package com.bla.laa.server;

import com.bla.laa.client.rootService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class rootServiceImpl extends RemoteServiceServlet implements rootService {
    // Implementation of sample interface method
    public String getMessage(String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }
}