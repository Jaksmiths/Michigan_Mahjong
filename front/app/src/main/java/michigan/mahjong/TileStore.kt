package michigan.mahjong


import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object TileStore {
    private val _tiles = mutableStateListOf<Tile>();
    val tiles: List<Tile> = _tiles
    private val nFields = Tile::class.declaredMemberProperties.size

    private lateinit var queue: RequestQueue
    private const val serverUrl = ""

    fun reset() {
        for (i in 1..14) {
            _tiles.add(Tile("empty"))
        }
    }


//
//    fun postTiles(context: Context, tile: Tile) {
//        val jsonObj = mapOf(
//            "username" to chatt.username,
//            "message" to chatt.message,
//            "audio" to chatt.audio
//        )
//        val postRequest = JsonObjectRequest(Request.Method.POST,
//            serverUrl+"postaudio/", JSONObject(jsonObj),
//            {
//                Log.d("postChatt", "chatt posted!")
//                getChatts(context)
//            },
//            { error -> Log.e("postChatt", error.localizedMessage ?: "JsonObjectRequest error") }
//        )
//
//        if (!this::queue.isInitialized) {
//            queue = newRequestQueue(context)
//        }
//        queue.add(postRequest)
//    }
//
//    fun getTiles(context: Context) {
//        val getRequest = JsonObjectRequest(serverUrl+"getaudio/",
//            { response ->
//                _chatts.clear()
//                val chattsReceived = try { response.getJSONArray("chatts") } catch (e: JSONException) { JSONArray() }
//                for (i in 0 until chattsReceived.length()) {
//                    val chattEntry = chattsReceived[i] as JSONArray
//                    if (chattEntry.length() == nFields) {
//                        _chatts.add(Chatt(username = chattEntry[0].toString(),
//                            message = chattEntry[1].toString(),
//                            timestamp = chattEntry[2].toString(),
//                            audio = chattEntry[3].toString()
//                        ))
//                    } else {
//                        Log.e("getChatts", "Received unexpected number of fields: " + chattEntry.length().toString() + " instead of " + nFields.toString())
//                    }
//                }
//            }, { }
//        )
//
//        if (!this::queue.isInitialized) {
//            queue = newRequestQueue(context)
//        }
//        queue.add(getRequest)
//    }
}