package com.factory.display.data.remote

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import com.factory.display.data.model.DisplayData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FactoryWebSocketListener @Inject constructor() : WebSocketListener() {

    var onConnectionFailure: (() -> Unit)? = null
    var onDataReceived: ((List<DisplayData>) -> Unit)? = null

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, DisplayData::class.java)
    private val jsonAdapter: JsonAdapter<List<DisplayData>> = moshi.adapter(listType)

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("FactoryWebSocket", "Connection opened")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("FactoryWebSocket", "Receiving: $text")
        try {
            val data = jsonAdapter.fromJson(text)
            if (data != null) {
                onDataReceived?.invoke(data)
            }
        } catch (e: Exception) {
            Log.e("FactoryWebSocket", "Error parsing JSON", e)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("FactoryWebSocket", "Receiving bytes: $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("FactoryWebSocket", "Closing: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("FactoryWebSocket", "Error: ${t.message}", t)
        onConnectionFailure?.invoke()
    }
}
