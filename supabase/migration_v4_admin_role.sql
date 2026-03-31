-- 1. PROFILES tablosuna is_admin sütunu ekle
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS is_admin boolean DEFAULT false;

-- 2. Yetki kontrol fonksiyonunu (is_project_manager) güncelle
-- Artık kullanıcı Admin ise veya Proje Sahibi/Müdürü ise TRUE dönecek.
CREATE OR REPLACE FUNCTION public.is_project_manager(p_project_id uuid, p_user_id uuid)
RETURNS boolean AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM public.profiles pr
        LEFT JOIN public.projects p ON p.id = p_project_id
        LEFT JOIN public.team_members tm ON tm.project_id = p.id AND tm.user_id = p_user_id
        WHERE pr.id = p_user_id 
        AND (
            pr.is_admin = true -- Global Admin kontrolü
            OR p.owner_id = p_user_id -- Proje Sahibi kontrolü
            OR tm.role = 'PROJE_MUDURU' -- Proje Müdürü rolü kontrolü
        )
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 3. Belirtilen kullanıcıyı ADMIN yap (SİZİN İÇİN)
UPDATE public.profiles 
SET is_admin = true 
WHERE id IN (
    SELECT id FROM auth.users WHERE email = 'gkhnazat@gmail.com'
);
