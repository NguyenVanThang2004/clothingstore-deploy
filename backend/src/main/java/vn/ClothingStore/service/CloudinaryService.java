package vn.ClothingStore.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.ClothingStore.domain.response.product.ResCloudinaryDTO;
import vn.ClothingStore.util.error.StorageException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public ResCloudinaryDTO uploadToFolder(final MultipartFile file, final String folder, final String publicIdHint)
            throws StorageException {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "image");
            options.put("folder", folder); // vd: clothing_store/product/9
            if (publicIdHint != null && !publicIdHint.isBlank()) {
                options.put("public_id", publicIdHint);
                options.put("unique_filename", true);
                options.put("use_filename", false);
            } else {
                options.put("unique_filename", true);
                options.put("use_filename", false);
            }

            @SuppressWarnings("rawtypes")
            Map result = cloudinary.uploader().upload(file.getBytes(), options);
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            return ResCloudinaryDTO.builder()
                    .publicId(publicId)
                    .url(url)
                    .build();

        } catch (Exception e) {
            throw new StorageException("Failed to upload file", e);
        }
    }

    public void delete(String publicId) throws StorageException {
        try {
            if (publicId == null || publicId.isBlank())
                return;
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new StorageException("Failed to delete cloudinary resource: " + publicId, e);
        }
    }
}
