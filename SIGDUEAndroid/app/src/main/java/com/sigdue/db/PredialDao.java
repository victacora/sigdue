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
 * DAO for table "PREDIAL".
*/
public class PredialDao extends AbstractDao<Predial, String> {

    public static final String TABLENAME = "PREDIAL";

    /**
     * Properties of entity Predial.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Dane_sede = new Property(0, String.class, "dane_sede", true, "DANE_SEDE");
        public final static Property Cod_predio = new Property(1, String.class, "cod_predio", false, "COD_PREDIO");
        public final static Property Clima = new Property(2, String.class, "clima", false, "CLIMA");
        public final static Property Distancia_mts_sede_ppal = new Property(3, String.class, "distancia_mts_sede_ppal", false, "DISTANCIA_MTS_SEDE_PPAL");
        public final static Property Dist_km_centro_poblado = new Property(4, String.class, "dist_km_centro_poblado", false, "DIST_KM_CENTRO_POBLADO");
        public final static Property Clase_predio = new Property(5, String.class, "clase_predio", false, "CLASE_PREDIO");
        public final static Property Avaluo_catastral = new Property(6, String.class, "avaluo_catastral", false, "AVALUO_CATASTRAL");
        public final static Property Fec_avaluo_catastral = new Property(7, String.class, "fec_avaluo_catastral", false, "FEC_AVALUO_CATASTRAL");
        public final static Property Avaluo_comercial = new Property(8, String.class, "avaluo_comercial", false, "AVALUO_COMERCIAL");
        public final static Property Fec_avaluo_comercial = new Property(9, String.class, "fec_avaluo_comercial", false, "FEC_AVALUO_COMERCIAL");
        public final static Property Zona_aislamiento = new Property(10, String.class, "zona_aislamiento", false, "ZONA_AISLAMIENTO");
        public final static Property Zona_alto_riesgo = new Property(11, String.class, "zona_alto_riesgo", false, "ZONA_ALTO_RIESGO");
        public final static Property Zona_proteccion = new Property(12, String.class, "zona_proteccion", false, "ZONA_PROTECCION");
        public final static Property Topografia = new Property(13, String.class, "topografia", false, "TOPOGRAFIA");
        public final static Property Propiedad_lote = new Property(14, String.class, "propiedad_lote", false, "PROPIEDAD_LOTE");
        public final static Property Tipo_documento = new Property(15, String.class, "tipo_documento", false, "TIPO_DOCUMENTO");
        public final static Property Cual_tipo_documento = new Property(16, String.class, "cual_tipo_documento", false, "CUAL_TIPO_DOCUMENTO");
        public final static Property Nro_documento_legalizacion = new Property(17, String.class, "nro_documento_legalizacion", false, "NRO_DOCUMENTO_LEGALIZACION");
        public final static Property Fec_expedicion = new Property(18, String.class, "fec_expedicion", false, "FEC_EXPEDICION");
        public final static Property Notaria_dependencia_origen = new Property(19, String.class, "notaria_dependencia_origen", false, "NOTARIA_DEPENDENCIA_ORIGEN");
        public final static Property Lugar_expedicion = new Property(20, String.class, "lugar_expedicion", false, "LUGAR_EXPEDICION");
        public final static Property Registro_catastral = new Property(21, String.class, "registro_catastral", false, "REGISTRO_CATASTRAL");
        public final static Property Matricula_inmobiliaria = new Property(22, String.class, "matricula_inmobiliaria", false, "MATRICULA_INMOBILIARIA");
        public final static Property Propietarios = new Property(23, String.class, "propietarios", false, "PROPIETARIOS");
        public final static Property Tenencia = new Property(24, String.class, "tenencia", false, "TENENCIA");
        public final static Property Con_quien_tenencia = new Property(25, String.class, "con_quien_tenencia", false, "CON_QUIEN_TENENCIA");
        public final static Property Nom_quien_tenencia = new Property(26, String.class, "nom_quien_tenencia", false, "NOM_QUIEN_TENENCIA");
        public final static Property Fecha_tenencia_lote = new Property(27, String.class, "fecha_tenencia_lote", false, "FECHA_TENENCIA_LOTE");
    }


    public PredialDao(DaoConfig config) {
        super(config);
    }
    
    public PredialDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PREDIAL\" (" + //
                "\"DANE_SEDE\" TEXT PRIMARY KEY NOT NULL ," + // 0: dane_sede
                "\"COD_PREDIO\" TEXT," + // 1: cod_predio
                "\"CLIMA\" TEXT," + // 2: clima
                "\"DISTANCIA_MTS_SEDE_PPAL\" TEXT," + // 3: distancia_mts_sede_ppal
                "\"DIST_KM_CENTRO_POBLADO\" TEXT," + // 4: dist_km_centro_poblado
                "\"CLASE_PREDIO\" TEXT," + // 5: clase_predio
                "\"AVALUO_CATASTRAL\" TEXT," + // 6: avaluo_catastral
                "\"FEC_AVALUO_CATASTRAL\" TEXT," + // 7: fec_avaluo_catastral
                "\"AVALUO_COMERCIAL\" TEXT," + // 8: avaluo_comercial
                "\"FEC_AVALUO_COMERCIAL\" TEXT," + // 9: fec_avaluo_comercial
                "\"ZONA_AISLAMIENTO\" TEXT," + // 10: zona_aislamiento
                "\"ZONA_ALTO_RIESGO\" TEXT," + // 11: zona_alto_riesgo
                "\"ZONA_PROTECCION\" TEXT," + // 12: zona_proteccion
                "\"TOPOGRAFIA\" TEXT," + // 13: topografia
                "\"PROPIEDAD_LOTE\" TEXT," + // 14: propiedad_lote
                "\"TIPO_DOCUMENTO\" TEXT," + // 15: tipo_documento
                "\"CUAL_TIPO_DOCUMENTO\" TEXT," + // 16: cual_tipo_documento
                "\"NRO_DOCUMENTO_LEGALIZACION\" TEXT," + // 17: nro_documento_legalizacion
                "\"FEC_EXPEDICION\" TEXT," + // 18: fec_expedicion
                "\"NOTARIA_DEPENDENCIA_ORIGEN\" TEXT," + // 19: notaria_dependencia_origen
                "\"LUGAR_EXPEDICION\" TEXT," + // 20: lugar_expedicion
                "\"REGISTRO_CATASTRAL\" TEXT," + // 21: registro_catastral
                "\"MATRICULA_INMOBILIARIA\" TEXT," + // 22: matricula_inmobiliaria
                "\"PROPIETARIOS\" TEXT," + // 23: propietarios
                "\"TENENCIA\" TEXT," + // 24: tenencia
                "\"CON_QUIEN_TENENCIA\" TEXT," + // 25: con_quien_tenencia
                "\"NOM_QUIEN_TENENCIA\" TEXT," + // 26: nom_quien_tenencia
                "\"FECHA_TENENCIA_LOTE\" TEXT);"); // 27: fecha_tenencia_lote
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PREDIAL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Predial entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getDane_sede());
 
        String cod_predio = entity.getCod_predio();
        if (cod_predio != null) {
            stmt.bindString(2, cod_predio);
        }
 
        String clima = entity.getClima();
        if (clima != null) {
            stmt.bindString(3, clima);
        }
 
        String distancia_mts_sede_ppal = entity.getDistancia_mts_sede_ppal();
        if (distancia_mts_sede_ppal != null) {
            stmt.bindString(4, distancia_mts_sede_ppal);
        }
 
        String dist_km_centro_poblado = entity.getDist_km_centro_poblado();
        if (dist_km_centro_poblado != null) {
            stmt.bindString(5, dist_km_centro_poblado);
        }
 
        String clase_predio = entity.getClase_predio();
        if (clase_predio != null) {
            stmt.bindString(6, clase_predio);
        }
 
        String avaluo_catastral = entity.getAvaluo_catastral();
        if (avaluo_catastral != null) {
            stmt.bindString(7, avaluo_catastral);
        }
 
        String fec_avaluo_catastral = entity.getFec_avaluo_catastral();
        if (fec_avaluo_catastral != null) {
            stmt.bindString(8, fec_avaluo_catastral);
        }
 
        String avaluo_comercial = entity.getAvaluo_comercial();
        if (avaluo_comercial != null) {
            stmt.bindString(9, avaluo_comercial);
        }
 
        String fec_avaluo_comercial = entity.getFec_avaluo_comercial();
        if (fec_avaluo_comercial != null) {
            stmt.bindString(10, fec_avaluo_comercial);
        }
 
        String zona_aislamiento = entity.getZona_aislamiento();
        if (zona_aislamiento != null) {
            stmt.bindString(11, zona_aislamiento);
        }
 
        String zona_alto_riesgo = entity.getZona_alto_riesgo();
        if (zona_alto_riesgo != null) {
            stmt.bindString(12, zona_alto_riesgo);
        }
 
        String zona_proteccion = entity.getZona_proteccion();
        if (zona_proteccion != null) {
            stmt.bindString(13, zona_proteccion);
        }
 
        String topografia = entity.getTopografia();
        if (topografia != null) {
            stmt.bindString(14, topografia);
        }
 
        String propiedad_lote = entity.getPropiedad_lote();
        if (propiedad_lote != null) {
            stmt.bindString(15, propiedad_lote);
        }
 
        String tipo_documento = entity.getTipo_documento();
        if (tipo_documento != null) {
            stmt.bindString(16, tipo_documento);
        }
 
        String cual_tipo_documento = entity.getCual_tipo_documento();
        if (cual_tipo_documento != null) {
            stmt.bindString(17, cual_tipo_documento);
        }
 
        String nro_documento_legalizacion = entity.getNro_documento_legalizacion();
        if (nro_documento_legalizacion != null) {
            stmt.bindString(18, nro_documento_legalizacion);
        }
 
        String fec_expedicion = entity.getFec_expedicion();
        if (fec_expedicion != null) {
            stmt.bindString(19, fec_expedicion);
        }
 
        String notaria_dependencia_origen = entity.getNotaria_dependencia_origen();
        if (notaria_dependencia_origen != null) {
            stmt.bindString(20, notaria_dependencia_origen);
        }
 
        String lugar_expedicion = entity.getLugar_expedicion();
        if (lugar_expedicion != null) {
            stmt.bindString(21, lugar_expedicion);
        }
 
        String registro_catastral = entity.getRegistro_catastral();
        if (registro_catastral != null) {
            stmt.bindString(22, registro_catastral);
        }
 
        String matricula_inmobiliaria = entity.getMatricula_inmobiliaria();
        if (matricula_inmobiliaria != null) {
            stmt.bindString(23, matricula_inmobiliaria);
        }
 
        String propietarios = entity.getPropietarios();
        if (propietarios != null) {
            stmt.bindString(24, propietarios);
        }
 
        String tenencia = entity.getTenencia();
        if (tenencia != null) {
            stmt.bindString(25, tenencia);
        }
 
        String con_quien_tenencia = entity.getCon_quien_tenencia();
        if (con_quien_tenencia != null) {
            stmt.bindString(26, con_quien_tenencia);
        }
 
        String nom_quien_tenencia = entity.getNom_quien_tenencia();
        if (nom_quien_tenencia != null) {
            stmt.bindString(27, nom_quien_tenencia);
        }
 
        String fecha_tenencia_lote = entity.getFecha_tenencia_lote();
        if (fecha_tenencia_lote != null) {
            stmt.bindString(28, fecha_tenencia_lote);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Predial entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getDane_sede());
 
        String cod_predio = entity.getCod_predio();
        if (cod_predio != null) {
            stmt.bindString(2, cod_predio);
        }
 
        String clima = entity.getClima();
        if (clima != null) {
            stmt.bindString(3, clima);
        }
 
        String distancia_mts_sede_ppal = entity.getDistancia_mts_sede_ppal();
        if (distancia_mts_sede_ppal != null) {
            stmt.bindString(4, distancia_mts_sede_ppal);
        }
 
        String dist_km_centro_poblado = entity.getDist_km_centro_poblado();
        if (dist_km_centro_poblado != null) {
            stmt.bindString(5, dist_km_centro_poblado);
        }
 
        String clase_predio = entity.getClase_predio();
        if (clase_predio != null) {
            stmt.bindString(6, clase_predio);
        }
 
        String avaluo_catastral = entity.getAvaluo_catastral();
        if (avaluo_catastral != null) {
            stmt.bindString(7, avaluo_catastral);
        }
 
        String fec_avaluo_catastral = entity.getFec_avaluo_catastral();
        if (fec_avaluo_catastral != null) {
            stmt.bindString(8, fec_avaluo_catastral);
        }
 
        String avaluo_comercial = entity.getAvaluo_comercial();
        if (avaluo_comercial != null) {
            stmt.bindString(9, avaluo_comercial);
        }
 
        String fec_avaluo_comercial = entity.getFec_avaluo_comercial();
        if (fec_avaluo_comercial != null) {
            stmt.bindString(10, fec_avaluo_comercial);
        }
 
        String zona_aislamiento = entity.getZona_aislamiento();
        if (zona_aislamiento != null) {
            stmt.bindString(11, zona_aislamiento);
        }
 
        String zona_alto_riesgo = entity.getZona_alto_riesgo();
        if (zona_alto_riesgo != null) {
            stmt.bindString(12, zona_alto_riesgo);
        }
 
        String zona_proteccion = entity.getZona_proteccion();
        if (zona_proteccion != null) {
            stmt.bindString(13, zona_proteccion);
        }
 
        String topografia = entity.getTopografia();
        if (topografia != null) {
            stmt.bindString(14, topografia);
        }
 
        String propiedad_lote = entity.getPropiedad_lote();
        if (propiedad_lote != null) {
            stmt.bindString(15, propiedad_lote);
        }
 
        String tipo_documento = entity.getTipo_documento();
        if (tipo_documento != null) {
            stmt.bindString(16, tipo_documento);
        }
 
        String cual_tipo_documento = entity.getCual_tipo_documento();
        if (cual_tipo_documento != null) {
            stmt.bindString(17, cual_tipo_documento);
        }
 
        String nro_documento_legalizacion = entity.getNro_documento_legalizacion();
        if (nro_documento_legalizacion != null) {
            stmt.bindString(18, nro_documento_legalizacion);
        }
 
        String fec_expedicion = entity.getFec_expedicion();
        if (fec_expedicion != null) {
            stmt.bindString(19, fec_expedicion);
        }
 
        String notaria_dependencia_origen = entity.getNotaria_dependencia_origen();
        if (notaria_dependencia_origen != null) {
            stmt.bindString(20, notaria_dependencia_origen);
        }
 
        String lugar_expedicion = entity.getLugar_expedicion();
        if (lugar_expedicion != null) {
            stmt.bindString(21, lugar_expedicion);
        }
 
        String registro_catastral = entity.getRegistro_catastral();
        if (registro_catastral != null) {
            stmt.bindString(22, registro_catastral);
        }
 
        String matricula_inmobiliaria = entity.getMatricula_inmobiliaria();
        if (matricula_inmobiliaria != null) {
            stmt.bindString(23, matricula_inmobiliaria);
        }
 
        String propietarios = entity.getPropietarios();
        if (propietarios != null) {
            stmt.bindString(24, propietarios);
        }
 
        String tenencia = entity.getTenencia();
        if (tenencia != null) {
            stmt.bindString(25, tenencia);
        }
 
        String con_quien_tenencia = entity.getCon_quien_tenencia();
        if (con_quien_tenencia != null) {
            stmt.bindString(26, con_quien_tenencia);
        }
 
        String nom_quien_tenencia = entity.getNom_quien_tenencia();
        if (nom_quien_tenencia != null) {
            stmt.bindString(27, nom_quien_tenencia);
        }
 
        String fecha_tenencia_lote = entity.getFecha_tenencia_lote();
        if (fecha_tenencia_lote != null) {
            stmt.bindString(28, fecha_tenencia_lote);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    @Override
    public Predial readEntity(Cursor cursor, int offset) {
        Predial entity = new Predial( //
            cursor.getString(offset + 0), // dane_sede
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // cod_predio
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // clima
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // distancia_mts_sede_ppal
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // dist_km_centro_poblado
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // clase_predio
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // avaluo_catastral
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // fec_avaluo_catastral
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // avaluo_comercial
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // fec_avaluo_comercial
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // zona_aislamiento
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // zona_alto_riesgo
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // zona_proteccion
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // topografia
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // propiedad_lote
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // tipo_documento
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // cual_tipo_documento
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // nro_documento_legalizacion
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // fec_expedicion
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // notaria_dependencia_origen
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // lugar_expedicion
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // registro_catastral
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // matricula_inmobiliaria
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // propietarios
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // tenencia
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // con_quien_tenencia
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // nom_quien_tenencia
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27) // fecha_tenencia_lote
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Predial entity, int offset) {
        entity.setDane_sede(cursor.getString(offset + 0));
        entity.setCod_predio(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setClima(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDistancia_mts_sede_ppal(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDist_km_centro_poblado(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setClase_predio(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAvaluo_catastral(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setFec_avaluo_catastral(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAvaluo_comercial(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFec_avaluo_comercial(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setZona_aislamiento(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setZona_alto_riesgo(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setZona_proteccion(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setTopografia(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setPropiedad_lote(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setTipo_documento(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setCual_tipo_documento(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setNro_documento_legalizacion(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setFec_expedicion(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setNotaria_dependencia_origen(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setLugar_expedicion(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setRegistro_catastral(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setMatricula_inmobiliaria(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setPropietarios(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setTenencia(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setCon_quien_tenencia(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setNom_quien_tenencia(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setFecha_tenencia_lote(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Predial entity, long rowId) {
        return entity.getDane_sede();
    }
    
    @Override
    public String getKey(Predial entity) {
        if(entity != null) {
            return entity.getDane_sede();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Predial entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
