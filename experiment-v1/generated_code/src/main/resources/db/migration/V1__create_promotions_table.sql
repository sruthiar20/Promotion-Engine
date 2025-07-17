CREATE TABLE IF NOT EXISTS promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(255) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    value_json NUMERIC,
    value_type VARCHAR(50),
    starts_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,
    is_automatic BOOLEAN DEFAULT false,
    usage_limit INT,
    usage_count INT DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    conditions_json JSONB,
    rules_json JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
); 