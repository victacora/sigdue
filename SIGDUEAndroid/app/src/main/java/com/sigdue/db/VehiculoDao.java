package com.sigdue.db;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VEHICULO".
*/
public class VehiculoDao extends AbstractDao<Vehiculo, Long> {

    public static final String TABLENAME = "VEHICULO";

    /**
     * Properties of entity Vehiculo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id_vehiculo = new Property(0, long.class, "id_vehiculo", true, "ID_VEHICULO");
        public final static Property Placa = new Property(1, String.class, "placa", false, "PLACA");
        public final static Property No_motor = new Property(2, String.class, "no_motor", false, "NO_MOTOR");
        public final static Property No_chasis = new Property(3, String.class, "no_chasis", false, "NO_CHASIS");
        public final static Property No_serie = new Property(4, String.class, "no_serie", false, "NO_SERIE");
        public final static Property Id_clase_vehiculo = new Property(5, Long.class, "id_clase_vehiculo", false, "ID_CLASE_VEHICULO");
        public final static Property Id_tipo_servicio = new Property(6, Long.class, "id_tipo_servicio", false, "ID_TIPO_SERVICIO");
        public final static Property Marca = new Property(7, String.class, "marca", false, "MARCA");
        public final static Property Linea = new Property(8, String.class, "linea", false, "LINEA");
        public final static Property Modelo = new Property(9, Long.class, "modelo", false, "MODELO");
        public final static Property Id_color = new Property(10, Long.class, "id_color", false, "ID_COLOR");
    }

    private DaoSession daoSession;


    public VehiculoDao(DaoConfig config) {
        super(config);
    }
    
    public VehiculoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VEHICULO\" (" + //
                "\"ID_VEHICULO\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id_vehiculo
                "\"PLACA\" TEXT UNIQUE ," + // 1: placa
                "\"NO_MOTOR\" TEXT UNIQUE ," + // 2: no_motor
                "\"NO_CHASIS\" TEXT UNIQUE ," + // 3: no_chasis
                "\"NO_SERIE\" TEXT UNIQUE ," + // 4: no_serie
                "\"ID_CLASE_VEHICULO\" INTEGER," + // 5: id_clase_vehiculo
                "\"ID_TIPO_SERVICIO\" INTEGER," + // 6: id_tipo_servicio
                "\"MARCA\" TEXT," + // 7: marca
                "\"LINEA\" TEXT," + // 8: linea
                "\"MODELO\" INTEGER," + // 9: modelo
                "\"ID_COLOR\" INTEGER);"); // 10: id_color
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VEHICULO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Vehiculo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId_vehiculo());
 
        String placa = entity.getPlaca();
        if (placa != null) {
            stmt.bindString(2, placa);
        }
 
        String no_motor = entity.getNo_motor();
        if (no_motor != null) {
            stmt.bindString(3, no_motor);
        }
 
        String no_chasis = entity.getNo_chasis();
        if (no_chasis != null) {
            stmt.bindString(4, no_chasis);
        }
 
        String no_serie = entity.getNo_serie();
        if (no_serie != null) {
            stmt.bindString(5, no_serie);
        }
 
        Long id_clase_vehiculo = entity.getId_clase_vehiculo();
        if (id_clase_vehiculo != null) {
            stmt.bindLong(6, id_clase_vehiculo);
        }
 
        Long id_tipo_servicio = entity.getId_tipo_servicio();
        if (id_tipo_servicio != null) {
            stmt.bindLong(7, id_tipo_servicio);
        }
 
        String marca = entity.getMarca();
        if (marca != null) {
            stmt.bindString(8, marca);
        }
 
        String linea = entity.getLinea();
        if (linea != null) {
            stmt.bindString(9, linea);
        }
 
        Long modelo = entity.getModelo();
        if (modelo != null) {
            stmt.bindLong(10, modelo);
        }
 
        Long id_color = entity.getId_color();
        if (id_color != null) {
            stmt.bindLong(11, id_color);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Vehiculo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId_vehiculo());
 
        String placa = entity.getPlaca();
        if (placa != null) {
            stmt.bindString(2, placa);
        }
 
        String no_motor = entity.getNo_motor();
        if (no_motor != null) {
            stmt.bindString(3, no_motor);
        }
 
        String no_chasis = entity.getNo_chasis();
        if (no_chasis != null) {
            stmt.bindString(4, no_chasis);
        }
 
        String no_serie = entity.getNo_serie();
        if (no_serie != null) {
            stmt.bindString(5, no_serie);
        }
 
        Long id_clase_vehiculo = entity.getId_clase_vehiculo();
        if (id_clase_vehiculo != null) {
            stmt.bindLong(6, id_clase_vehiculo);
        }
 
        Long id_tipo_servicio = entity.getId_tipo_servicio();
        if (id_tipo_servicio != null) {
            stmt.bindLong(7, id_tipo_servicio);
        }
 
        String marca = entity.getMarca();
        if (marca != null) {
            stmt.bindString(8, marca);
        }
 
        String linea = entity.getLinea();
        if (linea != null) {
            stmt.bindString(9, linea);
        }
 
        Long modelo = entity.getModelo();
        if (modelo != null) {
            stmt.bindLong(10, modelo);
        }
 
        Long id_color = entity.getId_color();
        if (id_color != null) {
            stmt.bindLong(11, id_color);
        }
    }

    @Override
    protected final void attachEntity(Vehiculo entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public Vehiculo readEntity(Cursor cursor, int offset) {
        Vehiculo entity = new Vehiculo( //
            cursor.getLong(offset + 0), // id_vehiculo
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // placa
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // no_motor
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // no_chasis
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // no_serie
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // id_clase_vehiculo
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // id_tipo_servicio
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // marca
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // linea
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9), // modelo
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10) // id_color
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Vehiculo entity, int offset) {
        entity.setId_vehiculo(cursor.getLong(offset + 0));
        entity.setPlaca(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNo_motor(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNo_chasis(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNo_serie(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setId_clase_vehiculo(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setId_tipo_servicio(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setMarca(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setLinea(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setModelo(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
        entity.setId_color(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Vehiculo entity, long rowId) {
        entity.setId_vehiculo(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Vehiculo entity) {
        if(entity != null) {
            return entity.getId_vehiculo();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Vehiculo entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getClaseVehiculoDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getTipoServicioDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getColorDao().getAllColumns());
            builder.append(" FROM VEHICULO T");
            builder.append(" LEFT JOIN CLASE_VEHICULO T0 ON T.\"ID_CLASE_VEHICULO\"=T0.\"ID_CLASE_VEHICULO\"");
            builder.append(" LEFT JOIN TIPO_SERVICIO T1 ON T.\"ID_TIPO_SERVICIO\"=T1.\"ID_TIPO_SERVICIO\"");
            builder.append(" LEFT JOIN COLOR T2 ON T.\"ID_COLOR\"=T2.\"ID_COLOR\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Vehiculo loadCurrentDeep(Cursor cursor, boolean lock) {
        Vehiculo entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        ClaseVehiculo claseVehiculo = loadCurrentOther(daoSession.getClaseVehiculoDao(), cursor, offset);
        entity.setClaseVehiculo(claseVehiculo);
        offset += daoSession.getClaseVehiculoDao().getAllColumns().length;

        TipoServicio tipoServicio = loadCurrentOther(daoSession.getTipoServicioDao(), cursor, offset);
        entity.setTipoServicio(tipoServicio);
        offset += daoSession.getTipoServicioDao().getAllColumns().length;

        Color color = loadCurrentOther(daoSession.getColorDao(), cursor, offset);
        entity.setColor(color);

        return entity;    
    }

    public Vehiculo loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Vehiculo> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Vehiculo> list = new ArrayList<Vehiculo>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Vehiculo> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Vehiculo> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
