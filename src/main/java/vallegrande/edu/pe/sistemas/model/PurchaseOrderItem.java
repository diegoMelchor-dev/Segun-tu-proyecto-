package vallegrande.edu.pe.sistemas.model;

import java.math.BigDecimal;

/**
 * Representa una fila de la tabla purchase_order_items.
 * line_total es una columna generada en MySQL (quantity * unit_price),
 * por eso solo se lee y nunca se inserta ni se actualiza.
 */
public class PurchaseOrderItem {

    private long itemId;
    private long orderId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;   // solo lectura

    public PurchaseOrderItem() {
    }

    public PurchaseOrderItem(String productName, BigDecimal quantity, BigDecimal unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public long getItemId() { return itemId; }
    public void setItemId(long itemId) { this.itemId = itemId; }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}