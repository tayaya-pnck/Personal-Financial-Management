CREATE TABLE budgets (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    budget_month VARCHAR(7) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    is_notification_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, category_id, budget_month)
);

CREATE INDEX idx_budgets_user_month ON budgets(user_id, budget_month);
