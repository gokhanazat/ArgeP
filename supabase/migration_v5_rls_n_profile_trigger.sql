-- 1. TEAM_MEMBERS RLS GÜNCELLEMESİ (Döngüsel Bağımlılığı Giderme)
-- Mevcut politikaları temizle
DROP POLICY IF EXISTS "Team members are viewable by members of the same project" ON public.team_members;

-- Yeni, döngüsel olmayan SELECT politikası:
-- Proje sahipleri tüm üyeleri görebilir
CREATE POLICY "Project owners can see all team members" ON public.team_members
    FOR SELECT USING (
        EXISTS (SELECT 1 FROM public.projects p WHERE p.id = team_members.project_id AND p.owner_id = auth.uid())
    );

-- Ekip üyeleri kendilerini ve aynı projedeki diğer üyeleri görebilir (basit kontrol)
CREATE POLICY "Team members can see themselves and project colleagues" ON public.team_members
    FOR SELECT USING (
        user_id = auth.uid() OR
        project_id IN (SELECT tm.project_id FROM public.team_members tm WHERE tm.user_id = auth.uid())
    );

-- 2. PROJECTS RLS GÜNCELLEMESİ
DROP POLICY IF EXISTS "Users can view projects they are members of" ON public.projects;
CREATE POLICY "Users can view projects they are members of" ON public.projects
    FOR SELECT USING (
        auth.uid() = owner_id OR 
        id IN (SELECT tm.project_id FROM public.team_members tm WHERE tm.user_id = auth.uid())
    );

-- 3. OTOMATİK PROFİL OLUŞTURMA TETİKLEYİCİSİ (Profile Trigger)
-- Bu fonksiyon yeni bir kullanıcı kayıt olduğunda çalışır ve profiles tablosuna temel bir kayıt atar.
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.profiles (id, full_name, avatar_url, department)
  VALUES (
    NEW.id, 
    COALESCE(NEW.raw_user_meta_data->>'full_name', NEW.email), -- İsim yoksa e-posta kullan
    NEW.raw_user_meta_data->>'avatar_url',
    COALESCE(NEW.raw_user_meta_data->>'department', 'Ar-Ge Departmanı')
  );
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Tetikleyici: auth.users tablosuna kayıt düştüğünde çalışır
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- Mevcut kullanıcılar için eksik profilleri oluştur (Opsiyonel ama güvenlik için iyi)
INSERT INTO public.profiles (id, full_name, department)
SELECT id, email, 'Ar-Ge Departmanı'
FROM auth.users
ON CONFLICT (id) DO NOTHING;
