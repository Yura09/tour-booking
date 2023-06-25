UPDATE users.supplier SET
code_variable = UPPER(REPLACE(name, ' ', ''))
WHERE code_variable IS NULL;