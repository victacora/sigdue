package com.sigdue.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TIPO_IDENTIFICACION".
*/
public class TipoIdentificacionDao extends AbstractDao<TipoIdentificacion, String> {

    public static final String TABLENAME = "TIPO_IDENTIFICACION";

    /**
     * Properties of entity TipoIdentificacion.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id_tipo_identificacion = new Property(0, String.class, "id_tipo_identificacion", true, "ID_TIPO_IDENTIFICACION");
        public final static Property Nombre = new Property(1, String.class, "nombre", false, "NOMBRE");
    }


    public TipoIdentificacionDao(DaoConfig config) {
        super(config);
    }
    
    public TipoIdentificacionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TIPO_IDENTIFICACION\" (" + //
                "\"ID_TIPO_IDENTIFICACION\" TEXT PRIMARY KEY NOT NULL ," + // 0: id_tipo_identificacion
                "\"NOMBRE\" TEXT);"); // 1: nombre
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TIPO_IDENTIFICACION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TipoIdentificacion entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId_tipo_identificacion());
 
        String nombre = entity.getNombre();
        if (nombre != null) {
            stmt.bindString(2, nombre);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TipoIdentificacion entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId_tipo_identificacion());
 
        String nombre = entity.getNombre();
        if (nombre != null) {
            stmt.bindString(2, nombre);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    @Override
    public TipoIdentificacion readEntity(Cursor cursor, int offset) {
        TipoIdentificacion entity = new TipoIdentificacion( //
            cursor.getString(offset + 0), // id_tipo_identificacion
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1) // nombre
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TipoIdentificacion entity, int offset) {
        entity.setId_tipo_identificacion(cursor.getString(offset + 0));
        entity.setNombre(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
     }
    
    @Override
    protected final String updateKeyAfterInsert(TipoIdentificacion entity, long rowId) {
        return entity.getId_tipo_identificacion();
    }
    
    @Override
    public String getKey(TipoIdentificacion entity) {
        if(entity != null) {
            return entity.getId_tipo_identificacion();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TipoIdentificacion entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
