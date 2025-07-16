####  Database Schema Definition

First, we need a database table to store the promotions. Based on your provided JSON response, a `promotions` table in PostgreSQL could be defined as follows. Using `JSONB` is ideal for storing the `conditions` and `rules` arrays.

```sql
CREATE TABLE promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(255) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    value NUMERIC,
    value_type VARCHAR(50),
    starts_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,
    is_automatic BOOLEAN DEFAULT false,
    usage_limit INT,
    usage_count INT DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    conditions JSONB,
    rules JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```
