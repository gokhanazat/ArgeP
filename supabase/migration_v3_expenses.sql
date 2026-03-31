-- 1. PROJECTS Tablosuna Tarih Sütunları Ekleme
ALTER TABLE public.projects ADD COLUMN IF NOT EXISTS start_date timestamptz DEFAULT now();
ALTER TABLE public.projects ADD COLUMN IF NOT EXISTS end_date timestamptz;

-- 2. EXPENSES Tablosu Oluşturma
CREATE TABLE IF NOT EXISTS public.expenses (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES public.projects(id) ON DELETE CASCADE NOT NULL,
    amount numeric NOT NULL DEFAULT 0,
    category text DEFAULT 'Diğer', -- Personel, Yazılım, Donanım, Diğer vb.
    description text,
    expense_date timestamptz DEFAULT now(),
    created_by uuid REFERENCES auth.users(id),
    created_at timestamptz DEFAULT now()
);

-- RLS Etkinleştirme
ALTER TABLE public.expenses ENABLE ROW LEVEL SECURITY;

-- 3. RLS POLICIES: EXPENSES
-- Proje Sahibi, PM veya Admin harcamaları yönetebilir
CREATE POLICY "Managers can manage expenses" ON public.expenses
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.projects p
            LEFT JOIN public.team_members tm ON tm.project_id = p.id
            WHERE p.id = expenses.project_id 
            AND (p.owner_id = auth.uid() OR (tm.user_id = auth.uid() AND tm.role = 'PROJE_MUDURU') OR
                 EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND is_admin = true))
        )
    );

-- Ekip Üyeleri harcamaları görebilir (Sadece OKUMA)
CREATE POLICY "Team members can view expenses" ON public.expenses
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.team_members 
            WHERE project_id = expenses.project_id AND user_id = auth.uid()
        )
    );

-- 4. OTOMATİK BÜTÇE GÜNCELLEME (TRIGGER)
CREATE OR REPLACE FUNCTION public.update_project_budget_spent()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        UPDATE public.projects
        SET budget_spent = (SELECT COALESCE(SUM(amount), 0) FROM public.expenses WHERE project_id = NEW.project_id)
        WHERE id = NEW.project_id;
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE public.projects
        SET budget_spent = (SELECT COALESCE(SUM(amount), 0) FROM public.expenses WHERE project_id = OLD.project_id)
        WHERE id = OLD.project_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER trigger_update_budget
AFTER INSERT OR UPDATE OR DELETE ON public.expenses
FOR EACH ROW EXECUTE FUNCTION public.update_project_budget_spent();
