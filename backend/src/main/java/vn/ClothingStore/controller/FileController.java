package vn.ClothingStore.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.ClothingStore.domain.response.file.ResUploadFileDTO;
import vn.ClothingStore.service.FileService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    @Value("${backend.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {

        // validate file upload
        if (file == null || file.isEmpty()) {
            throw new StorageException("file is empty . please upload a file");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. only allows " + allowedExtensions.toString());
        }

        // create a directory if not exist
        this.fileService.createDirectory(baseURI + folder);

        // store file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/files/multi")
    @ApiMessage("Upload multiple files")
    public ResponseEntity<List<ResUploadFileDTO>> uploadMulti(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {

        if (files == null || files.length == 0) {
            throw new StorageException("No files found. Please upload at least one file.");
        }

        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

        // create a directory if not exist
        this.fileService.createDirectory(baseURI + folder);

        List<ResUploadFileDTO> responses = new java.util.ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty())
                continue;

            String fileName = file.getOriginalFilename();
            boolean isValid = allowedExtensions.stream().anyMatch(ext -> fileName.toLowerCase().endsWith(ext));

            if (!isValid) {
                throw new StorageException(
                        "Invalid file extension: " + fileName + ". Only allows " + allowedExtensions.toString());
            }

            String uploadedFile = this.fileService.store(file, folder);
            responses.add(new ResUploadFileDTO(uploadedFile, Instant.now()));
        }

        return ResponseEntity.ok(responses);
    }

}