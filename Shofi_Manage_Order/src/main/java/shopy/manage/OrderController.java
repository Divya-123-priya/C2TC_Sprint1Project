package shopy.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController 
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/orders") 
public class OrderController {
    
    @Autowired 
    private OrderService orderService;

    // ========== BASIC CRUD OPERATIONS ==========
    
    // Retrieve all orders
    @GetMapping
    public ResponseEntity<List<Order>> listAll() {
        List<Order> orders = orderService.listAll();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Retrieve order by id
    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Integer id) {
        Order order = orderService.get(id);
        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Create order
    @PostMapping
    public ResponseEntity<Order> add(@RequestBody Order order) {
        orderService.save(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // Update order
    @PutMapping("/{id}") 
    public ResponseEntity<Order> update(@RequestBody Order order, @PathVariable Integer id) {
        Order existingOrder = orderService.get(id);
        if (existingOrder != null) {
            existingOrder.setCustomerId(order.getCustomerId());
            existingOrder.setProductName(order.getProductName());
            existingOrder.setShopId(order.getShopId());
            existingOrder.setStatus(order.getStatus());
            existingOrder.setQuantity(order.getQuantity());
            existingOrder.setTotalAmount(order.getTotalAmount());
            existingOrder.setPaymentMethod(order.getPaymentMethod());
            
            orderService.save(existingOrder);
            return new ResponseEntity<>(existingOrder, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Order order = orderService.get(id);
        if (order != null) {
            orderService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // ========== ORDER STATUS MANAGEMENT ==========
    
    // Cancel an order
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer id) {
        boolean cancelled = orderService.cancelOrder(id);
        if (cancelled) {
            return new ResponseEntity<>("Order cancelled successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Order cannot be cancelled", HttpStatus.BAD_REQUEST);
    }
    
    // Mark order as delivered
    @PutMapping("/{id}/deliver")
    public ResponseEntity<String> markAsDelivered(@PathVariable Integer id) {
        boolean delivered = orderService.markAsDelivered(id);
        if (delivered) {
            return new ResponseEntity<>("Order marked as delivered", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to mark as delivered", HttpStatus.BAD_REQUEST);
    }
    
    // Mark order as shipped
    @PutMapping("/{id}/ship")
    public ResponseEntity<String> markAsShipped(@PathVariable Integer id) {
        boolean shipped = orderService.markAsShipped(id);
        if (shipped) {
            return new ResponseEntity<>("Order marked as shipped", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to mark as shipped. Order must be in PROCESSING or PENDING state", HttpStatus.BAD_REQUEST);
    }
    
    // Update order status with custom status
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Integer id, @RequestBody StatusUpdateRequest request) {
        boolean updated = orderService.updateOrderStatus(id, request.getNewStatus());
        if (updated) {
            return new ResponseEntity<>("Order status updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid status transition", HttpStatus.BAD_REQUEST);
    }
    
    // ========== ORDER QUERIES & FILTERS ==========
    
    // Get late orders
    @GetMapping("/late")
    public ResponseEntity<List<Order>> getLateOrders() {
        List<Order> lateOrders = orderService.getLateOrders();
        return new ResponseEntity<>(lateOrders, HttpStatus.OK);
    }
    
    // Get cancelled orders
    @GetMapping("/cancelled")
    public ResponseEntity<List<Order>> getCancelledOrders() {
        List<Order> cancelledOrders = orderService.getCancelledOrders();
        return new ResponseEntity<>(cancelledOrders, HttpStatus.OK);
    }
    
    // Get delivered orders
    @GetMapping("/delivered")
    public ResponseEntity<List<Order>> getDeliveredOrders() {
        List<Order> deliveredOrders = orderService.getDeliveredOrders();
        return new ResponseEntity<>(deliveredOrders, HttpStatus.OK);
    }
    
    // Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    // ========== ORDER UTILITIES ==========
    
    // Check if order can be cancelled
    @GetMapping("/{id}/can-cancel")
    public ResponseEntity<Boolean> canBeCancelled(@PathVariable Integer id) {
        Order order = orderService.get(id);
        if (order != null) {
            boolean canCancel = orderService.canBeCancelled(order);
            return new ResponseEntity<>(canCancel, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }
    
    // Check if order is late
    @GetMapping("/{id}/is-late")
    public ResponseEntity<Boolean> isLateOrder(@PathVariable Integer id) {
        Order order = orderService.get(id);
        if (order != null) {
            boolean isLate = orderService.isLateOrder(order);
            return new ResponseEntity<>(isLate, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }
    
    // Get days late for an order
    @GetMapping("/{id}/days-late")
    public ResponseEntity<Long> getDaysLate(@PathVariable Integer id) {
        Order order = orderService.get(id);
        if (order != null) {
            long daysLate = orderService.getDaysLate(order);
            return new ResponseEntity<>(daysLate, HttpStatus.OK);
        }
        return new ResponseEntity<>(0L, HttpStatus.NOT_FOUND);
    }
}

// Helper class for status update requests
class StatusUpdateRequest {
    private String newStatus;
    
    public StatusUpdateRequest() {}
    
    public StatusUpdateRequest(String newStatus) {
        this.newStatus = newStatus;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}