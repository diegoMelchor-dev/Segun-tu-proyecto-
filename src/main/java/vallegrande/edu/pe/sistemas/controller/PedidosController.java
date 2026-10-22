package vallegrande.edu.pe.sistemas.controller;

import vallegrande.edu.pe.sistemas.model.PurchaseOrder;
import vallegrande.edu.pe.sistemas.model.PurchaseOrderDAO;
import vallegrande.edu.pe.sistemas.view.PedidosView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lógica del módulo Pedidos: conecta los botones de PedidosView con PurchaseOrderDAO.
 */
public class PedidosController {

    private final PedidosView view;
    private final PurchaseOrderDAO dao = new PurchaseOrderDAO();

    public PedidosController(PedidosView view) {
        this.view = view;
        configurarEventos();
    }

    private void configurarEventos() {
        view.getBtnRefrescar().setOnAction(e -> cargarPedidos());

        // Al elegir un pedido en la tabla, se cargan sus ítems y sus estados
        view.getTablaPedidos().getSelectionModel().selectedItemProperty().addListener((obs, anterior, sel) -> {
            if (sel == null) {
                view.mostrarItems(new ArrayList<>());
                return;
            }
            view.cargarEstados(sel);
            cargarItems(sel);
        });

        view.getBtnActualizar().setOnAction(e -> actualizarEstado());
        view.getBtnEliminar().setOnAction(e -> eliminarPedido());

        view.getBtnAgregarItem().setOnAction(e -> {
            try {
                view.agregarItem();
            } catch (IllegalArgumentException ex) {
                view.mostrarError(ex.getMessage());
            }
        });
        view.getBtnQuitarItem().setOnAction(e -> view.quitarItem());
        view.getBtnRegistrar().setOnAction(e -> registrarPedido());
    }

    /** Se llama al abrir el módulo y después de cada cambio. */
    public void cargarPedidos() {
        try {
            view.mostrarPedidos(dao.findAll());
        } catch (SQLException ex) {
            view.mostrarError("No se pudieron cargar los pedidos: " + ex.getMessage());
        }
    }

    private void cargarItems(PurchaseOrder pedido) {
        try {
            view.mostrarItems(dao.findItemsByOrderId(pedido.getOrderId()));
        } catch (SQLException ex) {
            view.mostrarError("No se pudieron cargar los ítems: " + ex.getMessage());
        }
    }

    private void registrarPedido() {
        try {
            PurchaseOrder pedido = view.leerFormulario();
            dao.insert(pedido, new ArrayList<>(view.getItemsNuevos()));
            view.limpiarFormulario();
            cargarPedidos();
            view.irAPestanaPedidos();
            view.mostrarInfo("Pedido registrado: " + pedido.getOrderNumber());
        } catch (IllegalArgumentException ex) {
            view.mostrarError(ex.getMessage());              // dato faltante o inválido
        } catch (SQLException ex) {
            view.mostrarError("No se pudo registrar el pedido: " + ex.getMessage());
        }
    }

    private void actualizarEstado() {
        PurchaseOrder pedido = view.getPedidoSeleccionado();
        if (pedido == null) {
            view.mostrarError("Selecciona un pedido de la tabla.");
            return;
        }
        String estadoPedido = view.getEstadoPedidoElegido();
        String estadoPago = view.getEstadoPagoElegido();
        if (estadoPedido == null || estadoPago == null) {
            view.mostrarError("Elige el estado del pedido y el estado de pago.");
            return;
        }
        try {
            dao.updateStatus(pedido.getOrderId(), estadoPedido, estadoPago);
            cargarPedidos();
            view.mostrarInfo("Pedido actualizado.");
        } catch (SQLException ex) {
            view.mostrarError("No se pudo actualizar: " + ex.getMessage());
        }
    }

    private void eliminarPedido() {
        PurchaseOrder pedido = view.getPedidoSeleccionado();
        if (pedido == null) {
            view.mostrarError("Selecciona un pedido de la tabla.");
            return;
        }
        if (!view.confirmar("¿Eliminar el pedido " + pedido.getOrderNumber() + " y todos sus ítems?")) {
            return;
        }
        try {
            dao.delete(pedido.getOrderId());
            cargarPedidos();
        } catch (SQLException ex) {
            view.mostrarError("No se pudo eliminar: " + ex.getMessage());
        }
    }
}
