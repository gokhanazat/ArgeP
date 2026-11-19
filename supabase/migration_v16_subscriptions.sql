-- RevenueCat webhook'unun yazdığı abonelik tablosu (telefon + web aynı yerden okur)
create table if not exists public.subscriptions (
    user_id    uuid primary key references auth.users(id) on delete cascade,
    plan_type  text not null default 'free',
    is_active  boolean not null default true,
    end_date   timestamptz,
    updated_at timestamptz not null default now()
);

alter table public.subscriptions enable row level security;

-- Kullanıcı yalnızca kendi aboneliğini OKUYABİLİR. Yazma yalnızca webhook (service role) ile yapılır.
drop policy if exists "Users can view own subscription" on public.subscriptions;
create policy "Users can view own subscription"
    on public.subscriptions for select
    using (auth.uid() = user_id);
