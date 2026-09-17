-- PostgreSQL Performance Optimization Indexes for 100k - 500k+ Records
-- Run these in PostgreSQL (db_13092026_postgres) for sub-second searches

-- 1. Enable Trigram Extension for fast ILIKE substring searches
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 2. Companies Table Indexes
CREATE INDEX IF NOT EXISTS idx_companies_deleted_at_id ON companies (deleted_at, id DESC);
CREATE INDEX IF NOT EXISTS idx_companies_category_id ON companies (category_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_companies_status ON companies (status) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_companies_slug ON companies (slug);
CREATE INDEX IF NOT EXISTS idx_companies_username ON companies (username);
CREATE INDEX IF NOT EXISTS idx_companies_bin ON companies (bin);

-- Trigram GIN indexes for 10ms text searches
CREATE INDEX IF NOT EXISTS idx_companies_name_trgm ON companies USING gin (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_companies_bin_trgm ON companies USING gin (bin gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_companies_email_trgm ON companies USING gin (email gin_trgm_ops);

-- 3. Users Table Indexes
CREATE INDEX IF NOT EXISTS idx_users_company_id ON users (company_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_group_id ON users (group_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_status ON users (status) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_is_online ON users (is_online) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

CREATE INDEX IF NOT EXISTS idx_users_name_trgm ON users USING gin (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_users_username_trgm ON users USING gin (username gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_users_email_trgm ON users USING gin (email gin_trgm_ops);

-- 4. Groups Table Indexes
CREATE INDEX IF NOT EXISTS idx_groups_parent_group_id ON groups (parent_group_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_groups_slug ON groups (slug);
CREATE INDEX IF NOT EXISTS idx_groups_name_trgm ON groups USING gin (name gin_trgm_ops);
