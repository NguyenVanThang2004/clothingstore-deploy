package vn.ClothingStore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.ClothingStore.domain.Category;
import vn.ClothingStore.service.CategoryService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ApiMessage("Get all categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(this.categoryService.getAllCategory());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ApiMessage("Create category success")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.createCategory(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ApiMessage("Delete category success")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") int id) throws IdInvalidException {
        this.categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
