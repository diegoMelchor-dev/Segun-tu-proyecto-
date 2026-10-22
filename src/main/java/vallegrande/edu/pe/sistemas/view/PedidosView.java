package vallegrande.edu.pe.sistemas.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vallegrande.edu.pe.sistemas.model.PurchaseOrder;
import vallegrande.edu.pe.sistemas.model.PurchaseOrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;

/**
 * Panel del módulo Pedidos. Se construye en código (sin FXML) y se agrega
 * dentro de panelPedidos en MainView. La lógica de los botones está en PedidosController.
 */
public class PedidosView extends VBox {

    // ---------- Pestaña "Pedidos" ----------
    private final TableView<PurchaseOrder> tablaPedidos = new TableView<>();
    private final TableView<PurchaseOrderItem> tablaItems = new TableView<>();
    private final ComboBox<String> cmbEstadoPedido = new ComboBox<>(FXCollections.observableArrayList(
            "RECEIVED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"));
    private final ComboBox<String> cmbEstadoPago = new ComboBox<>(FXCollections.observableArrayList(
            "PENDING", "PAID", "REFUNDED"));
    private final Button btnActualizar = boton("Actualizar", "#EAB308");
    private final Button btnEliminar = boton("Eliminar", "#DC2626");
    private final Button btnRefrescar = boton("Refrescar", "#2563EB");

    // ---------- Pestaña "Nuevo pedido" ----------
    private final TextField txtCliente = new TextField();
    private final TextField txtEmail = new TextField();
    private final TextField txtTelefono = new TextField();
    private final TextField txtDireccion = new TextField();
    private final TextField txtDistrito = new TextField();
    private final TextField txtCiudad = new TextField();
    private final TextField txtDepartamento = new TextField();
    private final TextField txtComentario = new TextField();
    private final TextField txtEnvio = new TextField("0.00");
    private final TextField txtDescuento = new TextField("0.00");
    private final ComboBox<String> cmbEntrega = new ComboBox<>(FXCollections.observableArrayList(
            "delivery", "pickup"));
    private final ComboBox<String> cmbPago = new ComboBox<>(FXCollections.observableArrayList(
            "transferencia", "yape", "contra-entrega"));

    private final TextField txtProducto = new TextField();
    private final TextField txtCantidad = new TextField();
    private final TextField txtPrecio = new TextField();
    private final Button btnAgregarItem = boton("Agregar ítem", "#2563EB");
    private final Button btnQuitarItem = boton("Quitar ítem", "#64748B");
    private final Button btnRegistrar = boton("Registrar pedido", "#16A34A");
    private final TableView<PurchaseOrderItem> tablaNuevosItems = new TableView<>();
    private final ObservableList<PurchaseOrderItem> itemsNuevos = FXCollections.observableArrayList();
    private final Label lblTotal = new Label();

    private final TabPane tabs = new TabPane();

    public PedidosView() {
        setSpacing(15);

        Label titulo = new Label("PEDIDOS");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Tab tabLista = new Tab("Pedidos", construirTabLista());
        Tab tabNuevo = new Tab("Nuevo pedido", construirTabNuevo());
        tabs.getTabs().addAll(tabLista, tabNuevo);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        getChildren().addAll(titulo, tabs);
    }

    // =====================================================================
    //  Construcción de pestañas
    // =====================================================================

    private VBox construirTabLista() {
        tablaPedidos.getColumns().addAll(
                colP("N° pedido", PurchaseOrder::getOrderNumber),
                colP("Cliente", PurchaseOrder::getCustomerName),
                colP("Método de pago", PurchaseOrder::getPaymentMethod),
                colP("Total (S/)", o -> String.valueOf(o.getTotalAmount())),
                colP("Estado", PurchaseOrder::getOrderStatus),
                colP("Estado de pago", PurchaseOrder::getPaymentStatus),
                colP("Fecha", o -> String.valueOf(o.getCreatedAt())));
        tablaPedidos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaPedidos.setPrefHeight(220);
        VBox.setVgrow(tablaPedidos, Priority.ALWAYS);

        configurarColumnasItems(tablaItems);
        tablaItems.setPrefHeight(130);
        tablaItems.setPlaceholder(new Label("Selecciona un pedido para ver sus ítems"));

        cmbEstadoPedido.setPromptText("Estado del pedido");
        cmbEstadoPago.setPromptText("Estado de pago");

        cmbEstadoPedido.setPrefWidth(130);
        cmbEstadoPago.setPrefWidth(110);
        HBox acciones = new HBox(10,
                new Label("Pedido:"), cmbEstadoPedido,
                new Label("Pago:"), cmbEstadoPago,
                btnActualizar, btnEliminar, btnRefrescar);
        acciones.setPadding(new Insets(5, 0, 0, 0));

        Label lblItems = new Label("Ítems del pedido seleccionado");
        lblItems.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");

        VBox caja = new VBox(10, tablaPedidos, lblItems, tablaItems, acciones);
        caja.setPadding(new Insets(10, 0, 0, 0));
        return caja;
    }

