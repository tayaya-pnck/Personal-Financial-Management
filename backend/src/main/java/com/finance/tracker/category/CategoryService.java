package com.finance.tracker.category;

import com.finance.tracker.category.dto.CategoryRequest;
import com.finance.tracker.category.dto.CategoryResponse;
import com.finance.tracker.common.ResourceNotFoundException;
import com.finance.tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getUserCategories(UUID userId) {
        var roots = categoryRepository.findByUserIdAndParentIsNullOrderByNameAsc(userId);
        return roots.stream()
            .map(this::buildTree)
            .toList();
    }

    public List<CategoryResponse> getUserFlatCategories(UUID userId) {
        return categoryRepository.findByUserIdOrderByNameAsc(userId)
            .stream()
            .map(categoryMapper::toResponse)
            .toList();
    }

    public CategoryResponse getCategory(UUID userId, UUID categoryId) {
        var category = findCategory(userId, categoryId);
        return buildTree(category);
    }

    @Transactional
    public CategoryResponse createCategory(UUID userId, CategoryRequest request, User user) {
        var category = categoryMapper.toEntity(request);
        category.setUser(user);

        if (request.getParentId() != null) {
            var parent = findCategory(userId, request.getParentId());
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return buildTree(category);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID userId, UUID categoryId, CategoryRequest request) {
        var category = findCategory(userId, categoryId);
        categoryMapper.updateEntity(request, category);

        if (request.getParentId() != null) {
            var parent = findCategory(userId, request.getParentId());
            if (parent.getId().equals(category.getId())) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return buildTree(category);
    }

    @Transactional
    public void deleteCategory(UUID userId, UUID categoryId) {
        var category = findCategory(userId, categoryId);
        categoryRepository.delete(category);
    }

    private Category findCategory(UUID userId, UUID categoryId) {
        var category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category", categoryId);
        }
        return category;
    }

    private CategoryResponse buildTree(Category category) {
        var response = categoryMapper.toResponse(category);
        var children = categoryRepository.findByParentId(category.getId())
            .stream()
            .map(this::buildTree)
            .toList();
        response.getChildren().addAll(children);
        return response;
    }
}
