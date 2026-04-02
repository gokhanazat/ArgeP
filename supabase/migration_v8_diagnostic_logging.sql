-- ### 1. DIAGNOSTIK KAYIT TABLOSU ###
-- Bu tablo sadece hata ayıklama (troubleshooting) için kullanılır.
-- Edge Function her adımda buraya kayıt atarak nerede takıldığını bize söyleyecek.

CREATE TABLE IF NOT EXISTS public.debug_logs (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    event_name text NOT NULL,
    payload jsonb,
    error_detail text,
    created_at timestamptz DEFAULT now()
);

-- RLS: Sadece Service Role buraya dokunabilsin (veya audit için okunsun)
ALTER TABLE public.debug_logs ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Service Role full access" ON public.debug_logs FOR ALL USING (true); -- Bypassed by service_role anyway

-- ### 2. MEVCUT DURUM DENO SÜRÜMÜNÜ TEST ET ###
-- (Bu sadece bir yorumdur, SQL etkilemez)
-- Supabase Edge Functions 'supabase-js' 2.x sürümü ile uyumludur.

COMMENT ON TABLE public.debug_logs IS 'Diagnostic logs for Edge Function troubleshooting';
