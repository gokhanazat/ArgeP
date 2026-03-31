import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.argesurec.shared.SupabaseConfig
import com.argesurec.shared.initKoin
import com.argesurec.shared.navigation.AppNavigation
import com.argesurec.shared.viewmodel.AuthViewModel
import kotlinx.browser.document
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.w3c.dom.HTMLDivElement

class WebApp : KoinComponent {
    val authViewModel: AuthViewModel by inject()
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val loading = document.getElementById("loading") as? HTMLDivElement
    try {
        initKoin(
            supabaseUrl = SupabaseConfig.URL,
            supabaseKey = SupabaseConfig.ANON_KEY
        )
        val webApp = WebApp()
        
        // Başarılıysa loader'ı kaldır
        loading?.style?.display = "none"
        
        CanvasBasedWindow(
            title = "Ar-Ge Süreç Yönetimi",
            canvasElementId = "compose-target"
        ) {
            LaunchedEffect(Unit) {
                webApp.authViewModel.checkSession()
            }
            AppNavigation()
        }
    } catch (e: Throwable) {
        // Hata varsa loader spinner'ını hata mesajıyla değiştir
        if (loading != null) {
            loading.innerHTML = """
                <div style="color: #EF4444; font-weight: bold; text-align: center; padding: 20px;">
                    <div style="font-size: 24px; margin-bottom: 12px;">⚠️ Başlatma Hatası</div>
                    <div style="font-size: 16px;">${e.message ?: "Bilinmeyen bir hata oluştu."}</div>
                    ${if (SupabaseConfig.URL.contains("PLACEHOLDER")) 
                        "<div style='color: #FBBF24; margin-top: 12px; font-size: 14px;'>İpucu: Supabase anahtarları enjekte edilememiş!</div>" 
                      else ""}
                </div>
            """.trimIndent()
        }
    }
}
