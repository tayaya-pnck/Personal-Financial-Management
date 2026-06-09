package com.finance.tracker.budget;

import com.finance.tracker.budget.dto.BudgetRequest;
import com.finance.tracker.budget.dto.BudgetResponse;
import com.finance.tracker.category.Category;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-09T16:17:48+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Ubuntu)"
)
@Component
public class BudgetMapperImpl implements BudgetMapper {

    @Override
    public BudgetResponse toResponse(Budget budget) {
        if ( budget == null ) {
            return null;
        }

        BudgetResponse.BudgetResponseBuilder budgetResponse = BudgetResponse.builder();

        budgetResponse.categoryId( budgetCategoryId( budget ) );
        budgetResponse.categoryName( budgetCategoryName( budget ) );
        budgetResponse.categoryIcon( budgetCategoryIcon( budget ) );
        budgetResponse.categoryColor( budgetCategoryColor( budget ) );
        budgetResponse.month( budget.getBudgetMonth() );
        budgetResponse.id( budget.getId() );
        budgetResponse.amount( budget.getAmount() );
        budgetResponse.createdAt( budget.getCreatedAt() );

        return budgetResponse.build();
    }

    @Override
    public Budget toEntity(BudgetRequest request) {
        if ( request == null ) {
            return null;
        }

        Budget budget = new Budget();

        budget.setBudgetMonth( request.getMonth() );
        budget.setAmount( request.getAmount() );

        return budget;
    }

    private UUID budgetCategoryId(Budget budget) {
        Category category = budget.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getId();
    }

    private String budgetCategoryName(Budget budget) {
        Category category = budget.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }

    private String budgetCategoryIcon(Budget budget) {
        Category category = budget.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getIcon();
    }

    private String budgetCategoryColor(Budget budget) {
        Category category = budget.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getColor();
    }
}
