package vn.ClothingStore.domain;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, length = 1000)
    private String url;
    @Column(name = "cloudinary_id", nullable = false, length = 255)
    private String cloudinaryId;

    @Column(nullable = false)
    private boolean thumbnail = false;
    @Column(name = "upload_at", nullable = false, updatable = false)
    private Instant uploadAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
