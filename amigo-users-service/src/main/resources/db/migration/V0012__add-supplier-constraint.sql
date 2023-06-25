alter table users.supplier
    add constraint unique_name_ unique (name),
    add constraint unique_code_variable_ unique (code_variable);

CREATE OR REPLACE FUNCTION alterSupplierCodeVariable()
    RETURNS integer
    LANGUAGE plpgsql AS
$func$
declare
    count integer := 0;
    res   users.supplier;
    u_cursor cursor for select distinct on (supplier.name) *
                        from users.supplier
                        where code_variable is null;

BEGIN
    open u_cursor;
    loop
        fetch u_cursor into res;
        if not found
        then
            exit;
        end if;
        update users.supplier
        set code_variable = replace(upper(res.name), ' ', '')
        where supplier.name = res.name;

        count := count + 1;
    end loop;

    return count;
END
$func$;

select alterSupplierCodeVariable();