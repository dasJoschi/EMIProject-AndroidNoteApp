package com.example.emiproject_androidnoteapp.utils;

import com.example.emiproject_androidnoteapp.models.Image;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class RealmFacade implements Closeable {

    private static boolean configured = false;
    private Realm realm;

    private static final Object mLock = new Object();

    public RealmFacade() {
    }

    /**
     * Use only when you are trying to access a method not supported by {@link RealmFacade}
     */
    public Realm getRealm() {
        checkRealm();
        return realm;
    }

    public <T extends RealmObject> RealmQuery<T> where(Class<T> clazz) {
        checkRealm();
        if (!isValid()) {
            return null;
        }
        return realm.where(clazz);
    }

    public <T extends RealmObject> RealmResults<T> allObjects(Class<T> clazz) {
        checkRealm();
        if (!isValid()) {
            return null;
        }
        return realm.where(clazz).findAll();
    }

    public <T extends RealmObject> T createObject(Class<T> clazz) {
        if (!isValid()) {
            return null;
        }
        return realm.createObject(clazz);
    }

    public <T extends RealmObject> T copyToRealm(T object) {
        if (!isValid()) {
            return null;
        }
        return realm.copyToRealm(object);
    }

    public <T extends RealmObject> T copyToRealmOrUpdate(T object) {
        if (!isValid()) {
            return null;
        }
        return realm.copyToRealmOrUpdate(object);

    }

    public <E extends RealmObject> List<E> copyToRealm(Iterable<E> objects) {
        if (!isValid()) {
            return null;
        }
        return realm.copyToRealm(objects);
    }

    public <E extends RealmObject> List<E> copyToRealmOrUpdate(Iterable<E> objects) {
        if (!isValid()) {
            return null;
        }
        return realm.copyToRealmOrUpdate(objects);
    }

    public <E extends RealmObject> void save(final E object) {
        executeTransaction(new Transaction() {
            @Override
            public void execute(RealmFacade realm) {
                copyToRealmOrUpdate(object);
            }
        });
    }

    public <E extends RealmObject> void save(final Iterable<E> objects) {
        executeTransaction(new Transaction() {
            @Override
            public void execute(RealmFacade realm) {
                copyToRealmOrUpdate(objects);
            }
        });
    }

    //region experimental
    public <T extends RealmObject> RealmFacade set(T object, String fieldName, Object value) {
        Method method = findMethod(object, fieldName, value.getClass());
        invokeMethod(method, object, value);

        return this;
    }

    public <T extends RealmObject> RealmFacade set(T object, String fieldName, boolean value) {
        Method method = findMethod(object, fieldName, boolean.class);
        invokeMethod(method, object, value);

        return this;
    }

    public <T extends RealmObject> RealmFacade set(T object, String fieldName, long value) {
        Method method = findMethod(object, fieldName, long.class);
        invokeMethod(method, object, value);

        return this;
    }

    public <T extends RealmObject> RealmFacade set(T object, String fieldName, float value) {
        Method method = findMethod(object, fieldName, float.class);
        invokeMethod(method, object, value);

        return this;
    }

    public <T extends RealmObject> RealmFacade set(List<T> objects, String fieldName, Object value) {
        if (objects.isEmpty()) {
            return this;
        }

        Method method = findMethod(objects, fieldName, value.getClass());
        invokeMethod(method, objects, value);

        return this;
    }

    public <T extends RealmObject> RealmFacade set(List<T> objects, String fieldName, boolean value) {
        if (objects.isEmpty()) {
            return this;
        }

        Method method = findMethod(objects, fieldName, boolean.class);
        invokeMethod(method, objects, value);

        return this;
    }

    public <T extends RealmObject> RealmFacade set(List<T> objects, String fieldName, long value) {
        Method method = findMethod(objects, fieldName, long.class);
        invokeMethod(method, objects, value);

        return this;
    }

    public <T extends RealmObject> RealmFacade set(List<T> objects, String fieldName, float value) {
        Method method = findMethod(objects, fieldName, float.class);
        invokeMethod(method, objects, value);

        return this;
    }

    private <T extends RealmObject> void invokeMethod(Method method, T object, Object value) {

        synchronized (mLock) {
            internalBeginTransaction(false);
            try {
                method.invoke(object, value);
                internalCommitTransaction();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                internalCancelTransaction();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                internalCancelTransaction();
            }
        }

    }

    private <T extends RealmObject> void invokeMethod(Method method, List<T> objects, Object value) {

        synchronized (mLock) {
            internalBeginTransaction(false);
            try {
                for (int i = 0; i < objects.size(); i++) {
                    method.invoke(objects.get(i), value);
                }
                internalCommitTransaction();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                internalCancelTransaction();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                internalCancelTransaction();
            }
        }
    }


    private synchronized Method findMethod(Object obj,
                                           String property, Class paramType) {
        Class<?> theClass = obj.getClass();
        String setter = String.format("set%C%s",
                property.charAt(0), property.substring(1));
        //Class paramType = value.getClass();

        try {
            return theClass.getMethod(setter, paramType);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    private synchronized Method findMethod(List objects,
                                           String property, Class paramType) {
        return findMethod(objects.get(0), property, paramType);
    }

    //endregion

    public <T extends RealmObject> void removeFromRealm(T object) {
        checkRealm();

        synchronized (mLock) {
            internalBeginTransaction(false);
            try {
                if (object.isValid()) {
                    object.deleteFromRealm();
                    internalCommitTransaction();
                }
            } catch (RuntimeException e) {
                internalCancelTransaction();
                throw new RealmException("Error during transaction.", e);
            } catch (Error e) {
                internalCancelTransaction();
                throw e;
            }
        }
    }

    public void beginTransaction() {
        beginTransaction(false);
    }

    public void beginTransaction(boolean commitCurrent) {
        synchronized (mLock) {
            internalBeginTransaction(commitCurrent);
        }
    }

    private void internalBeginTransaction(boolean commitCurrent) {
        checkRealm();

        if (realm.isInTransaction() && commitCurrent) {
            realm.commitTransaction();
            realm.beginTransaction();
        } else if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }

    }

    public void commitTransaction() {
        synchronized (mLock) {
            internalCommitTransaction();
        }
    }

    private void internalCommitTransaction() {
        if (isValid() && realm.isInTransaction()) {
            realm.commitTransaction();
        }
    }

    public void cancelTransaction() {
        synchronized (mLock) {
            internalCancelTransaction();
        }
    }

    private void internalCancelTransaction() {
        if (isValid() && realm.isInTransaction()) {
            realm.cancelTransaction();
        }
    }

    public void clear(Class<? extends RealmObject> classSpec) {
        realm.where(classSpec).findAll().deleteAllFromRealm();
    }

    @Override
    public void close() {
        internalClose();
    }

    private void internalClose() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private static void applyDefaultConfig() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
        configured = true;
    }

    private void checkRealm() {
        if (realm == null || realm.isClosed()) {

            if (!configured) {
                applyDefaultConfig();
            }

            realm = Realm.getDefaultInstance();
        }
    }

    public boolean isValid() {
        return !(realm == null || realm.isClosed());
    }

    public <T extends RealmObject> void executeTransaction(T object, AdvancedTransaction<T> transaction) {

        synchronized (mLock) {
            internalBeginTransaction(false);
            try {
                if (object.isValid()) {
                    transaction.execute(object);
                    internalCommitTransaction();
                }
            } catch (RuntimeException e) {
                internalCancelTransaction();
                throw new RealmException("Error during transaction.", e);
            } catch (Error e) {
                internalCancelTransaction();
                throw e;
            }
        }

    }

    public void executeTransaction(Transaction transaction) {
        executeTransaction(transaction, false);
    }

    public synchronized void executeTransaction(final Transaction transaction, boolean commitCurrent) {
        if (transaction == null) {
            return;
        }

        synchronized (mLock) {
            internalBeginTransaction(commitCurrent);
            try {
                transaction.execute(this);
                internalCommitTransaction();
            } catch (RuntimeException e) {
                internalCancelTransaction();
                throw new RealmException("Error during transaction.", e);
            } catch (Error e) {
                internalCancelTransaction();
                throw e;
            }
        }

    }

    public interface Transaction {
        void execute(RealmFacade realm);
    }

    public interface AdvancedTransaction<T extends RealmObject> {
        void execute(T object);
    }

    public void clearDataBase() {
        beginTransaction();
        realm.deleteAll();
        commitTransaction();
    }

    public void deleteAllImages() {

        RealmResults<Image> images = allObjects(Image.class);
        File file;
        for (Image image : images) {
            file = new File(image.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
