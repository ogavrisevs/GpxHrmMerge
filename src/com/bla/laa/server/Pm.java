/*
 * @(#)Pm.java
 *
 * Copyright Swiss Reinsurance Company, Mythenquai 50/60, CH 8022 Zurich. All rights reserved.
 */
package com.bla.laa.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.util.logging.Logger;

public class Pm {
    private static final Logger logger = Logger.getLogger(Pm.class.getName());
    private static final PersistenceManagerFactory pmfInstance =
            JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private Pm() {
    }

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}

