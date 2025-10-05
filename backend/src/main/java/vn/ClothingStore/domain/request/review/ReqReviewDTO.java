package vn.ClothingStore.domain.request.review;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqReviewDTO {
    @NotNull
    private int productId;
    @NotNull
    private int userId;
    @NotNull
    private int orderDetailId;
    private int rating;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String comment;
}
