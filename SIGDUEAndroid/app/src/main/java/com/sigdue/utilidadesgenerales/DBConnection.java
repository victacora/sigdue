package com.sigdue.utilidadesgenerales;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sigdue.db.DaoMaster;
import com.sigdue.db.DaoSession;

public class DBConnection {
    private Context context;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;

    public DBConnection(Context context) {
        try {
            this.context = context;
            this.db = new DaoMaster.DevOpenHelper(context, "inmovilizaciones-db", null).getWritableDatabase();
            this.daoMaster = new DaoMaster(this.db);
            this.daoSession = this.daoMaster.newSession();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public SQLiteDatabase getDb() {
        return this.db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public DaoMaster getDaoMaster() {
        return this.daoMaster;
    }

    public void setDaoMaster(DaoMaster daoMaster) {
        this.daoMaster = daoMaster;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public DaoSession getDaoSession() {
        return this.daoSession;
    }
}
