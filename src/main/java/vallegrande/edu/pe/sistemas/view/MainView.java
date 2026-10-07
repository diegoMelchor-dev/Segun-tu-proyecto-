package vallegrande.edu.pe.sistemas.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import vallegrande.edu.pe.sistemas.model.Usuario;

import java.util.List;

public class MainView {

    @FXML private Button btnInicio;
    @FXML private Button btnUsuarios;
    @FXML private Button btnRegistrar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtEstado;

    @FXML private VBox panelInicio;
    @FXML private VBox panelUsuarios;

    @FXML private TableView<Usuario> tablaUsuarios;

    // JavaFX llama a este método automáticamente al cargar el FXML
    @FXML
    public void initialize() {
        crearColumnas();
        mostrarInicio();
    }

    private void crearColumnas() {
        TableColumn<Usuario, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> colApellido = new TableColumn<>("Apellido");
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        TableColumn<Usuario, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        TableColumn<Usuario, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaUsuarios.getColumns().addAll(colId, colNombre, colApellido, colCorreo, colEstado);
    }

    // ---------- Navegación ----------
    public void mostrarInicio() {
        mostrarPanel(panelInicio, panelUsuarios);
    }

    public void mostrarUsuarios() {
        mostrarPanel(panelUsuarios, panelInicio);
    }

    private void mostrarPanel(VBox mostrar, VBox ocultar) {
        mostrar.setVisible(true);
        mostrar.setManaged(true);
        ocultar.setVisible(false);
        ocultar.setManaged(false);
    }

    // ---------- Tabla ----------
    public void mostrarDatosUsuarios(List<Usuario> usuarios) {
        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    public Usuario getUsuarioSeleccionado() {
        return tablaUsuarios.getSelectionModel().getSelectedItem();
    }

    public TableView<Usuario> getTablaUsuarios() {
        return tablaUsuarios;
    }

    // ---------- Formulario ----------
    public void cargarUsuarioEnFormulario(Usuario usuario) {
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtCorreo.setText(usuario.getCorreo());
        txtEstado.setText(usuario.getEstado());
    }

    public void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
        txtEstado.clear();
    }

    public String getNombre()   { return txtNombre.getText(); }
    public String getApellido() { return txtApellido.getText(); }
    public String getCorreo()   { return txtCorreo.getText(); }
    public String getEstado()   { return txtEstado.getText(); }

    // ---------- Botones ----------
    public Button getBtnInicio()      { return btnInicio; }
    public Button getBtnUsuarios()    { return btnUsuarios; }
    public Button getBtnRegistrar()   { return btnRegistrar; }
    public Button getBtnActualizar()  { return btnActualizar; }
    public Button getBtnEliminar()    { return btnEliminar; }
}
