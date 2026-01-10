ALTER TABLE urls
    ADD CONSTRAINT uq_urls_url_id UNIQUE (url_id);

CREATE INDEX IF NOT EXISTS idx_urls_expiration_date ON urls (expiration_date);
