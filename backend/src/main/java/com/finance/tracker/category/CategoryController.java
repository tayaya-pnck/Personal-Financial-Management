package com.finance.tracker.category;

import com.finance.tracker.category.dto.CategoryRequest;
import com.finance.tracker.category.dto.CategoryResponse;
import com.finance.tracker.common.CurrentUser;
import com.finance.tracker.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @CurrentUser User user,
            @RequestParam(defaultValue = "false") boolean flat) {
        if (flat) {
            return ResponseEntity.ok(categoryService.getUserFlatCategories(user.getId()));
        }
        return ResponseEntity.ok(categoryService.getUserCategories(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@CurrentUser User user, @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@CurrentUser User user, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(user.getId(), request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@CurrentUser User user, @PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@CurrentUser User user, @PathVariable UUID id) {
        categoryService.deleteCategory(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
