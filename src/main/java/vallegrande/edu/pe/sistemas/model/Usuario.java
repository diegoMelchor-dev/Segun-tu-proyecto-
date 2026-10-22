package vallegrande.edu.pe.sistemas.model;

import java.sql.Timestamp;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private String motivo;
    private String mensaje;
    private Timestamp fecha;

    public Usuario() {}

    public Usuario(int id, String nombre, String correo, String telefono,
                   String motivo, String mensaje, Timestamp fecha) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.motivo = motivo;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}