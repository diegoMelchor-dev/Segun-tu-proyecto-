package vallegrande.edu.pe.sistemas.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Lista los contactos, los más recientes primero
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, correo, telefono, motivo, mensaje, fecha "
                + "FROM contactos ORDER BY fecha DESC";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("telefono"));
                u.setMotivo(rs.getString("motivo"));
                u.setMensaje(rs.getString("mensaje"));
                u.setFecha(rs.getTimestamp("fecha"));
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Inserta un contacto (la fecha la pone la base de datos)
    public void insertar(Usuario u) {
        String sql = "INSERT INTO contactos (nombre, correo, telefono, motivo, mensaje) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getCorreo());
            stmt.setString(3, u.getTelefono());
            stmt.setString(4, u.getMotivo());
            stmt.setString(5, u.getMensaje());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualiza un contacto existente
    public void actualizar(Usuario u) {
        String sql = "UPDATE contactos SET nombre = ?, correo = ?, telefono = ?, "
                + "motivo = ?, mensaje = ? WHERE id = ?";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getCorreo());
            stmt.setString(3, u.getTelefono());
            stmt.setString(4, u.getMotivo());
            stmt.setString(5, u.getMensaje());
            stmt.setInt(6, u.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Elimina un contacto por su id
    public void eliminar(int id) {
        String sql = "DELETE FROM contactos WHERE id = ?";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}