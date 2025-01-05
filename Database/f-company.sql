    
CREATE OR REPLACE FUNCTION trigger_hash_company_password()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.company_password := crypt(NEW.company_password, gen_salt('bf'));
    RETURN NEW;
END;
$$;



CREATE TRIGGER company_insert_trigger
BEFORE INSERT ON company
FOR EACH ROW
EXECUTE FUNCTION trigger_hash_company_password();


