-- 1. ESKİ RECURSIVE POLİTİKALARI TEMİZLE (Kalıntı Bırakma)
-- Projects Politikaları
DROP POLICY IF EXISTS "Users can view projects they are members of" ON public.projects;
DROP POLICY IF EXISTS "Project view for owner and member" ON public.projects;

-- Team Members Politikaları
DROP POLICY IF EXISTS "Team members are viewable by members of the same project" ON public.team_members;
DROP POLICY IF EXISTS "Project owners can see all team members" ON public.team_members;
DROP POLICY IF EXISTS "Team members can see project colleagues" ON public.team_members;
DROP POLICY IF EXISTS "Team members can see themselves and project colleagues" ON public.team_members;
DROP POLICY IF EXISTS "Project owners full access" ON public.team_members;
DROP POLICY IF EXISTS "Team members view colleagues" ON public.team_members;
DROP POLICY IF EXISTS "Public team members select" ON public.team_members;

-- 2. ERİŞİM KONTROL FONKSİYONU (Döngüyü Kırmak İçin SECURITY DEFINER)
-- Bu fonksiyon RLS'i bypass ederek çalışır ve döngüye girmez.
CREATE OR REPLACE FUNCTION public.can_access_project(p_id uuid)
RETURNS boolean AS $$
BEGIN
    RETURN EXISTS (
        -- 1. Yol: Kullanıcı projenin sahibi mi?
        SELECT 1 FROM public.projects p 
        WHERE p.id = p_id AND p.owner_id = auth.uid()
        
        UNION ALL
        
        -- 2. Yol: Kullanıcı projenin ekip üyesi mi?
        SELECT 1 FROM public.team_members tm 
        WHERE tm.project_id = p_id AND tm.user_id = auth.uid()
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 3. YENİ TEMİZ POLİTİKALAR (SELECT)
-- PROJECTS: Erişim hakkı varsa gör
CREATE POLICY "Select projects" ON public.projects
    FOR SELECT USING (public.can_access_project(id));

-- TEAM_MEMBERS: Projeye erişim hakkı varsa üyeleri gör
CREATE POLICY "Select team_members" ON public.team_members
    FOR SELECT USING (public.can_access_project(project_id));

-- 4. INSERT/UPDATE/DELETE (Sahipler için tam yetki)
-- Team Members için sadece sahipler işlem yapabilsin
CREATE POLICY "Manage team_members" ON public.team_members
    FOR ALL USING (
        EXISTS (SELECT 1 FROM public.projects p WHERE p.id = team_members.project_id AND p.owner_id = auth.uid())
    );

-- 5. PROFILES (Zaten güvenli ama tekrar garantiye alalım)
DROP POLICY IF EXISTS "Profiles are readable by authenticated users" ON public.profiles;
CREATE POLICY "Profiles select" ON public.profiles
    FOR SELECT USING (auth.role() = 'authenticated');
