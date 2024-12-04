import android.util.Base64
import org.json.JSONObject

fun extractUserId(token: String): String {
    try {
        // Divide el token en sus partes (header, payload, signature)
        val parts = token.split(".")
        if (parts.size < 2) return ""

        // Decodifica el payload (segunda parte del token)
        val payload = String(Base64.decode(parts[1], Base64.DEFAULT))

        // Convierte el payload a un objeto JSON y extrae el `nameid`
        val json = JSONObject(payload)
        return json.optString("nameid", "")
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}
