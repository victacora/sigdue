package com.sigdue.db;

import org.greenrobot.greendao.annotation.*;

import org.greenrobot.greendao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "VEHICULO".
 */
@Entity(active = true)
public class Vehiculo {

    @Id
    private long id_vehiculo;

    @Unique
    private String placa;

    @Unique
    private String no_motor;

    @Unique
    private String no_chasis;

    @Unique
    private String no_serie;
    private Long id_clase_vehiculo;
    private Long id_tipo_servicio;
    private String marca;
    private String linea;
    private Long modelo;
    private Long id_color;

    /** Used to resolve relations */
    @Generated
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated
    private transient VehiculoDao myDao;

    @ToOne(joinProperty = "id_clase_vehiculo")
    private ClaseVehiculo claseVehiculo;

    @Generated
    private transient Long claseVehiculo__resolvedKey;

    @ToOne(joinProperty = "id_tipo_servicio")
    private TipoServicio tipoServicio;

    @Generated
    private transient Long tipoServicio__resolvedKey;

    @ToOne(joinProperty = "id_color")
    private Color color;

    @Generated
    private transient Long color__resolvedKey;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    @Generated
    public Vehiculo() {
    }

    public Vehiculo(long id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    @Generated
    public Vehiculo(long id_vehiculo, String placa, String no_motor, String no_chasis, String no_serie, Long id_clase_vehiculo, Long id_tipo_servicio, String marca, String linea, Long modelo, Long id_color) {
        this.id_vehiculo = id_vehiculo;
        this.placa = placa;
        this.no_motor = no_motor;
        this.no_chasis = no_chasis;
        this.no_serie = no_serie;
        this.id_clase_vehiculo = id_clase_vehiculo;
        this.id_tipo_servicio = id_tipo_servicio;
        this.marca = marca;
        this.linea = linea;
        this.modelo = modelo;
        this.id_color = id_color;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVehiculoDao() : null;
    }

    public long getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(long id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNo_motor() {
        return no_motor;
    }

    public void setNo_motor(String no_motor) {
        this.no_motor = no_motor;
    }

    public String getNo_chasis() {
        return no_chasis;
    }

    public void setNo_chasis(String no_chasis) {
        this.no_chasis = no_chasis;
    }

    public String getNo_serie() {
        return no_serie;
    }

    public void setNo_serie(String no_serie) {
        this.no_serie = no_serie;
    }

    public Long getId_clase_vehiculo() {
        return id_clase_vehiculo;
    }

    public void setId_clase_vehiculo(Long id_clase_vehiculo) {
        this.id_clase_vehiculo = id_clase_vehiculo;
    }

    public Long getId_tipo_servicio() {
        return id_tipo_servicio;
    }

    public void setId_tipo_servicio(Long id_tipo_servicio) {
        this.id_tipo_servicio = id_tipo_servicio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public Long getModelo() {
        return modelo;
    }

    public void setModelo(Long modelo) {
        this.modelo = modelo;
    }

    public Long getId_color() {
        return id_color;
    }

    public void setId_color(Long id_color) {
        this.id_color = id_color;
    }

    /** To-one relationship, resolved on first access. */
    @Generated
    public ClaseVehiculo getClaseVehiculo() {
        Long __key = this.id_clase_vehiculo;
        if (claseVehiculo__resolvedKey == null || !claseVehiculo__resolvedKey.equals(__key)) {
            __throwIfDetached();
            ClaseVehiculoDao targetDao = daoSession.getClaseVehiculoDao();
            ClaseVehiculo claseVehiculoNew = targetDao.load(__key);
            synchronized (this) {
                claseVehiculo = claseVehiculoNew;
            	claseVehiculo__resolvedKey = __key;
            }
        }
        return claseVehiculo;
    }

    @Generated
    public void setClaseVehiculo(ClaseVehiculo claseVehiculo) {
        synchronized (this) {
            this.claseVehiculo = claseVehiculo;
            id_clase_vehiculo = claseVehiculo == null ? null : claseVehiculo.getId_clase_vehiculo();
            claseVehiculo__resolvedKey = id_clase_vehiculo;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated
    public TipoServicio getTipoServicio() {
        Long __key = this.id_tipo_servicio;
        if (tipoServicio__resolvedKey == null || !tipoServicio__resolvedKey.equals(__key)) {
            __throwIfDetached();
            TipoServicioDao targetDao = daoSession.getTipoServicioDao();
            TipoServicio tipoServicioNew = targetDao.load(__key);
            synchronized (this) {
                tipoServicio = tipoServicioNew;
            	tipoServicio__resolvedKey = __key;
            }
        }
        return tipoServicio;
    }

    @Generated
    public void setTipoServicio(TipoServicio tipoServicio) {
        synchronized (this) {
            this.tipoServicio = tipoServicio;
            id_tipo_servicio = tipoServicio == null ? null : tipoServicio.getId_tipo_servicio();
            tipoServicio__resolvedKey = id_tipo_servicio;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated
    public Color getColor() {
        Long __key = this.id_color;
        if (color__resolvedKey == null || !color__resolvedKey.equals(__key)) {
            __throwIfDetached();
            ColorDao targetDao = daoSession.getColorDao();
            Color colorNew = targetDao.load(__key);
            synchronized (this) {
                color = colorNew;
            	color__resolvedKey = __key;
            }
        }
        return color;
    }

    @Generated
    public void setColor(Color color) {
        synchronized (this) {
            this.color = color;
            id_color = color == null ? null : color.getId_color();
            color__resolvedKey = id_color;
        }
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void delete() {
        __throwIfDetached();
        myDao.delete(this);
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void update() {
        __throwIfDetached();
        myDao.update(this);
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void refresh() {
        __throwIfDetached();
        myDao.refresh(this);
    }

    @Generated
    private void __throwIfDetached() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
