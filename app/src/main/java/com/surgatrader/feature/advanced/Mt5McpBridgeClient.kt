package com.surgatrader.feature.advanced

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class Mt5AccountData(
    val server: String,
    val broker: String,
    val login: String,
    val name: String,
    val type: String,
    val balance: Double,
    val equity: Double,
    val margin: Double,
    val marginFree: Double,
    val profit: Double,
    val currency: String
)

@Singleton
class Mt5McpBridgeClient @Inject constructor(
    private val gson: Gson
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private var currentSessionId: String? = null

    /**
     * Inisialisasi sesi MCP dengan server MetaTrader 5 lokal
     */
    suspend fun initializeSession(baseUrl: String, token: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val initJson = """
                {
                    "jsonrpc": "2.0",
                    "method": "initialize",
                    "params": {
                        "protocolVersion": "2025-06-18",
                        "capabilities": {},
                        "clientInfo": {
                            "name": "SurgaTrader",
                            "version": "1.0"
                        }
                    },
                    "id": 1
                }
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .post(initJson.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP Error ${response.code}: ${response.message}"))
            }

            val sessionId = response.header("Mcp-Session-Id") ?: response.header("x-session-id")
            currentSessionId = sessionId

            // Kirim notifikasi initialized
            val notifJson = """
                {
                    "jsonrpc": "2.0",
                    "method": "notifications/initialized"
                }
            """.trimIndent()

            val notifReqBuilder = Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
            if (sessionId != null) {
                notifReqBuilder.addHeader("Mcp-Session-Id", sessionId)
            }

            try {
                client.newCall(notifReqBuilder.post(notifJson.toRequestBody("application/json".toMediaType())).build()).execute().close()
            } catch (_: Exception) {}

            Result.success(sessionId ?: "active")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Memanggil tool get_trading_account_info untuk mengambil Saldo, Equity, Broker & Margin
     */
    suspend fun getTradingAccountInfo(baseUrl: String, token: String): Result<Mt5AccountData> = withContext(Dispatchers.IO) {
        try {
            // Pastikan sesi sudah dibuat
            if (currentSessionId == null) {
                val initRes = initializeSession(baseUrl, token)
                if (initRes.isFailure) {
                    return@withContext Result.failure(initRes.exceptionOrNull() ?: Exception("Gagal inisialisasi sesi"))
                }
            }

            val callJson = """
                {
                    "jsonrpc": "2.0",
                    "method": "tools/call",
                    "params": {
                        "name": "get_trading_account_info",
                        "arguments": {}
                    },
                    "id": 2
                }
            """.trimIndent()

            val reqBuilder = Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
            currentSessionId?.let { reqBuilder.addHeader("Mcp-Session-Id", it) }

            val response = client.newCall(reqBuilder.post(callJson.toRequestBody("application/json".toMediaType())).build()).execute()
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gagal mengambil data akun: ${response.code}"))
            }

            // Parse response
            val rootObj = gson.fromJson(bodyString, JsonObject::class.java)
            val resultObj = rootObj.getAsJsonObject("result")
            val contentArr = resultObj?.getAsJsonArray("content")
            val textContent = contentArr?.get(0)?.asJsonObject?.get("text")?.asString

            if (textContent != null) {
                val dataObj = gson.fromJson(textContent, JsonObject::class.java)
                val accObj = dataObj.getAsJsonObject("account")
                val accountData = Mt5AccountData(
                    server = accObj.get("server")?.asString ?: "",
                    broker = accObj.get("broker")?.asString ?: "",
                    login = accObj.get("login")?.asString ?: "",
                    name = accObj.get("name")?.asString ?: "",
                    type = accObj.get("type")?.asString ?: "",
                    balance = accObj.get("balance")?.asDouble ?: 0.0,
                    equity = accObj.get("equity")?.asDouble ?: 0.0,
                    margin = accObj.get("margin")?.asDouble ?: 0.0,
                    marginFree = accObj.get("margin_free")?.asDouble ?: 0.0,
                    profit = accObj.get("profit")?.asDouble ?: 0.0,
                    currency = accObj.get("currency")?.asString ?: "USC"
                )
                Result.success(accountData)
            } else {
                Result.failure(Exception("Format data akun tidak sesuai"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
