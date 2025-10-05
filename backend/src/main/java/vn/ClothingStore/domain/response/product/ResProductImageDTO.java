
package vn.ClothingStore.domain.response.product;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResProductImageDTO {

    private int id;
    private String url;
    private boolean thumbnail;
    private Instant uploadAt;
    private int productID;

}