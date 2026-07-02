import { serve } from 'https://deno.land/std@0.168.0/http/server.ts'
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'apikey, content-type, authorization, x-client-info, x-region',
  'Access-Control-Allow-Methods': 'POST, OPTIONS',
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

    let body
    try {
      body = await req.json()
    } catch (e) {
      throw new Error(`JSON ayrıştırma hatası (Gelen veri bozuk): ${e.message}`)
    }

    const { email, projectId, orgId, role } = body
    const userRole = role || 'GOZLEMCI'
    const targetEmail = email.trim().toLowerCase()

    if (!targetEmail || (!projectId && !orgId)) {
      throw new Error(`E-posta (${targetEmail}) ve (Proje ID veya Organizasyon ID) gereklidir!`)
    }

    console.log(`[invite-member] İşlem -> E-posta: ${targetEmail}, Proje: ${projectId}, Org: ${orgId}, Rol: ${userRole}`)

    // 1. Önce RPC ile mevcut kullanıcıyı eklemeyi dene
    let rpcName = projectId ? 'add_team_member_by_email' : 'add_org_member_by_email'
    let rpcArgs = projectId 
        ? { p_email: targetEmail, p_project_id: projectId, p_role: userRole }
        : { p_email: targetEmail, p_org_id: orgId, p_role: userRole }

    const { data: result, error: rpcError } = await supabaseAdmin.rpc(rpcName, rpcArgs)

    // 2. Eğer kullanıcı bulunamadıysa, davet et
    if (result?.error === 'USER_NOT_FOUND' || (result?.error && result.error.includes('sistemde bulunamadı'))) {
      console.log(`[invite-member] Kullanıcı bulunamadı, davet gönderiliyor: ${targetEmail}`)
      
      const { data: inviteData, error: inviteError } = await supabaseAdmin.auth.admin.inviteUserByEmail(targetEmail, {
        data: { full_name: 'Yeni Üye' }
      })

      if (inviteError) {
        throw new Error(`Davet gönderilemedi: ${inviteError.message}`)
      }

      // Davet sonrası tekrar RPC çağır (Artık auth.users'da kayıt var ama profile henüz trigger ile oluşmamış olabilir)
      // Bu yüzden bir saniye bekleyip tekrar deniyoruz veya doğrudan başarı dönüyoruz.
      return new Response(JSON.stringify({ 
        success: true, 
        message: 'Kullanıcı sistemde bulunamadı, davet e-postası gönderildi. Kayıt olduktan sonra otomatik eklenecektir.' 
      }), { 
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }, 
        status: 200 
      })
    }

    if (rpcError) {
      throw new Error(`SQL Hatası: ${rpcError.message}`)
    }

    return new Response(JSON.stringify(result), { 
      headers: { ...corsHeaders, 'Content-Type': 'application/json' }, 
      status: 200 
    })

  } catch (error: any) {
    console.error(`[invite-member] Global Hata: ${error.message}`)
    return new Response(JSON.stringify({ success: false, error: error.message }), {
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      status: 200 // Hata mesajlarını 200 ile dönerek snackbar'da gösterilmesini sağlıyoruz
    })
  }
})
