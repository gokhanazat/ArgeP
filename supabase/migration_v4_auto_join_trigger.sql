-- Otomatik Ekip Katılım Tetikleyicisi
-- Bu fonksiyon yeni bir profil oluşturulduğunda (kullanıcı kayıt olduğunda) çalışır
-- ve o e-posta adresine yapılmış bekleyen davetleri kontrol ederek kullanıcıyı projeye ekler.

CREATE OR REPLACE FUNCTION public.handle_new_user_invitations()
RETURNS trigger AS $$
DECLARE
    invitation_record record;
BEGIN
    -- 1. Kullanıcının e-postasını auth.users tablosundan al
    -- 2. Bu e-posta için bekleyen davetleri bul
    FOR invitation_record IN 
        SELECT project_id, role, id 
        FROM public.invitations 
        WHERE email = (SELECT email FROM auth.users WHERE id = NEW.id)
        AND accepted_at IS NULL
    LOOP
        -- 3. Davet varsa ekip_üyeleri tablosuna ekle
        INSERT INTO public.team_members (user_id, project_id, role)
        VALUES (NEW.id, invitation_record.project_id, invitation_record.role)
        ON CONFLICT (user_id, project_id) DO NOTHING;

        -- 4. Daveti kabul edilmiş olarak işaretle
        UPDATE public.invitations 
        SET accepted_at = now() 
        WHERE id = invitation_record.id;
    END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Tetikleyici: Yeni bir profil eklendiğinde (kayıt sonrası) çalışır
DROP TRIGGER IF EXISTS on_profile_created_join_projects ON public.profiles;
CREATE TRIGGER on_profile_created_join_projects
    AFTER INSERT ON public.profiles
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_new_user_invitations();
