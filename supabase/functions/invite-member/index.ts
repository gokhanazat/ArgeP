import { serve } from 'https://deno.land/std@0.168.0/http/server.ts'
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    const supabaseAdmin = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? '',
      { auth: { persistSession: false } }
    )

    const { email, projectId, role } = await req.json()

    if (!email || !projectId) {
      throw new Error('E-posta ve Proje ID gereklidir.')
    }

    console.log(`[invite-member] Attempting atomic add for ${email} in project ${projectId} (role: ${role})`)

    // ATOMİK SQL FONKSİYONUNU ÇAĞIR
    const { data: result, error: rpcError } = await supabaseAdmin.rpc('add_team_member_by_email', { 
        p_email: email.trim().toLowerCase(),
        p_project_id: projectId,
        p_role: role || 'GOZLEMCI'
    })

    if (rpcError) {
      console.error(`[invite-member] RPC Error: ${rpcError.message}`)
      throw new Error(`Veritabanı erişim hatası: ${rpcError.message}`)
    }

    // RPC sonucunu direkt dön (success: true/false ve error mesajı zaten içinde)
    if (!result || !result.success) {
      console.warn(`[invite-member] RPC returned failure: ${result?.error || 'Unknown error'}`)
      return new Response(JSON.stringify(result || { success: false, error: 'Bilinmeyen hata' }), { 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }, 
        status: 200 // Hata mesajını UI'a 200 ile dönüyoruz ki Result.success(false) olarak yakalansın
      })
    }

    console.log(`[invite-member] Successfully added/updated member: ${email}`)
    return new Response(JSON.stringify(result), { 
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }, 
      status: 200 
    })

  } catch (error: any) {
    console.error(`[invite-member] Global Catch: ${error.message}`)
    return new Response(JSON.stringify({ success: false, error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 400
    })
  }
})
