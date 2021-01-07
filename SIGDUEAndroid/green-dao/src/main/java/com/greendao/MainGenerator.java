package com.greendao;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MainGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir");
    private static Entity parametro;
    private static Entity predial;
    private static Entity usuario;
    private static Entity archivo;

    public static void main(String[] args) {
        Schema schema = new Schema(5, "com.sigdue.db");
        schema.enableKeepSectionsByDefault();
        addTables(schema);
        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "\\app\\src\\main\\java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        parametro = addParametro(schema);
        predial = addPredial(schema);
        archivo = addArchivos(schema);
        usuario = addUsuarios(schema);
    }

    private static Entity addParametro(Schema schema) {
        Entity parametro = schema.addEntity("Parametro");
        parametro.addLongProperty("id_parametro").primaryKey().notNull().autoincrement();
        parametro.addIntProperty("tipo");
        parametro.addStringProperty("parametro");
        return parametro;
    }

    private static Entity addPredial(Schema schema) {
        Entity predial = schema.addEntity("Predial");
        predial.addLongProperty("id_predial").primaryKey().notNull();
        predial.addStringProperty("dane_sede");
        predial.addStringProperty("cod_predio");
        predial.addStringProperty("clima");
        predial.addStringProperty("distancia_mts_sede_ppal");
        predial.addStringProperty("dist_km_centro_poblado");
        predial.addStringProperty("clase_predio");
        predial.addStringProperty("avaluo_catastral");
        predial.addStringProperty("fec_avaluo_catastral");
        predial.addStringProperty("avaluo_comercial");
        predial.addStringProperty("fec_avaluo_comercial");
        predial.addStringProperty("zona_aislamiento");
        predial.addStringProperty("zona_alto_riesgo");
        predial.addStringProperty("zona_proteccion");
        predial.addStringProperty("topografia");
        predial.addStringProperty("propiedad_lote");
        predial.addStringProperty("tipo_documento");
        predial.addStringProperty("cual_tipo_documento");
        predial.addStringProperty("nro_documento_legalizacion");
        predial.addStringProperty("fec_expedicion");
        predial.addStringProperty("notaria_dependencia_origen");
        predial.addStringProperty("lugar_expedicion");
        predial.addStringProperty("registro_catastral");
        predial.addStringProperty("matricula_inmobiliaria");
        predial.addStringProperty("propietarios");
        predial.addStringProperty("tenencia");
        predial.addStringProperty("con_quien_tenencia");
        predial.addStringProperty("nom_quien_tenencia");
        predial.addStringProperty("fecha_tenencia_lote");
        predial.addStringProperty("url_video");
        predial.addStringProperty("longitude");
        predial.addStringProperty("latitude");
        predial.addStringProperty("estado");
        predial.addStringProperty("id_bd");
        return predial;
    }

    private static Entity addUsuarios(Schema schema) {
        Entity usuario = schema.addEntity("Usuario");
        usuario.addLongProperty("id_usuario").primaryKey().notNull();
        usuario.addStringProperty("usuario").unique();
        usuario.addStringProperty("contrasena");
        return usuario;
    }

    private static Entity addArchivos(Schema schema) {
        Entity archivo = schema.addEntity("Archivo");
        archivo.addLongProperty("id_archivo").primaryKey().notNull();
        archivo.addLongProperty("id_predial");
        archivo.addStringProperty("ruta");
        return archivo;
    }
}
