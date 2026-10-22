package vallegrande.edu.pe.sistemas.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD de purchase_orders y purchase_order_items.
 *
 * IMPORTANTE: ajusta la línea de conexión (getConnection) a como la
 * tenga tu proyecto. Aquí se asume una clase Conexion con un método
 * estático getConnection(), igual que en UsuarioDAO.
 */
public class PurchaseOrderDAO {

    private Connection getConnection() throws SQLException {
        // TODO: reemplaza por tu clase de conexión si se llama distinto
        return Conexion.conectar();
    }

    // ---------- CONSULTAR (READ) ----------

    /** Lista todos los pedidos, el más reciente primero. */
    public List<PurchaseOrder> findAll() throws SQLException {
        String sql = "SELECT * FROM purchase_orders ORDER BY order_id DESC";
        List<PurchaseOrder> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        }
        return list;
    }

    /** Lista los ítems de un pedido. */
    public List<PurchaseOrderItem> findItemsByOrderId(long orderId) throws SQLException {
        String sql = "SELECT * FROM purchase_order_items WHERE order_id = ? ORDER BY item_id";
        List<PurchaseOrderItem> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        }
        return list;
    }

    // ---------- REGISTRAR (CREATE) ----------

    /**
     * Registra un pedido con sus ítems en una sola transacción:
     * si falla un ítem, no se guarda nada.
     * Devuelve el order_id generado.
     */
    public long insert(PurchaseOrder o, List<PurchaseOrderItem> items) throws SQLException {
        String sqlOrder = "INSERT INTO purchase_orders "
                + "(order_number, customer_name, customer_email, customer_phone, "
                + "delivery_address, delivery_district, delivery_city, delivery_department, "
                + "delivery_method, customer_comment, payment_method, payment_status, order_status, "
                + "subtotal, shipping_cost, discount_amount, total_amount) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        // line_total NO se incluye: MySQL lo calcula solo
        String sqlItem = "INSERT INTO purchase_order_items "
                + "(order_id, product_name, quantity, unit_price) VALUES (?,?,?,?)";

        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            try {
                long orderId;
                try (PreparedStatement ps = cn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                    int i = 1;
                    ps.setString(i++, o.getOrderNumber());
                    ps.setString(i++, o.getCustomerName());
                    ps.setString(i++, o.getCustomerEmail());
                    ps.setString(i++, o.getCustomerPhone());
                    ps.setString(i++, o.getDeliveryAddress());
                    ps.setString(i++, o.getDeliveryDistrict());
                    ps.setString(i++, o.getDeliveryCity());
                    ps.setString(i++, o.getDeliveryDepartment());
                    ps.setString(i++, o.getDeliveryMethod());
                    ps.setString(i++, o.getCustomerComment());   // puede ser null
                    ps.setString(i++, o.getPaymentMethod());
                    ps.setString(i++, o.getPaymentStatus());
                    ps.setString(i++, o.getOrderStatus());
                    ps.setBigDecimal(i++, o.getSubtotal());
                    ps.setBigDecimal(i++, o.getShippingCost());
                    ps.setBigDecimal(i++, o.getDiscountAmount());
                    ps.setBigDecimal(i++, o.getTotalAmount());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se obtuvo el order_id generado.");
                        }
                        orderId = keys.getLong(1);
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(sqlItem)) {
                    for (PurchaseOrderItem it : items) {
                        ps.setLong(1, orderId);
                        ps.setString(2, it.getProductName());
                        ps.setBigDecimal(3, it.getQuantity());
                        ps.setBigDecimal(4, it.getUnitPrice());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                cn.commit();
                return orderId;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    // ---------- ACTUALIZAR (UPDATE) ----------

    /** Cambia el estado del pedido y el estado del pago. */
    public boolean updateStatus(long orderId, String orderStatus, String paymentStatus) throws SQLException {
        String sql = "UPDATE purchase_orders SET order_status = ?, payment_status = ? WHERE order_id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, orderStatus);
            ps.setString(2, paymentStatus);
            ps.setLong(3, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- ELIMINAR (DELETE) ----------

    /** Elimina el pedido. Sus ítems se borran solos por ON DELETE CASCADE. */
    public boolean delete(long orderId) throws SQLException {
        String sql = "DELETE FROM purchase_orders WHERE order_id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- Mapeo ResultSet -> objeto ----------

    private PurchaseOrder mapOrder(ResultSet rs) throws SQLException {
        PurchaseOrder o = new PurchaseOrder();
        o.setOrderId(rs.getLong("order_id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setCustomerEmail(rs.getString("customer_email"));
        o.setCustomerPhone(rs.getString("customer_phone"));
        o.setDeliveryAddress(rs.getString("delivery_address"));
        o.setDeliveryDistrict(rs.getString("delivery_district"));
        o.setDeliveryCity(rs.getString("delivery_city"));
        o.setDeliveryDepartment(rs.getString("delivery_department"));
        o.setDeliveryMethod(rs.getString("delivery_method"));
        o.setCustomerComment(rs.getString("customer_comment"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setOrderStatus(rs.getString("order_status"));
        o.setSubtotal(rs.getBigDecimal("subtotal"));
        o.setShippingCost(rs.getBigDecimal("shipping_cost"));
        o.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }

    private PurchaseOrderItem mapItem(ResultSet rs) throws SQLException {
        PurchaseOrderItem it = new PurchaseOrderItem();
        it.setItemId(rs.getLong("item_id"));
        it.setOrderId(rs.getLong("order_id"));
        it.setProductName(rs.getString("product_name"));
        it.setQuantity(rs.getBigDecimal("quantity"));
        it.setUnitPrice(rs.getBigDecimal("unit_price"));
        it.setLineTotal(rs.getBigDecimal("line_total"));
        return it;
    }
}
