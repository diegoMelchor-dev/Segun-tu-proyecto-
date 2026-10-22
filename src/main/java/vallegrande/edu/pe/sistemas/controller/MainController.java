package vallegrande.edu.pe.sistemas.controller;

import vallegrande.edu.pe.sistemas.model.Usuario;
import vallegrande.edu.pe.sistemas.model.UsuarioDAO;
import vallegrande.edu.pe.sistemas.view.MainView;

import java.util.List;

public class MainController {

    private MainView view;
    private UsuarioDAO usuarioDAO;
    private PedidosController pedidosController;

    public MainController(MainView view) {
        this.view = view;
        usuarioDAO = new UsuarioDAO();
        pedidosController = new PedidosController(view.getPedidosView());
        configurarEventos();
    }

    public void configurarEventos() {
        view.getBtnInicio().setOnAction(e -> {
            view.mostrarInicio();
        });

        view.getBtnUsuarios().setOnAction(e -> {
            view.mostrarUsuarios();
            cargarUsuarios();
        });

        view.getBtnPedidos().setOnAction(e -> {
            view.mostrarPedidos();
            pedidosController.cargarPedidos();
        });

        view.getBtnRegistrar().setOnAction(e -> {
            registrarUsuario();
        });

        view.getBtnActualizar().setOnAction(e -> {
            actualizarUsuario();
        });

        view.getBtnEliminar().setOnAction(e -> {
            eliminarUsuario();
        });

        view.getTablaUsuarios().setOnMouseClicked(e -> {
            Usuario usuario = view.getUsuarioSeleccionado();
            if (usuario != null) {
                view.cargarUsuarioEnFormulario(usuario);
            }
        });
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.listar();
        view.mostrarDatosUsuarios(usuarios);
    }

    private void registrarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre(view.getNombre());
        usuario.setCorreo(view.getCorreo());
        usuario.setTelefono(view.getTelefono());
        usuario.setMotivo(view.getMotivo());
        usuario.setMensaje(view.getMensaje());
        usuarioDAO.insertar(usuario);
        cargarUsuarios();
        view.limpiarFormulario();
    }

    private void actualizarUsuario() {
        Usuario usuario = view.getUsuarioSeleccionado();

        if (usuario == null) {
            return;
        }

        usuario.setNombre(view.getNombre());
        usuario.setCorreo(view.getCorreo());
        usuario.setTelefono(view.getTelefono());
        usuario.setMotivo(view.getMotivo());
        usuario.setMensaje(view.getMensaje());

        usuarioDAO.actualizar(usuario);
        cargarUsuarios();
        view.limpiarFormulario();
    }

    private void eliminarUsuario() {
        Usuario usuario = view.getUsuarioSeleccionado();

        if (usuario == null) {
            return;
        }

        usuarioDAO.eliminar(usuario.getId());
        cargarUsuarios();
        view.limpiarFormulario();
    }
}