package vn.ClothingStore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import vn.ClothingStore.util.constant.ColorEnum;
import vn.ClothingStore.util.constant.OrderStatusEnum;
import vn.ClothingStore.util.constant.SizeEnum;

import java.util.List;

@Entity
@Table(name = "productVariants")
@Getter
@Setter
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('BLACK', 'WHITE', 'GRAY', 'BLUE')")
    private ColorEnum color;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('S', 'M', 'L', 'XL')")
    private SizeEnum size; // S, M, L, 29, 30, 31

    @DecimalMin(value = "0.0")
    private float price;

    private int stockQuantity; // tồn kho riêng cho biến thể

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productVariant")
    private List<OrderDetail> orderDetails;
}
