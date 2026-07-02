-- migration_v14_organizations.sql
-- Bu migrasyon işletme/organizasyon yapısını veritabanına ekler.

-- 1. Organizations Tablosu
CREATE TABLE IF NOT EXISTS public.organizations (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    owner_id uuid REFERENCES auth.users DEFAULT auth.uid(),
    subscription_status text DEFAULT 'free',
    created_at timestamptz DEFAULT now()
);

-- 2. Profiles Tablosunu Güncelle
-- org_id: Kullanıcının bağlı olduğu işletme
-- role: Kullanıcının bu işletmedeki rolü (owner, manager, member, etc.)
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS org_id uuid REFERENCES public.organizations(id);
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS role text DEFAULT 'customer';

-- 3. Projects Tablosuna org_id Ekle
-- Projelerin bir işletmeye ait olması zorunluluğu için
ALTER TABLE public.projects ADD COLUMN IF NOT EXISTS org_id uuid REFERENCES public.organizations(id);

-- 4. RLS (Row Level Security) Politikaları
ALTER TABLE public.organizations ENABLE ROW LEVEL SECURITY;

-- Kullanıcılar sadece kendi işletmelerini görebilir
CREATE POLICY "Select Organization: Members Only" ON public.organizations
    FOR SELECT USING (
        id IN (SELECT org_id FROM public.profiles WHERE id = auth.uid())
    );

-- İşletme sahibi tüm yetkilere sahiptir
CREATE POLICY "Manage Organization: Owner Only" ON public.organizations
    FOR ALL USING (owner_id = auth.uid());

-- 5. Mevcut Kayıtları Temizle (Opsiyonel)
-- Eğer daha önce oluşturulmuş projeler varsa, onları ilk oluşturulacak organizasyona bağlamak gerekebilir.
-- Ancak şimdilik yeni kayıtlar için bu yapı zorunlu olacak.
