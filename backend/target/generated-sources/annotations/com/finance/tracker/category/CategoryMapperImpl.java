package com.finance.tracker.category;

import com.finance.tracker.category.dto.CategoryRequest;
import com.finance.tracker.category.dto.CategoryResponse;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-09T16:17:48+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Ubuntu)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryResponse toResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.parentId( categoryParentId( category ) );
        categoryResponse.parentName( categoryParentName( category ) );
        categoryResponse.id( category.getId() );
        categoryResponse.name( category.getName() );
        categoryResponse.type( category.getType() );
        categoryResponse.icon( category.getIcon() );
        categoryResponse.color( category.getColor() );
        categoryResponse.createdAt( category.getCreatedAt() );

        return categoryResponse.build();
    }

    @Override
    public Category toEntity(CategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( request.getName() );
        category.setType( request.getType() );
        category.setIcon( request.getIcon() );
        category.setColor( request.getColor() );

        return category;
    }

    @Override
    public void updateEntity(CategoryRequest request, Category category) {
        if ( request == null ) {
            return;
        }

        category.setName( request.getName() );
        category.setType( request.getType() );
        category.setIcon( request.getIcon() );
        category.setColor( request.getColor() );
    }

    private UUID categoryParentId(Category category) {
        Category parent = category.getParent();
        if ( parent == null ) {
            return null;
        }
        return parent.getId();
    }

    private String categoryParentName(Category category) {
        Category parent = category.getParent();
        if ( parent == null ) {
            return null;
        }
        return parent.getName();
    }
}
