package vn.ClothingStore.specifications;

import vn.ClothingStore.domain.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecs {
    public static Specification<User> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return null;
            String likePattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("email")), likePattern),
                    cb.like(cb.lower(root.get("fullName")), likePattern),
                    cb.like(cb.lower(root.get("address")), likePattern));
        };
    }

    public static Specification<User> hasRole(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank())
                return null;
            return cb.equal(root.get("role").get("name"), name);
        };
    }

    public static Specification<User> hasPhoneName(String phoneName) {
        return (root, query, cb) -> {
            if (phoneName == null || phoneName.isBlank())
                return null;
            return cb.equal(root.get("phoneNumber"), phoneName);
        };
    }
}
