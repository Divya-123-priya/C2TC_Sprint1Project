package shopy.manage;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository repo;
    
    public List<Order> listAll() {
        return repo.findAll();
    }
    
    public Order get(Integer id) {
        return repo.findById(id).orElse(null);  // Changed to return null if not found
    }
    
    public void save(Order order) {
        if (order.getOrderNumber() == null) {
            order.setOrderNumber("ORD" + System.currentTimeMillis());
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(java.time.LocalDateTime.now());
        }
        repo.save(order);
    }
    
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    
    // ========== ORDER STATUS HELPER METHODS ==========
    
    // Check if order is late (more than 3 days from order date)
    public boolean isLateOrder(Order order) {
        if (order == null || order.getOrderDate() == null || order.getStatus() == null) {
            return false;
        }
        LocalDateTime dueDate = order.getOrderDate().plusDays(3);
        return LocalDateTime.now().isAfter(dueDate) 
                && !"DELIVERED".equals(order.getStatus()) 
                && !"CANCELLED".equals(order.getStatus());
    }
    
    // Check if order can be cancelled
    public boolean canBeCancelled(Order order) {
        if (order == null || order.getStatus() == null) {
            return false;
        }
        String status = order.getStatus();
        return !"DELIVERED".equals(status) 
                && !"CANCELLED".equals(status) 
                && !"SHIPPED".equals(status);
    }
    
    // Get days late for an order
    public long getDaysLate(Order order) {
        if (!isLateOrder(order)) {
            return 0;
        }
        LocalDateTime dueDate = order.getOrderDate().plusDays(3);
        return ChronoUnit.DAYS.between(dueDate, LocalDateTime.now());
    }
    
    // ========== ORDER STATUS SERVICE METHODS ==========
    
    // Get all late orders
    public List<Order> getLateOrders() {
        List<Order> allOrders = repo.findAll();
        return allOrders.stream()
                .filter(order -> isLateOrder(order))
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Get all cancelled orders
    public List<Order> getCancelledOrders() {
        return repo.findAll().stream()
                .filter(order -> "CANCELLED".equals(order.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Get all delivered orders
    public List<Order> getDeliveredOrders() {
        return repo.findAll().stream()
                .filter(order -> "DELIVERED".equals(order.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Get orders by status
    public List<Order> getOrdersByStatus(String status) {
        return repo.findAll().stream()
                .filter(order -> status.equalsIgnoreCase(order.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Cancel an order
    public boolean cancelOrder(Integer orderId) {
        try {
            Order order = repo.findById(orderId).get();
            if (canBeCancelled(order)) {
                order.setStatus("CANCELLED");
                repo.save(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Mark order as delivered
    public boolean markAsDelivered(Integer orderId) {
        try {
            Order order = repo.findById(orderId).get();
            order.setStatus("DELIVERED");
            repo.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Mark order as shipped
    public boolean markAsShipped(Integer orderId) {
        try {
            Order order = repo.findById(orderId).get();
            if ("PROCESSING".equals(order.getStatus()) || "PENDING".equals(order.getStatus())) {
                order.setStatus("SHIPPED");
                repo.save(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Update order status
    public boolean updateOrderStatus(Integer orderId, String newStatus) {
        try {
            Order order = repo.findById(orderId).get();
            
            // Validate status transition
            if (isValidStatusTransition(order.getStatus(), newStatus)) {
                order.setStatus(newStatus.toUpperCase());
                repo.save(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Validate status transition
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid transitions
        java.util.Map<String, java.util.List<String>> validTransitions = new java.util.HashMap<>();
        
        validTransitions.put("PENDING", java.util.Arrays.asList("PROCESSING", "CANCELLED"));
        validTransitions.put("PROCESSING", java.util.Arrays.asList("SHIPPED", "CANCELLED"));
        validTransitions.put("SHIPPED", java.util.Arrays.asList("DELIVERED", "LATE_ORDER"));
        validTransitions.put("DELIVERED", java.util.Arrays.asList()); // No further transitions
        validTransitions.put("CANCELLED", java.util.Arrays.asList()); // No further transitions
        validTransitions.put("LATE_ORDER", java.util.Arrays.asList("DELIVERED"));
        
        java.util.List<String> allowed = validTransitions.get(currentStatus);
        return allowed != null && allowed.contains(newStatus.toUpperCase());
    }
}