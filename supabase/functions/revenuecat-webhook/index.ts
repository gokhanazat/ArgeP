// RevenueCat → Supabase: ARGEP abonelik durumunu subscriptions tablosuna yazar.
// Uygulama girişte Purchases.logIn(supabaseUserId) çağırdığı için app_user_id = Supabase kullanıcı id'si.
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2';

const ENTITLEMENTS = ['premium', 'ArgeP Pro'];
const ACTIVE = ['INITIAL_PURCHASE', 'RENEWAL', 'UNCANCELLATION', 'PRODUCT_CHANGE', 'NON_RENEWING_PURCHASE', 'SUBSCRIPTION_EXTENDED', 'TEMPORARY_ENTITLEMENT_GRANT'];
const INACTIVE = ['EXPIRATION'];
const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

Deno.serve(async (req) => {
  if (req.headers.get('Authorization') !== `Bearer ${Deno.env.get('RC_WEBHOOK_SECRET')}`) {
    return new Response('unauthorized', { status: 401 });
  }
  const { event } = await req.json();
  if (!event) return new Response('no event', { status: 400 });

  const ids: string[] = [event.app_user_id, ...(event.aliases ?? [])].filter((x: string) => UUID.test(x ?? ''));
  const userId = ids[0];
  if (!userId) return new Response('ignored: anonymous user', { status: 200 });

  const ents: string[] = event.entitlement_ids ?? [];
  if (ents.length && !ents.some((e) => ENTITLEMENTS.includes(e))) return new Response('ignored: other entitlement', { status: 200 });

  let isActive: boolean | null = null;
  if (ACTIVE.includes(event.type)) isActive = true;
  if (INACTIVE.includes(event.type)) isActive = false;
  if (isActive === null) return new Response(`ignored: ${event.type}`, { status: 200 });

  const db = createClient(Deno.env.get('SUPABASE_URL')!, Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!);
  const { error } = await db.from('subscriptions').upsert({
    user_id: userId,
    plan_type: isActive ? 'revenuecat_premium' : 'free',
    is_active: true,
    end_date: event.expiration_at_ms ? new Date(event.expiration_at_ms).toISOString() : null,
    updated_at: new Date().toISOString(),
  });
  if (error) return new Response(error.message, { status: 500 });
  return new Response('ok', { status: 200 });
});
