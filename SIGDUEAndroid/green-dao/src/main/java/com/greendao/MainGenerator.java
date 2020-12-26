package com.greendao;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class MainGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir");
    private static Entity departamento;
    private static Entity parqueadero;
    private static Entity zonas;
    private static Entity tipoIdentificacion;
    private static Entity grua;
    private static Entity color;
    private static Entity claseVehiculo;
    private static Entity tipoServicio;
    private static Entity municipio;
    private static Entity infraccion;
    private static Entity persona;
    private static Entity vehiculo;
    private static Entity inmovilizacion;


    public static void main(String[] args) {
        Schema schema = new Schema(14, "com.inmovilizaciones.db");
        schema.enableKeepSectionsByDefault();
        addTables(schema);
        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "\\app\\src\\main\\java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        departamento = addDepartamento(schema);
        municipio = addMunicipio(schema);
        infraccion = addInfraccion(schema);
        parqueadero = addParqueadero(schema);
        zonas = addZonas(schema);
        tipoIdentificacion = addTipoIdentificacion(schema);
        grua = addGrua(schema);
        color = addColor(schema);
        tipoServicio = addTipoServicio(schema);
        claseVehiculo = addClaseVehiculo(schema);
        vehiculo = addVehiculo(schema);
        persona = addPersonas(schema);
        inmovilizacion = addInmovilizacion(schema);
    }

    private static Entity addDepartamento(Schema schema) {
        Entity departamento = schema.addEntity("Departamento");
        departamento.addStringProperty("id_departamento").primaryKey().notNull();
        departamento.addStringProperty("nom_departamento");
        return departamento;
    }

    private static Entity addMunicipio(Schema schema) {
        Entity municipio = schema.addEntity("Municipio");
        municipio.addStringProperty("id_municipio").primaryKey().notNull();
        Property id_departamento = municipio.addStringProperty("id_departamento").getProperty();
        municipio.addToOne(departamento, id_departamento, "departamento");
        municipio.addStringProperty("nom_municipio");
        return municipio;
    }

    private static Entity addInfraccion(Schema schema) {
        Entity infraccion = schema.addEntity("Infraccion");
        infraccion.addLongProperty("id_infraccion").primaryKey().notNull();
        infraccion.addStringProperty("codigo").unique();
        infraccion.addStringProperty("descripcion");
        infraccion.addStringProperty("inmovilizacion");
        infraccion.addLongProperty("R1");
        infraccion.addLongProperty("R2");
        infraccion.addLongProperty("R3");
        infraccion.addLongProperty("R4");
        infraccion.addLongProperty("R5");
        infraccion.addLongProperty("R6");
        return infraccion;
    }

    private static Entity addParqueadero(Schema schema) {
        Entity parqueadero = schema.addEntity("Parqueadero");
        parqueadero.addLongProperty("id_parqueadero").primaryKey().notNull();
        parqueadero.addStringProperty("nombre");
        parqueadero.addStringProperty("direccion");
        parqueadero.addStringProperty("telefono");
        Property id_municipio = parqueadero.addStringProperty("id_municipio").getProperty();
        parqueadero.addToOne(municipio, id_municipio, "municipio");
        return parqueadero;
    }

    private static Entity addZonas(Schema schema) {
        Entity zonas = schema.addEntity("Zonas");
        zonas.addLongProperty("id_zona").primaryKey().notNull();
        zonas.addStringProperty("nom_zona");
        zonas.addStringProperty("descripcion");
        Property id_municipio = zonas.addStringProperty("id_municipio").getProperty();
        zonas.addToOne(municipio, id_municipio, "municipio");
        return zonas;
    }

    private static Entity addTipoIdentificacion(Schema schema) {
        Entity tipoIdentificacion = schema.addEntity("TipoIdentificacion");
        tipoIdentificacion.addStringProperty("id_tipo_identificacion").primaryKey().notNull();
        tipoIdentificacion.addStringProperty("nombre");
        return tipoIdentificacion;
    }

    private static Entity addGrua(Schema schema) {
        Entity grua = schema.addEntity("Grua");
        grua.addLongProperty("id_grua").primaryKey().notNull();
        grua.addStringProperty("placa").unique();
        grua.addStringProperty("no_grua");
        return grua;
    }

    private static Entity addColor(Schema schema) {
        Entity color = schema.addEntity("Color");
        color.addLongProperty("id_color").primaryKey().notNull();
        color.addStringProperty("nom_color");
        return color;
    }

    private static Entity addTipoServicio(Schema schema) {
        Entity tipoServicio = schema.addEntity("TipoServicio");
        tipoServicio.addLongProperty("id_tipo_servicio").primaryKey().notNull();
        tipoServicio.addStringProperty("nom_tipo_servicio");
        return tipoServicio;
    }

    private static Entity addClaseVehiculo(Schema schema) {
        Entity tipoServicio = schema.addEntity("ClaseVehiculo");
        tipoServicio.addLongProperty("id_clase_vehiculo").primaryKey().notNull();
        tipoServicio.addStringProperty("nom_clase_vehiculo");
        return tipoServicio;
    }

    private static Entity addVehiculo(Schema schema) {
        Entity vehiculo = schema.addEntity("Vehiculo");
        vehiculo.addLongProperty("id_vehiculo").primaryKey().notNull();
        vehiculo.addStringProperty("placa").unique();
        vehiculo.addStringProperty("no_motor").unique();
        vehiculo.addStringProperty("no_chasis").unique();
        vehiculo.addStringProperty("no_serie").unique();
        Property id_clase_vehiculo = vehiculo.addLongProperty("id_clase_vehiculo").getProperty();
        vehiculo.addToOne(claseVehiculo, id_clase_vehiculo, "claseVehiculo");
        Property id_tipo_servicio = vehiculo.addLongProperty("id_tipo_servicio").getProperty();
        vehiculo.addToOne(tipoServicio, id_tipo_servicio, "tipoServicio");
        vehiculo.addStringProperty("marca");
        vehiculo.addStringProperty("linea");
        vehiculo.addLongProperty("modelo");
        Property id_color = vehiculo.addLongProperty("id_color").getProperty();
        vehiculo.addToOne(color, id_color, "color");
        return vehiculo;
    }


    private static Entity addPersonas(Schema schema) {
        Entity persona = schema.addEntity("Persona");
        persona.addLongProperty("id_persona").primaryKey().notNull();
        persona.addStringProperty("propietario");
        persona.addStringProperty("placa");
        persona.addStringProperty("login");
        persona.addStringProperty("password");
        Property id_tipo_identificacion = persona.addStringProperty("id_tipo_identificacion").getProperty();
        persona.addToOne(tipoIdentificacion, id_tipo_identificacion, "tipoIdentificacion");
        persona.addStringProperty("no_identificacion").unique();
        persona.addStringProperty("nombre1");
        persona.addStringProperty("nombre2");
        persona.addStringProperty("apellido1");
        persona.addStringProperty("apellido2");
        persona.addStringProperty("telefono");
        persona.addStringProperty("direccion");
        persona.addStringProperty("tipo");
        persona.addStringProperty("estado");
        persona.addLongProperty("id_dispositivo");
        persona.addStringProperty("nom_dispositivo");
        Property id_municipio = persona.addStringProperty("id_municipio").getProperty();
        persona.addToOne(municipio, id_municipio, "municipio");
        return persona;
    }

    private static Entity addInmovilizacion(Schema schema) {
        Entity inmovilizacion = schema.addEntity("Inmovilizacion");
        inmovilizacion.addLongProperty("id_inmovilizacion").primaryKey().notNull();
        Property id_usuario = inmovilizacion.addLongProperty("id_usuario").getProperty();
        inmovilizacion.addToOne(persona, id_usuario, "usuario");
        Property id_vehiculo = inmovilizacion.addLongProperty("id_vehiculo").getProperty();
        inmovilizacion.addToOne(vehiculo, id_vehiculo, "vehiculo");
        Property id_infractor = inmovilizacion.addLongProperty("id_infractor").getProperty();
        inmovilizacion.addToOne(persona, id_infractor, "infractor");
        inmovilizacion.addStringProperty("propietario_presente");
        inmovilizacion.addLongProperty("no_comparendo");
        Property id_infraccion = inmovilizacion.addLongProperty("id_infraccion").getProperty();
        inmovilizacion.addToOne(infraccion, id_infraccion, "infraccion");
        Property id_agente = inmovilizacion.addLongProperty("id_agente").getProperty();
        inmovilizacion.addToOne(persona, id_agente, "agente");
        Property id_grua = inmovilizacion.addLongProperty("id_grua").getProperty();
        inmovilizacion.addToOne(grua, id_grua, "grua");
        Property id_zona = inmovilizacion.addLongProperty("id_zona").getProperty();
        inmovilizacion.addToOne(zonas, id_zona, "zona");
        Property id_parqueadero = inmovilizacion.addLongProperty("id_parqueadero").getProperty();
        inmovilizacion.addToOne(parqueadero, id_parqueadero, "parqueadero");
        inmovilizacion.addDateProperty("fec_ini_inm");
        inmovilizacion.addDateProperty("fech_fin_inm");
        inmovilizacion.addStringProperty("observacion");
        inmovilizacion.addStringProperty("direccion");
        inmovilizacion.addStringProperty("estado");
        inmovilizacion.addStringProperty("salida_transito");
        inmovilizacion.addStringProperty("salida_parqueadero");
        inmovilizacion.addStringProperty("desenganche");
        return inmovilizacion;
    }

}
