package com.bla.laa.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("rootService")
public interface rootService extends RemoteService {
    // Sample interface method of remote interface
    String getMessage(String msg);

    /**
     * Utility/Convenience class.
     * Use rootService.App.getInstance() to access static instance of rootServiceAsync
     */
    public static class App {
        private static com.bla.laa.client.rootServiceAsync ourInstance = GWT.create(rootService.class);

        public static synchronized rootServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
