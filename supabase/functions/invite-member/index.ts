import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders })
  }

  try {
    // Service role ile çalış → auth.users'a erişebiliriz
    const supabaseAdmin = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    )

    // İsteği yapan kullanıcıyı doğrula
    const authHeader = req.headers.get('Authorization')!
    const token = authHeader.replace('Bearer ', '')
    const { data: { user: requester }, error: authError } = await supabaseAdmin.auth.getUser(token)
    if (authError || !requester) {
      throw new Error('Unauthorized: Kullanıcı doğrulanamadı.')
    }

    const { email: inputEmail, projectId, role } = await req.json()
    const email = inputEmail?.trim()?.toLowerCase()
    if (!email || !projectId || !role) {
      throw new Error('Eksik parametre: email, projectId ve role zorunludur.')
    }

    console.log(`[invite-member] ${requester.email} → ${email} (project: ${projectId}, role: ${role})`)

    console.log(`[invite-member] Searching user for: ${email}`)
    
    let userId: string

    // 1. E-posta ile kullanıcı UUID'sini güvenli RPC üzerinden bul
    const { data: foundUserId, error: rpcError } = await supabaseAdmin.rpc('get_user_id_by_email', { 
        p_email: email 
    })
    
    if (rpcError || !foundUserId) {
      console.log(`[invite-member] User NOT FOUND or RPC Error: ${rpcError?.message || 'Empty response'}`)
      return new Response(JSON.stringify({
        success: false,
        error: `${email} sistemde bulunamadı. Lütfen kullanıcının önce uygulamaya kayıt olduğundan emin olun.`
      }), { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 404 })
    }

    userId = foundUserId
    console.log(`[invite-member] Found user UUID: ${userId}`)

    // 2. Doğrudan team_members'a ekle
    const { error: teamError } = await supabaseAdmin
      .from('team_members')
      .upsert({
        user_id: userId,
        project_id: projectId,
        role: role
      }, { onConflict: 'user_id, project_id' })

    if (teamError) {
      console.error(`[invite-member] team_members WRITE FAIL: ${teamError.message}`)
      throw new Error(`Veritabanına yazılamadı: ${teamError.message}`)
    }

    console.log(`[invite-member] SUCCESS: ${userId} added to ${projectId}`)

    return new Response(JSON.stringify({
      success: true,
      status: 'added',
      userId,
      invitedEmail: email
    }), { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 200 })

  } catch (error: any) {
    console.error('[invite-member] ERROR:', error.message)
    return new Response(JSON.stringify({
      success: false,
      error: error.message
    }), { headers: { ...corsHeaders, 'Content-Type': 'application/json' }, status: 400 })
  }
})
