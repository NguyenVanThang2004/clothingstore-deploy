package vn.ClothingStore.domain.request.review;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateReviewDTO {
    private int rating;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String comment;
}
