package com.zzzcc.pomodorotimer.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ServerConnection(
    val serverName: String
)

class NetworkRepository(
    private val client: OkHttpClient = sharedClient
) {
    suspend fun checkConnection(): Result<ServerConnection> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url(ConnectionCheckUrl)
                .header("Accept", "application/json")
                .header("User-Agent", "PomodoroTimer-Android")
                .build()

            client.newCall(request).execute().use { response ->
                check(response.isSuccessful) {
                    "Unexpected HTTP status ${response.code}"
                }

                val responseJson = JSONObject(response.body.string())
                check(responseJson.optBoolean("ok")) {
                    "Backend health check failed"
                }

                val serviceName = responseJson.getString("service")
                ServerConnection(serverName = "腾讯云 · $serviceName")
            }
        }
    }

    companion object {
        private const val ConnectionCheckUrl =
            "https://pomodoro-dev-d1gghiq6p79a72113-1452513516.ap-shanghai.app.tcloudbase.com/api/health"

        private val sharedClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}
