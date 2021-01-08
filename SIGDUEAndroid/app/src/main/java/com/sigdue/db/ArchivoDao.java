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
 * DAO for table "ARCHIVO".
*/
public class ArchivoDao extends AbstractDao<Archivo, Long> {

    public static final String TABLENAME = "ARCHIVO";

    /**
     * Properties of entity Archivo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id_archivo = new Property(0, long.class, "id_archivo", true, "ID_ARCHIVO");
        public final static Property Id_usuario = new Property(1, Long.class, "id_usuario", false, "ID_USUARIO");
        public final static Property Tipo = new Property(2, String.class, "tipo", false, "TIPO");
        public final static Property Ruta = new Property(3, String.class, "ruta", false, "RUTA");
        public final static Property Nombre = new Property(4, String.class, "nombre", false, "NOMBRE");
        public final static Property Descripcion = new Property(5, String.class, "descripcion", false, "DESCRIPCION");
        public final static Property Estado = new Property(6, String.class, "estado", false, "ESTADO");
    }


    public ArchivoDao(DaoConfig config) {
        super(config);
    }
    
    public ArchivoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ARCHIVO\" (" + //
                "\"ID_ARCHIVO\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id_archivo
                "\"ID_USUARIO\" INTEGER," + // 1: id_usuario
                "\"TIPO\" TEXT," + // 2: tipo
                "\"RUTA\" TEXT," + // 3: ruta
                "\"NOMBRE\" TEXT," + // 4: nombre
                "\"DESCRIPCION\" TEXT," + // 5: descripcion
                "\"ESTADO\" TEXT);"); // 6: estado
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ARCHIVO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Archivo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId_archivo());
 
        Long id_usuario = entity.getId_usuario();
        if (id_usuario != null) {
            stmt.bindLong(2, id_usuario);
        }
 
        String tipo = entity.getTipo();
        if (tipo != null) {
            stmt.bindString(3, tipo);
        }
 
        String ruta = entity.getRuta();
        if (ruta != null) {
            stmt.bindString(4, ruta);
        }
 
        String nombre = entity.getNombre();
        if (nombre != null) {
            stmt.bindString(5, nombre);
        }
 
        String descripcion = entity.getDescripcion();
        if (descripcion != null) {
            stmt.bindString(6, descripcion);
        }
 
        String estado = entity.getEstado();
        if (estado != null) {
            stmt.bindString(7, estado);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Archivo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId_archivo());
 
        Long id_usuario = entity.getId_usuario();
        if (id_usuario != null) {
            stmt.bindLong(2, id_usuario);
        }
 
        String tipo = entity.getTipo();
        if (tipo != null) {
            stmt.bindString(3, tipo);
        }
 
        String ruta = entity.getRuta();
        if (ruta != null) {
            stmt.bindString(4, ruta);
        }
 
        String nombre = entity.getNombre();
        if (nombre != null) {
            stmt.bindString(5, nombre);
        }
 
        String descripcion = entity.getDescripcion();
        if (descripcion != null) {
            stmt.bindString(6, descripcion);
        }
 
        String estado = entity.getEstado();
        if (estado != null) {
            stmt.bindString(7, estado);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public Archivo readEntity(Cursor cursor, int offset) {
        Archivo entity = new Archivo( //
            cursor.getLong(offset + 0), // id_archivo
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // id_usuario
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // tipo
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ruta
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // nombre
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // descripcion
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // estado
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Archivo entity, int offset) {
        entity.setId_archivo(cursor.getLong(offset + 0));
        entity.setId_usuario(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setTipo(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRuta(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNombre(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDescripcion(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setEstado(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Archivo entity, long rowId) {
        entity.setId_archivo(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Archivo entity) {
        if(entity != null) {
            return entity.getId_archivo();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Archivo entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
