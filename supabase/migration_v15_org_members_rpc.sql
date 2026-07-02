-- ### 15. RPC: ADD ORGANIZATION MEMBER BY EMAIL ###
-- Bu fonksiyon bir kullanıcıyı e-posta adresi üzerinden doğrudan bir organizasyona ekler.

CREATE OR REPLACE FUNCTION public.add_org_member_by_email(
    p_email text,
    p_org_id uuid,
    p_role text DEFAULT 'customer'
)
RETURNS jsonb AS $$
DECLARE
    v_user_id uuid;
    v_result jsonb;
BEGIN
    -- 1. E-posta ile kullanıcıyı bul
    SELECT id INTO v_user_id 
    FROM auth.users 
    WHERE email = LOWER(TRIM(p_email)) 
    LIMIT 1;

    -- 2. Eğer kullanıcı yoksa hata dön (Edge function davet gönderecek)
    IF v_user_id IS NULL THEN
        v_result := jsonb_build_object('success', false, 'error', 'USER_NOT_FOUND', 'message', p_email || ' sistemde bulunamadı.');
        RETURN v_result;
    END IF;

    -- 3. Profiles tablosunda org_id ve role güncelle
    UPDATE public.profiles 
    SET org_id = p_org_id,
        role = p_role
    WHERE id = v_user_id;

    -- 4. Başarı logu
    INSERT INTO public.debug_logs (event_name, payload) 
    VALUES ('ADD_ORG_MEMBER_SUCCESS', jsonb_build_object('email', p_email, 'user_id', v_user_id, 'org_id', p_org_id, 'role', p_role));

    v_result := jsonb_build_object('success', true, 'user_id', v_user_id, 'message', 'Kullanıcı organizasyona başarıyla eklendi.');
    RETURN v_result;

EXCEPTION WHEN OTHERS THEN
    v_result := jsonb_build_object('success', false, 'error', 'DB_ERROR', 'message', 'Veritabanı hatası: ' || SQLERRM);
    RETURN v_result;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION public.add_org_member_by_email IS 'Adds a user to an organization using their email address. Updates the profiles table.';
