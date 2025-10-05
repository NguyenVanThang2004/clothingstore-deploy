package vn.ClothingStore.specifications;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import vn.ClothingStore.domain.Category;
import vn.ClothingStore.domain.Category_;
import vn.ClothingStore.domain.Product;
import vn.ClothingStore.domain.Product_;

public class ProductSpecs {

    public static Specification<Product> hasCategory(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return null;
            }
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasPriceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min != null && max != null) {
                return cb.between(root.get("price"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            } else {
                return cb.lessThanOrEqualTo(root.get("price"), max);
            }
        };
    }

    public static Specification<Product> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return null;
            String likePattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern),
                    cb.like(cb.lower(root.join("category").get("name")), likePattern));
        };
    }

}
