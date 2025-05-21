package com.project.Expensedivider.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping("/get")
    public List<Category> getcategory(){
        return this.categoryService.getCategory();
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@RequestBody String name){
        return this.categoryService.createCategory(name);
    }

}
