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
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "PomodoroTimer-Android")
                .build()

            client.newCall(request).execute().use { response ->
                check(response.isSuccessful) {
                    "Unexpected HTTP status ${response.code}"
                }

                val responseJson = JSONObject(response.body.string())
                val repositoryName = responseJson.getString("full_name")
                ServerConnection(serverName = repositoryName)
            }
        }
    }

    companion object {
        private const val ConnectionCheckUrl =
            "https://api.github.com/repos/zcx28/PomodoroTimer"

        private val sharedClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}
