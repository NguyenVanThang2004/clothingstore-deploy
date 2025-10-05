package vn.ClothingStore.util;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import vn.ClothingStore.util.error.StorageException;

public final class FileUploadUtil {

    private FileUploadUtil() {
    }

    // 2 MiB
    public static final long MAX_FILE_SIZE = 2L * 1024 * 1024;

    // Cho phép cả jpeg/webp
    private static final Set<String> ALLOWED_EXTS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/bmp",
            "image/webp");

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            .withZone(ZoneOffset.UTC);

    /** Ném StorageException nếu file không hợp lệ */
    public static void assertAllowed(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new StorageException("Max file size is 2MB.");
        }

        String original = sanitizeFilename(file.getOriginalFilename());
        String ext = extensionOf(original);

        if (!ALLOWED_EXTS.contains(ext)) {
            throw new StorageException("Only " + ALLOWED_EXTS + " files are allowed.");
        }

        String ct = file.getContentType();
        if (ct != null && !ALLOWED_CONTENT_TYPES.contains(ct)) {
            throw new StorageException("Unsupported content type: " + ct);
        }

        // Xác thực nội dung là ảnh thật sự (không chỉ đổi đuôi)
        try (InputStream is = file.getInputStream()) {
            if (ImageIO.read(is) == null) { // không decode được ảnh
                throw new StorageException("Invalid image data.");
            }
        } catch (IOException e) {
            throw new StorageException("Cannot read uploaded file.", e);
        }
    }

    public static String generateFileName(MultipartFile file, String prefix) throws StorageException {
        String original = sanitizeFilename(file.getOriginalFilename());
        String ext = extensionOf(original);
        String ts = TS_FMT.format(Instant.now());
        String rand = UUID.randomUUID().toString().substring(0, 8);
        String pre = (prefix == null || prefix.isBlank())
                ? "img"
                : prefix.trim().replaceAll("\\s+", "-");
        return pre + "_" + ts + "_" + rand + "." + ext;
    }

    /** Loại bỏ path, ký tự nguy hiểm, chống tên bắt đầu bằng '.' */
    public static String sanitizeFilename(String name) {
        if (name == null)
            return "file";
        name = name.replace("\\", "/");
        name = name.substring(name.lastIndexOf('/') + 1);
        name = name.replaceAll("[\\r\\n]", "");
        name = name.replaceAll("[^A-Za-z0-9._-]", "-");
        if (name.startsWith("."))
            name = "file" + name;
        return name;
    }

    /** Lấy extension, ném lỗi nếu không có đuôi */
    public static String extensionOf(String name) throws StorageException {
        int dot = name.lastIndexOf('.');
        if (dot == -1 || dot == name.length() - 1) {
            throw new StorageException("Filename must have an extension.");
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
