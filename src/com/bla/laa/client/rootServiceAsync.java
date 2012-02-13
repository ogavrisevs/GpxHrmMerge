package com.bla.laa.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface rootServiceAsync {
    void getMessage(String msg, AsyncCallback<String> async);
}
