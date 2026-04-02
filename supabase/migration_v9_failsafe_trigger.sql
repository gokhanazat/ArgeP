-- ### 1. FAIL-SAFE MEMBESHIP TRIGGER ###
-- Bu trigger, Edge Function'da bir hata olsa dahi, eğer 'invitations' tablosuna 
-- kayıt atılmışsa üyenin 'team_members' tablosuna otomatik eklenmesini sağlar.

CREATE OR REPLACE FUNCTION public.handle_invite_membership()
RETURNS TRIGGER AS $$
DECLARE
    v_user_id uuid;
BEGIN
    -- 1. E-posta adresi ile auth.users tablosunda kullanıcı var mı bak?
    SELECT id INTO v_user_id FROM auth.users WHERE email = NEW.email LIMIT 1;
    
    -- 2. Eğer kullanıcı varsa ve proje ID'si belirtilmişse, ekle!
    IF v_user_id IS NOT NULL AND NEW.project_id IS NOT NULL THEN
        INSERT INTO public.team_members (user_id, project_id, role)
        VALUES (v_user_id, NEW.project_id, NEW.role)
        ON CONFLICT (user_id, project_id) DO UPDATE 
        SET role = EXCLUDED.role;
        
        -- Diagnostic kaydı at
        INSERT INTO public.debug_logs (event_name, payload)
        VALUES ('FAILSAFE_TRIGGER_SUCCESS', jsonb_build_object('email', NEW.email, 'user_id', v_user_id, 'project_id', NEW.project_id));
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger'ı oluştur (Her INSERT ve UPDATE sonrası çalışır)
DROP TRIGGER IF EXISTS tr_invite_membership ON public.invitations;
CREATE TRIGGER tr_invite_membership
    AFTER INSERT OR UPDATE ON public.invitations
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_invite_membership();

COMMENT ON FUNCTION public.handle_invite_membership IS 'Automatically joins existing users to projects via invitation records';
