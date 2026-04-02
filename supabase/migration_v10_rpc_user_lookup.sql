-- ### 10. RPC: FIND USER ID BY EMAIL ###
-- Edge Function'ların auth.users tablosu yerine güvenli bir şekilde kullanıcı 
-- ID'si dönmesini sağlayan yardımcı fonksiyondur.

CREATE OR REPLACE FUNCTION public.get_user_id_by_email(p_email text)
RETURNS uuid AS $$
DECLARE
    v_user_id uuid;
BEGIN
    -- auth.users tablosundan ara (Security Definer olduğu için yetkilidir)
    SELECT id INTO v_user_id 
    FROM auth.users 
    WHERE email = LOWER(TRIM(p_email)) 
    LIMIT 1;
    
    RETURN v_user_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION public.get_user_id_by_email IS 'Returns the auth.user ID for a given email address. Requires service_role or admin privileges.';
