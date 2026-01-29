package shopy.manage;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    
    private String orderNumber;
    private Integer customerId;
    private String productName;       
    private Integer shopId; 
    private LocalDateTime orderDate;
    private String status;
    private Integer quantity;
    private Double totalAmount;
    private String paymentMethod;

    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    // Constructors
    public Order() {
        super();
    }
    
    public Order(Integer orderId, String orderNumber, Integer customerId,String productName,Integer shopId, LocalDateTime orderDate, 
                 String status, Integer quantity, Double totalAmount, String paymentMethod) {
        super();
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.productName = productName;
        this.shopId = shopId;
        this.orderDate = orderDate;
        this.status = status;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    // toString()
    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", orderNumber=" + orderNumber + ", customerId=" + customerId 
                + ",productName=" + productName + ",shopId = " + shopId + ", orderDate=" + orderDate + ", status=" + status + ", quantity=" + quantity 
                + ", totalAmount=" + totalAmount + ", paymentMethod=" + paymentMethod + "]";
    }

}