    private Node construirTabNuevo() {
        txtCliente.setPromptText("Nombre completo");
        txtEmail.setPromptText("correo@ejemplo.com");
        txtTelefono.setPromptText("Teléfono");
        txtDireccion.setPromptText("Dirección de entrega");
        txtDistrito.setPromptText("Distrito");
        txtCiudad.setPromptText("Ciudad");
        txtDepartamento.setPromptText("Departamento");
        txtComentario.setPromptText("Opcional");
        cmbEntrega.setPromptText("Selecciona");
        cmbPago.setPromptText("Selecciona");

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        fila(g, 0, "Cliente *", txtCliente, "Correo *", txtEmail);
        fila(g, 1, "Teléfono *", txtTelefono, "Dirección *", txtDireccion);
        fila(g, 2, "Distrito *", txtDistrito, "Ciudad *", txtCiudad);
        fila(g, 3, "Departamento *", txtDepartamento, "Método de entrega *", cmbEntrega);
        fila(g, 4, "Método de pago *", cmbPago, "Comentario", txtComentario);
        fila(g, 5, "Envío (S/)", txtEnvio, "Descuento (S/)", txtDescuento);

        // --- ítems del nuevo pedido ---
        txtProducto.setPromptText("Producto (ej. Cuy)");
        txtCantidad.setPromptText("Cantidad");
        txtPrecio.setPromptText("Precio unitario");
        txtProducto.setPrefWidth(220);
        txtCantidad.setPrefWidth(100);
        txtPrecio.setPrefWidth(120);

        HBox filaItem = new HBox(10, txtProducto, txtCantidad, txtPrecio, btnAgregarItem, btnQuitarItem);

        configurarColumnasItems(tablaNuevosItems);
        tablaNuevosItems.setItems(itemsNuevos);
        tablaNuevosItems.setPrefHeight(130);
        tablaNuevosItems.setPlaceholder(new Label("Aún no agregaste ítems"));

        lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        itemsNuevos.addListener((javafx.collections.ListChangeListener<PurchaseOrderItem>) c -> actualizarTotal());
        txtEnvio.textProperty().addListener((obs, a, b) -> actualizarTotal());
        txtDescuento.textProperty().addListener((obs, a, b) -> actualizarTotal());
        actualizarTotal();

        Label lblItems = new Label("Ítems del pedido");
        lblItems.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");

        VBox contenido = new VBox(12, g, lblItems, filaItem, tablaNuevosItems, lblTotal, btnRegistrar);
        contenido.setPadding(new Insets(10, 10, 10, 0));

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private void fila(GridPane g, int fila, String l1, Node n1, String l2, Node n2) {
        g.add(new Label(l1), 0, fila);
        g.add(n1, 1, fila);
        g.add(new Label(l2), 2, fila);
        g.add(n2, 3, fila);
        GridPane.setHgrow(n1, Priority.ALWAYS);
        GridPane.setHgrow(n2, Priority.ALWAYS);
        if (n1 instanceof ComboBox) ((ComboBox<?>) n1).setMaxWidth(Double.MAX_VALUE);
        if (n2 instanceof ComboBox) ((ComboBox<?>) n2).setMaxWidth(Double.MAX_VALUE);
    }

    private void configurarColumnasItems(TableView<PurchaseOrderItem> tabla) {
        tabla.getColumns().addAll(
                colI("Producto", PurchaseOrderItem::getProductName),
                colI("Cantidad", i -> String.valueOf(i.getQuantity())),
                colI("Precio unit. (S/)", i -> String.valueOf(i.getUnitPrice())),
                colI("Total línea (S/)", i -> {
                    // line_total viene de la BD; en un pedido nuevo todavía no existe, se calcula
                    BigDecimal t = i.getLineTotal() != null
                            ? i.getLineTotal()
                            : i.getQuantity().multiply(i.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);
                    return t.toString();
                }));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // =====================================================================
    //  Datos que muestra la vista
    // =====================================================================

    public void mostrarPedidos(List<PurchaseOrder> pedidos) {
        tablaPedidos.setItems(FXCollections.observableArrayList(pedidos));
    }

    public void mostrarItems(List<PurchaseOrderItem> items) {
        tablaItems.setItems(FXCollections.observableArrayList(items));
    }

    public void cargarEstados(PurchaseOrder p) {
        cmbEstadoPedido.setValue(p.getOrderStatus());
        cmbEstadoPago.setValue(p.getPaymentStatus());
    }

    public PurchaseOrder getPedidoSeleccionado() {
        return tablaPedidos.getSelectionModel().getSelectedItem();
    }

    public String getEstadoPedidoElegido() { return cmbEstadoPedido.getValue(); }

    public String getEstadoPagoElegido() { return cmbEstadoPago.getValue(); }

    public void irAPestanaPedidos() { tabs.getSelectionModel().select(0); }

    // =====================================================================
    //  Formulario de nuevo pedido (valida y arma los objetos)
    // =====================================================================

    /** Agrega el ítem escrito a la lista del pedido nuevo. Lanza IllegalArgumentException si hay un dato inválido. */
    public void agregarItem() {
        String producto = requerido(txtProducto, "Producto", 150);
        BigDecimal cantidad = parseDecimal(txtCantidad.getText(), "Cantidad");
        BigDecimal precio = parseDecimal(txtPrecio.getText(), "Precio unitario");
        if (cantidad.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
        if (precio.signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        itemsNuevos.add(new PurchaseOrderItem(producto, cantidad, precio));
        txtProducto.clear();
        txtCantidad.clear();
        txtPrecio.clear();
        txtProducto.requestFocus();
    }

    public void quitarItem() {
        PurchaseOrderItem sel = tablaNuevosItems.getSelectionModel().getSelectedItem();
        if (sel != null) {
            itemsNuevos.remove(sel);
        }
    }

    /** Lee el formulario y devuelve el pedido listo para insertar. Lanza IllegalArgumentException si falta algo. */
    public PurchaseOrder leerFormulario() {
        PurchaseOrder o = new PurchaseOrder();
        o.setCustomerName(requerido(txtCliente, "Cliente", 150));

        String email = requerido(txtEmail, "Correo", 150);
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El correo no es válido.");
        }
        o.setCustomerEmail(email);

        o.setCustomerPhone(requerido(txtTelefono, "Teléfono", 30));
        o.setDeliveryAddress(requerido(txtDireccion, "Dirección", 200));
        o.setDeliveryDistrict(requerido(txtDistrito, "Distrito", 100));
        o.setDeliveryCity(requerido(txtCiudad, "Ciudad", 100));
        o.setDeliveryDepartment(requerido(txtDepartamento, "Departamento", 50));

        if (cmbEntrega.getValue() == null) {
            throw new IllegalArgumentException("Selecciona el método de entrega.");
        }
        if (cmbPago.getValue() == null) {
            throw new IllegalArgumentException("Selecciona el método de pago.");
        }
        o.setDeliveryMethod(cmbEntrega.getValue());
        o.setPaymentMethod(cmbPago.getValue());

        String comentario = txtComentario.getText() == null ? "" : txtComentario.getText().trim();
        if (comentario.length() > 500) {
            throw new IllegalArgumentException("El comentario admite máximo 500 caracteres.");
        }
        o.setCustomerComment(comentario.isEmpty() ? null : comentario);

        if (itemsNuevos.isEmpty()) {
            throw new IllegalArgumentException("Agrega al menos un ítem al pedido.");
        }

        BigDecimal envio = parseDecimal(txtEnvio.getText(), "Envío");
        BigDecimal descuento = parseDecimal(txtDescuento.getText(), "Descuento");
        if (envio.signum() < 0 || descuento.signum() < 0) {
            throw new IllegalArgumentException("Envío y descuento no pueden ser negativos.");
        }
        BigDecimal subtotal = calcularSubtotal();
        BigDecimal total = subtotal.add(envio).subtract(descuento);
        if (total.signum() < 0) {
            throw new IllegalArgumentException("El descuento no puede ser mayor que el subtotal más el envío.");
        }

        o.setOrderNumber("ADM-" + System.currentTimeMillis());   // ADM = creado desde la app de escritorio
        o.setPaymentStatus("PENDING");
        o.setOrderStatus("RECEIVED");
        o.setSubtotal(subtotal);
        o.setShippingCost(envio.setScale(2, RoundingMode.HALF_UP));
        o.setDiscountAmount(descuento.setScale(2, RoundingMode.HALF_UP));
        o.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        return o;
    }

    public ObservableList<PurchaseOrderItem> getItemsNuevos() { return itemsNuevos; }

    public void limpiarFormulario() {
        txtCliente.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtDistrito.clear();
        txtCiudad.clear();
        txtDepartamento.clear();
        txtComentario.clear();
        txtEnvio.setText("0.00");
        txtDescuento.setText("0.00");
        cmbEntrega.setValue(null);
        cmbPago.setValue(null);
        txtProducto.clear();
        txtCantidad.clear();
        txtPrecio.clear();
        itemsNuevos.clear();
        actualizarTotal();
    }

    // =====================================================================
    //  Mensajes
    // =====================================================================

    public void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public void mostrarInfo(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public boolean confirmar(String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    // =====================================================================
    //  Getters para el controlador
    // =====================================================================

    public TableView<PurchaseOrder> getTablaPedidos() { return tablaPedidos; }
    public Button getBtnActualizar() { return btnActualizar; }
    public Button getBtnEliminar() { return btnEliminar; }
    public Button getBtnRefrescar() { return btnRefrescar; }
    public Button getBtnAgregarItem() { return btnAgregarItem; }
    public Button getBtnQuitarItem() { return btnQuitarItem; }
    public Button getBtnRegistrar() { return btnRegistrar; }

    // =====================================================================
    //  Utilidades internas
    // =====================================================================

    private void actualizarTotal() {
        BigDecimal subtotal = calcularSubtotal();
        BigDecimal envio = decimalSuave(txtEnvio.getText());
        BigDecimal descuento = decimalSuave(txtDescuento.getText());
        BigDecimal total = subtotal.add(envio).subtract(descuento).setScale(2, RoundingMode.HALF_UP);
        lblTotal.setText("Subtotal: S/ " + subtotal + "     Total: S/ " + total);
    }

    private BigDecimal calcularSubtotal() {
        BigDecimal s = BigDecimal.ZERO;
        for (PurchaseOrderItem it : itemsNuevos) {
            s = s.add(it.getQuantity().multiply(it.getUnitPrice()));
        }
        return s.setScale(2, RoundingMode.HALF_UP);
    }

    /** Como parseDecimal pero sin lanzar error: si el texto no es válido devuelve 0 (solo para mostrar el total). */
    private BigDecimal decimalSuave(String texto) {
        try {
            return parseDecimal(texto, "valor");
        } catch (IllegalArgumentException e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal parseDecimal(String texto, String campo) {
        String t = texto == null ? "" : texto.trim().replace(',', '.');
        if (t.isEmpty()) {
            throw new IllegalArgumentException("Completa el campo: " + campo + ".");
        }
        try {
            return new BigDecimal(t);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número.");
        }
    }

    private String requerido(TextField campo, String nombre, int max) {
        String v = campo.getText() == null ? "" : campo.getText().trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Completa el campo: " + nombre + ".");
        }
        if (v.length() > max) {
            throw new IllegalArgumentException(nombre + " admite máximo " + max + " caracteres.");
        }
        return v;
    }

    private static Button boton(String texto, String color) {
        Button b = new Button(texto);
        b.setPrefHeight(34);
        b.setMinWidth(100);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        return b;
    }

    private TableColumn<PurchaseOrder, String> colP(String titulo, Function<PurchaseOrder, String> f) {
        TableColumn<PurchaseOrder, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(d -> new SimpleStringProperty(f.apply(d.getValue())));
        return c;
    }

    private TableColumn<PurchaseOrderItem, String> colI(String titulo, Function<PurchaseOrderItem, String> f) {
        TableColumn<PurchaseOrderItem, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(d -> new SimpleStringProperty(f.apply(d.getValue())));
        return c;
    }
}
