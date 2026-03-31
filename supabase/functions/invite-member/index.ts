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
    const supabaseClient = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
    )

    // Token ve kullanıcıyı al
    const authHeader = req.headers.get('Authorization')!
    const token = authHeader.replace('Bearer ', '')
    const { data: { user: requester }, error: authError } = await supabaseClient.auth.getUser(token)

    if (authError || !requester) throw new Error('Unauthorized')

    const { email, projectId, role } = await req.json()
    const isGlobalInvite = !projectId || projectId === 'global'
    const finalProjectId = isGlobalInvite ? null : projectId

    // 1. Yetki Kontrolü
    console.log(`Checking permission for user: ${requester.id} on project: ${projectId}`)
    
    // Admin mi?
    const { data: profile } = await supabaseClient
      .from('profiles')
      .select('is_admin')
      .eq('id', requester.id)
      .single()

    const isAdmin = profile?.is_admin === true

    if (!isAdmin) {
      if (isGlobalInvite) {
        return new Response(JSON.stringify({ error: 'Global davet yetkiniz yok. Sadece Adminler kişi ekleyebilir.' }), {
          headers: { ...corsHeaders, 'Content-Type': 'application/json' },
          status: 403,
        })
      }

      // Proje bazlı PM kontrolü
      const { data: isManager } = await supabaseClient.rpc('is_project_manager', { 
        p_project_id: projectId, 
        p_user_id: requester.id 
      })

      if (!isManager) {
        return new Response(JSON.stringify({ error: 'Bu işlem için yetkiniz yok (PM veya Admin olmalısınız).' }), {
          headers: { ...corsHeaders, 'Content-Type': 'application/json' },
          status: 403,
        })
      }
    }

    // 2. Invitations tablosuna kayıt at
    const { error: inviteDbError } = await supabaseClient
      .from('invitations')
      .insert({
        email,
        project_id: finalProjectId,
        role,
        invited_by: requester.id
      })

    if (inviteDbError) throw inviteDbError

    // 3. Supabase Auth ile davet gönder
    const { error: authInviteError } = await supabaseClient.auth.admin.inviteUserByEmail(email, {
        data: { 
            invited_by: requester.id,
            project_id: finalProjectId,
            target_role: role
        }
    })

    if (authInviteError) throw authInviteError

    return new Response(JSON.stringify({ success: true, invitedEmail: email }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 200,
    })

  } catch (error: any) {
    console.error('Error:', error)
    return new Response(JSON.stringify({ error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 400,
    })
  }
})
