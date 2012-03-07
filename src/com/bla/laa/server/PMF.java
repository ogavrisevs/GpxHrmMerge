package com.bla.laa.server;

import com.google.appengine.api.datastore.Key;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.util.logging.Logger;


public class PMF {
    private static final Logger logger = Logger.getLogger(PMF.class.getName());
    private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {
    }

    public static PersistenceManagerFactory get() {
        logger.info("PersistenceManagerFactory.get()");
        return pmfInstance;
    }

    public static Object getByKey(Class c, Key key) {
        if (key == null) {
            logger.info("Key not set !!!");
            return null;
        }
        try {
            return get().getPersistenceManager().getObjectById(c, key);
        } catch (NucleusObjectNotFoundException nonfe) {
            logger.info("object by key not found !");
            nonfe.printStackTrace();
            return null;
        }
    }

}

