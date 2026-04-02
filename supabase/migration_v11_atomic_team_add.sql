-- ### 11. ATOMIC RPC: ADD TEAM MEMBER BY EMAIL ###
-- Tüm üye ekleme işlemini tek bir veritabanı işlemi (transaction) olarak 
-- gerçekleştiren ve Edge Function'daki karmaşıklığı bitiren ana fonksiyondur.

CREATE OR REPLACE FUNCTION public.add_team_member_by_email(
    p_email text,
    p_project_id uuid,
    p_role text
)
RETURNS jsonb AS $$
DECLARE
    v_user_id uuid;
    v_project_exists boolean;
    v_result jsonb;
BEGIN
    -- 1. E-posta ile kullanıcıyı bul (Security Definer olduğu için yetkilidir)
    SELECT id INTO v_user_id 
    FROM auth.users 
    WHERE email = LOWER(TRIM(p_email)) 
    LIMIT 1;

    -- 2. Eğer kullanıcı yoksa hata dön
    IF v_user_id IS NULL THEN
        v_result := jsonb_build_object('success', false, 'error', p_email || ' sistemde bulunamadı. Lütfen kullanıcının önce uygulamaya kayıt olduğundan emin olun.');
        INSERT INTO public.debug_logs (event_name, payload) 
        VALUES ('ADD_MEMBER_FAILED_NOT_FOUND', jsonb_build_object('email', p_email, 'project_id', p_project_id));
        RETURN v_result;
    END IF;

    -- 3. Proje var mı kontrol et
    SELECT EXISTS (SELECT 1 FROM public.projects WHERE id = p_project_id) INTO v_project_exists;
    IF NOT v_project_exists THEN
        v_result := jsonb_build_object('success', false, 'error', 'Belirtilen proje bulunamadı.');
        RETURN v_result;
    END IF;

    -- 4. team_members tablosuna ekle veya güncelle (Atomic Upsert)
    INSERT INTO public.team_members (user_id, project_id, role)
    VALUES (v_user_id, p_project_id, p_role)
    ON CONFLICT (user_id, project_id) DO UPDATE 
    SET role = EXCLUDED.role;

    -- 5. Başarı logu ve sonuç dön
    INSERT INTO public.debug_logs (event_name, payload) 
    VALUES ('ADD_MEMBER_SUCCESS', jsonb_build_object('email', p_email, 'user_id', v_user_id, 'project_id', p_project_id, 'role', p_role));

    v_result := jsonb_build_object('success', true, 'user_id', v_user_id, 'message', 'Üye başarıyla eklendi.');
    RETURN v_result;

EXCEPTION WHEN OTHERS THEN
    v_result := jsonb_build_object('success', false, 'error', 'Veritabanı hatası: ' || SQLERRM);
    RETURN v_result;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

COMMENT ON FUNCTION public.add_team_member_by_email IS 'Atomically adds or updates a team member in a project using their email. Bypasses RLS with Security Definer.';
