package com.example.emiproject_androidnoteapp.utils;


import io.realm.RealmObject;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class IdGenerator {

    private RealmFacade realm;
    private Class<? extends RealmObject> clazz;
    private long maximumId = 0;

    public IdGenerator(RealmFacade realm, Class<? extends RealmObject> clazz) {
        this.realm = realm;
        this.clazz = clazz;
    }

    public synchronized long generateNewID() {

        return generateNewID("localId");

    }

    public synchronized long generateNewID(String fieldName) {

        if (maximumId == 0 && realm.where(clazz).count() != 0) {
            maximumId = realm.where(clazz).max(fieldName).longValue();
        }

        maximumId++;

        return maximumId;
    }
}
