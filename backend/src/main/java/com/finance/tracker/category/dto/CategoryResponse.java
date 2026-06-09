package com.finance.tracker.category.dto;

import com.finance.tracker.category.Category.CategoryType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private CategoryType type;
    private UUID parentId;
    private String parentName;
    private String icon;
    private String color;
    @Builder.Default
    private List<CategoryResponse> children = new ArrayList<>();
    private LocalDateTime createdAt;
}
