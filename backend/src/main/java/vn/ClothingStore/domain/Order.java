package vn.ClothingStore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import vn.ClothingStore.util.constant.OrderStatusEnum;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fullname;
    private String email;
    private String phoneNumber;
    private String address;
    private String note;
    @Column(name = "order_date", nullable = false, updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant orderDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING'")
    private OrderStatusEnum status; // trang thai don hang chi duoc phep nhan mot so gia tri cu the

    @DecimalMin(value = "0.0")
    private float totalMoney;

    private String shippingMethod;
    private String shippingAddress;
    private Instant shippingDate;
    private String trackingNumber;
    private String paymentMethod;
    private boolean active; // xoa 1 don don = xoa mem

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

}
