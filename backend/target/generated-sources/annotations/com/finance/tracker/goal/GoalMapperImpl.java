package com.finance.tracker.goal;

import com.finance.tracker.goal.dto.GoalRequest;
import com.finance.tracker.goal.dto.GoalResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-09T16:17:48+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Ubuntu)"
)
@Component
public class GoalMapperImpl implements GoalMapper {

    @Override
    public GoalResponse toResponse(Goal goal) {
        if ( goal == null ) {
            return null;
        }

        GoalResponse.GoalResponseBuilder goalResponse = GoalResponse.builder();

        goalResponse.id( goal.getId() );
        goalResponse.name( goal.getName() );
        goalResponse.targetAmount( goal.getTargetAmount() );
        goalResponse.currentAmount( goal.getCurrentAmount() );
        goalResponse.deadline( goal.getDeadline() );
        goalResponse.icon( goal.getIcon() );
        goalResponse.color( goal.getColor() );
        goalResponse.createdAt( goal.getCreatedAt() );

        goalResponse.percentageComplete( calculatePercentage(goal.getCurrentAmount(), goal.getTargetAmount()) );
        goalResponse.daysRemaining( calculateDaysRemaining(goal.getDeadline()) );

        return goalResponse.build();
    }

    @Override
    public Goal toEntity(GoalRequest request) {
        if ( request == null ) {
            return null;
        }

        Goal goal = new Goal();

        goal.setName( request.getName() );
        goal.setTargetAmount( request.getTargetAmount() );
        goal.setCurrentAmount( request.getCurrentAmount() );
        goal.setDeadline( request.getDeadline() );
        goal.setIcon( request.getIcon() );
        goal.setColor( request.getColor() );

        return goal;
    }

    @Override
    public void updateEntity(GoalRequest request, Goal goal) {
        if ( request == null ) {
            return;
        }

        goal.setName( request.getName() );
        goal.setTargetAmount( request.getTargetAmount() );
        goal.setCurrentAmount( request.getCurrentAmount() );
        goal.setDeadline( request.getDeadline() );
        goal.setIcon( request.getIcon() );
        goal.setColor( request.getColor() );
    }
}
