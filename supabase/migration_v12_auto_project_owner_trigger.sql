-- ### 12. AUTOMATIC PROJECT OWNER MEMBERSHIP TRIGGER ###
-- Bu trigger, yeni bir proje oluşturulduğunda sahibini otomatik olarak 
-- team_members tablosuna ekleyerek boş liste sorununu çözer.

CREATE OR REPLACE FUNCTION public.handle_new_project_ownership()
RETURNS trigger AS $$
BEGIN
    INSERT INTO public.team_members (user_id, project_id, role)
    VALUES (new.owner_id, new.id, 'PROJE_SAHIBI')
    ON CONFLICT (user_id, project_id) DO NOTHING;
    RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger'ı oluştur
DROP TRIGGER IF EXISTS tr_new_project_owner ON public.projects;
CREATE TRIGGER tr_new_project_owner
    AFTER INSERT ON public.projects
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_new_project_ownership();

-- Mevcut projeler için eksik sahipleri ekle (Geridönük Tamir)
INSERT INTO public.team_members (user_id, project_id, role)
SELECT owner_id, id, 'PROJE_SAHIBI' 
FROM public.projects
ON CONFLICT (user_id, project_id) DO NOTHING;
