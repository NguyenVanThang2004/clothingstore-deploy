package vn.ClothingStore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.ClothingStore.domain.Category;
import vn.ClothingStore.repository.CategoryRepository;
import vn.ClothingStore.util.error.IdInvalidException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category fetchCategoryById(int id) throws IdInvalidException {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Category với id = " + id + " không tồn tại"));
    }

    public Category createCategory(Category category) throws IdInvalidException {
        boolean exists = this.categoryRepository.existsByName(category.getName());
        if (exists) {
            throw new IdInvalidException("Category " + category.getName() + " đã tồn tại, vui lòng chọn tên khác.");
        }
        return this.categoryRepository.save(category);
    }

    public List<Category> getAllCategory() {
        return this.categoryRepository.findAll();
    }

    public void deleteCategory(int id) throws IdInvalidException {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Category với id = " + id + " không tồn tại"));
        this.categoryRepository.delete(category);
    }
}
