package com.project.Expensedivider.category;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
   public List<Category> getCategory();

    ResponseEntity<String> createCategory(String name);
}
