-- Expiry timestamp for temporary suspensions (NULL = indefinite / banned).
ALTER TABLE usuarios ADD COLUMN suspension_hasta TIMESTAMP NULL;
