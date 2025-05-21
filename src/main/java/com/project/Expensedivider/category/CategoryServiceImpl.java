package com.project.Expensedivider.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final Categorrepository categorrepository;

    @Override
    public List<Category> getCategory() {
        return this.categorrepository.findAll();
    }

    @Override
    public ResponseEntity<String> createCategory(String name) {
        var category= Category.builder().name(name).build();
        this.categorrepository.save(category);
        return ResponseEntity.ok("Category created successfully");
    }
}
