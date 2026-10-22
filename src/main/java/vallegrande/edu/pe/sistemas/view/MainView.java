package vallegrande.edu.pe.sistemas.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vallegrande.edu.pe.sistemas.model.Usuario;

import java.util.List;
import java.util.function.Function;

public class MainView {

    @FXML private Button btnInicio;
    @FXML private Button btnUsuarios;
    @FXML private Button btnPedidos;
    @FXML private Button btnRegistrar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    @FXML private VBox panelInicio;
    @FXML private VBox panelUsuarios;
    @FXML private VBox panelPedidos;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbMotivo;
    @FXML private TextArea txtMensaje;

    @FXML private TableView<Usuario> tablaUsuarios;

    private PedidosView pedidosView;

    @FXML
    public void initialize() {
        cmbMotivo.setItems(FXCollections.observableArrayList(
                "Tengo una consulta",
                "Quiero comprar cuys",
                "Quiero comprar quinua",
                "Quiero información sobre los productos"));

        // El mensaje admite máximo 500 caracteres, igual que en la web
        txtMensaje.setTextFormatter(new TextFormatter<String>(
                c -> c.getControlNewText().length() <= 500 ? c : null));

        tablaUsuarios.getColumns().addAll(
                col("ID", u -> String.valueOf(u.getId())),
                col("Nombre", Usuario::getNombre),
                col("Correo", Usuario::getCorreo),
                col("Teléfono", Usuario::getTelefono),
                col("Motivo", Usuario::getMotivo),
                col("Mensaje", Usuario::getMensaje),
                col("Fecha", u -> String.valueOf(u.getFecha())));
        tablaUsuarios.setPlaceholder(new javafx.scene.control.Label("Sin contactos"));

        pedidosView = new PedidosView();
        VBox.setVgrow(pedidosView, Priority.ALWAYS);
        panelPedidos.getChildren().add(pedidosView);
    }

    // ---------- Navegación ----------

    public void mostrarInicio() { mostrarSolo(panelInicio); }

    public void mostrarUsuarios() { mostrarSolo(panelUsuarios); }

    public void mostrarPedidos() { mostrarSolo(panelPedidos); }

    private void mostrarSolo(VBox panel) {
        for (VBox p : new VBox[]{panelInicio, panelUsuarios, panelPedidos}) {
            boolean activo = (p == panel);
            p.setVisible(activo);
            p.setManaged(activo);
        }
    }

    // ---------- Datos de la tabla ----------

    public void mostrarDatosUsuarios(List<Usuario> usuarios) {
        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    public Usuario getUsuarioSeleccionado() {
        return tablaUsuarios.getSelectionModel().getSelectedItem();
    }

    public void cargarUsuarioEnFormulario(Usuario u) {
        txtNombre.setText(u.getNombre());
        txtCorreo.setText(u.getCorreo());
        txtTelefono.setText(u.getTelefono());
        cmbMotivo.setValue(u.getMotivo());
        txtMensaje.setText(u.getMensaje());
    }

    public void limpiarFormulario() {
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        cmbMotivo.setValue(null);
        txtMensaje.clear();
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    // ---------- Getters del formulario ----------

    public String getNombre() { return txtNombre.getText().trim(); }
    public String getCorreo() { return txtCorreo.getText().trim(); }
    public String getTelefono() { return txtTelefono.getText().trim(); }
    public String getMotivo() { return cmbMotivo.getValue(); }
    public String getMensaje() { return txtMensaje.getText().trim(); }

    // ---------- Getters para el controlador ----------

    public Button getBtnInicio() { return btnInicio; }
    public Button getBtnUsuarios() { return btnUsuarios; }
    public Button getBtnPedidos() { return btnPedidos; }
    public Button getBtnRegistrar() { return btnRegistrar; }
    public Button getBtnActualizar() { return btnActualizar; }
    public Button getBtnEliminar() { return btnEliminar; }
    public TableView<Usuario> getTablaUsuarios() { return tablaUsuarios; }
    public PedidosView getPedidosView() { return pedidosView; }

    // ---------- Utilidad ----------

    private TableColumn<Usuario, String> col(String titulo, Function<Usuario, String> f) {
        TableColumn<Usuario, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(d -> new SimpleStringProperty(f.apply(d.getValue())));
        return c;
    }
}