-- ### 1. TÜM ESKİ POLİTİKALARI TEMİZLE (NUCLEAR CLEANUP) ###
-- Aşağıdaki tüm tablo ve politika isimleri sistemin kilitlenmesine sebep olan kalıntılardır.

-- PROJECTS Tablosu
DROP POLICY IF EXISTS "Select projects" ON public.projects;
DROP POLICY IF EXISTS "Users can view projects they are members of" ON public.projects;
DROP POLICY IF EXISTS "Project view for owner and member" ON public.projects;
DROP POLICY IF EXISTS "Select Projects: Members or Owner" ON public.projects;
DROP POLICY IF EXISTS "Owners can update/insert projects" ON public.projects;
DROP POLICY IF EXISTS "Manage Projects: Owner Only" ON public.projects;

-- TEAM_MEMBERS Tablosu
DROP POLICY IF EXISTS "Select team_members" ON public.team_members;
DROP POLICY IF EXISTS "Project owners full access" ON public.team_members;
DROP POLICY IF EXISTS "Team members view colleagues" ON public.team_members;
DROP POLICY IF EXISTS "Select Team: Project Members" ON public.team_members;
DROP POLICY IF EXISTS "Manage Team: PM Only" ON public.team_members;
DROP POLICY IF EXISTS "Team members are viewable by members of the same project" ON public.team_members;
DROP POLICY IF EXISTS "Project owners can see all team members" ON public.team_members;
DROP POLICY IF EXISTS "Team members can see project colleagues" ON public.team_members;
DROP POLICY IF EXISTS "Team members can see themselves and project colleagues" ON public.team_members;
DROP POLICY IF EXISTS "Public team members select" ON public.team_members;
DROP POLICY IF EXISTS "Manage team_members" ON public.team_members;

-- TASKS Tablosu
DROP POLICY IF EXISTS "Select Tasks: Project Members" ON public.tasks;
DROP POLICY IF EXISTS "Insert Tasks: PM Only" ON public.tasks;
DROP POLICY IF EXISTS "Update Tasks: PM or Assigned User" ON public.tasks;
DROP POLICY IF EXISTS "Delete Tasks: PM Only" ON public.tasks;
DROP POLICY IF EXISTS "View tasks of accessible milestones" ON public.tasks;
DROP POLICY IF EXISTS "Assigned users can update tasks" ON public.tasks;
DROP POLICY IF EXISTS "Project owners can manage tasks" ON public.tasks;

-- MILESTONES Tablosu
DROP POLICY IF EXISTS "Select Milestones: Project Members" ON public.milestones;
DROP POLICY IF EXISTS "Manage Milestones: PM or Technical Lead" ON public.milestones;
DROP POLICY IF EXISTS "View milestones of accessible projects" ON public.milestones;
DROP POLICY IF EXISTS "Project owners can manage milestones" ON public.milestones;

-- EXPENSES Tablosu
DROP POLICY IF EXISTS "Managers can manage expenses" ON public.expenses;
DROP POLICY IF EXISTS "Team members can view expenses" ON public.expenses;

-- PROFILES Tablosu
DROP POLICY IF EXISTS "Profiles select" ON public.profiles;
DROP POLICY IF EXISTS "Public profiles are viewable by everyone" ON public.profiles;
DROP POLICY IF EXISTS "Users can update their own profile" ON public.profiles;
DROP POLICY IF EXISTS "Users can insert their own profile" ON public.profiles;
DROP POLICY IF EXISTS "Profiles are readable by authenticated users" ON public.profiles;

-- ### 2. TEMİZ ERİŞİM KONTROL FONKSİYONU ###
-- SECURITY DEFINER kullanarak RLS döngüsünü tamamen kırıyoruz.
CREATE OR REPLACE FUNCTION public.can_access_project(p_id uuid)
RETURNS boolean AS $$
BEGIN
    -- Kullanıcı projenin sahibi veya üyesi ise true döner
    RETURN EXISTS (
        SELECT 1 FROM public.projects p WHERE p.id = p_id AND p.owner_id = auth.uid()
        UNION ALL
        SELECT 1 FROM public.team_members tm WHERE tm.project_id = p_id AND tm.user_id = auth.uid()
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ### 3. YEPYENİ VE KARARLI POLİTİKALARI UYGULA ###

-- PROJECTS: View for owner and members
CREATE POLICY "Select projects" ON public.projects FOR SELECT USING (public.can_access_project(id));
CREATE POLICY "Manage projects" ON public.projects FOR ALL USING (auth.uid() = owner_id);

-- TEAM_MEMBERS: View for project colleagues, Manage for owner
CREATE POLICY "Select team_members" ON public.team_members FOR SELECT USING (public.can_access_project(project_id));
CREATE POLICY "Manage team_members" ON public.team_members FOR ALL USING (
    EXISTS (SELECT 1 FROM public.projects p WHERE p.id = team_members.project_id AND p.owner_id = auth.uid())
);

-- MILESTONES / TASKS / EXPENSES: All follow the project-access rule
CREATE POLICY "Select milestones" ON public.milestones FOR SELECT USING (public.can_access_project(project_id));
CREATE POLICY "Select tasks" ON public.tasks FOR SELECT USING (
    EXISTS (SELECT 1 FROM public.milestones m WHERE m.id = tasks.milestone_id AND public.can_access_project(m.project_id))
);
CREATE POLICY "Select expenses" ON public.expenses FOR SELECT USING (public.can_access_project(project_id));

-- PROFILES: Herkes profilleri görebilir
CREATE POLICY "Select profiles" ON public.profiles FOR SELECT USING (auth.role() = 'authenticated');
CREATE POLICY "Update profiles" ON public.profiles FOR UPDATE USING (auth.uid() = id);
