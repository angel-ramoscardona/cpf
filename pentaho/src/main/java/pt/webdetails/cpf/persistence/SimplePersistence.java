/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package pt.webdetails.cpf.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimplePersistence implements ISimplePersistence {

    private static SimplePersistence instance;
    private static final Log logger = LogFactory.getLog(SimplePersistence.class);

    private SimplePersistence() {
    }

    public synchronized static SimplePersistence getInstance() {
        if (instance == null) {
            instance = new SimplePersistence();
        }
        return instance;
    }

    public <T extends Persistable> List<T> loadAll(Class<T> klass) {
        return load(klass, null);
    }

    public void storeAll(Collection<? extends Persistable> items) {
        PersistenceEngine pe = PersistenceEngine.getInstance();
        for (Persistable item : items) {
            pe.store(item);
        }
    }

    public void deleteAll(Collection<? extends Persistable> items) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void delete(Class<? extends Persistable> klass, Filter filter) {
        PersistenceEngine pe = PersistenceEngine.getInstance();
        String query = "delete from " + klass.getName();
        if (filter != null) {
            query += " where " + filter.toString();
        }
        try {
            pe.command(query, null);
        } catch (JSONException jse) {
            logger.error(jse);
        }
    }

    public <T extends Persistable> List<T> load(Class<T> klass, Filter filter) {
        List<T> list = new ArrayList<T>();
        PersistenceEngine pe = PersistenceEngine.getInstance();
        try {
            String query = "select from " + klass.getName();
            if (filter != null) {
                query += " where " + filter.toString();
            }
            JSONObject json;
            try {
                json = pe.query(query, null);
            } catch (RuntimeException e) {
                /* This query can fail if the class isn' initialized in OrientDB,
                 * so if it does fail, we'll just try checking whether the class
                 * exists. If it doesn', we can safely initialise it and run the
                 * query normally (yielding 0 results, presumably)
                 */
                if (!pe.classExists(klass.getName())) {
                    pe.initializeClass(klass.getName());
                    json = pe.query(query, null);
                } else {
                    json = null;
                }
            }
            JSONArray arr = json.getJSONArray("object");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                T inst = klass.newInstance();
                try {
                    inst.fromJSON(o);
                    inst.setKey(o.getString("@rid"));
                    list.add(inst);
                } catch (JSONException e) {
                }
            }

        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
        return list;
    }
}
