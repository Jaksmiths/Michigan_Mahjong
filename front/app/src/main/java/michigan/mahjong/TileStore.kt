package michigan.mahjong


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import com.android.volley.RequestQueue
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import kotlin.reflect.full.declaredMemberProperties

interface MahjongAPIs {
    @Multipart
    @POST("cvresult/")
    suspend fun cvresult(
        @Part("location") location: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @POST("recmove/")
    suspend fun recmove(@Body requestBody: RequestBody): Response<ResponseBody>
}

object TileStore: CoroutineScope by MainScope() {
    private val _tiles = mutableStateListOf<Tile>();
    val tiles: List<Tile> = _tiles

    private val _discard = mutableStateOf<String?>(null);
    val discard: MutableState<String?> = _discard

    private val _isLoading = mutableStateOf(false)
    val isLoading: MutableState<Boolean> = _isLoading

    private val _cameraActive = mutableStateOf(true)
    val cameraActive: MutableState<Boolean> = _cameraActive


    private val nFields = Tile::class.declaredMemberProperties.size

    private const val serverUrl = "https://3.139.106.242/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(serverUrl)
        .build()
    private val mahjongAPIs = retrofit.create(MahjongAPIs::class.java)

    private val retrofitExCatcher = CoroutineExceptionHandler { _, error ->
        Log.e("Retrofit exception", error.localizedMessage ?: "NETWORKING ERROR")
    }

    fun setup() {
        _tiles.clear()
        for (i in 0 until 14) {
            _tiles.add(Tile())
        }
        discard.value = null
    }

    fun reset() {
        for (i in 0 until 14) {
            _tiles[i] = Tile()
        }
        Log.i("kilo", _tiles.map{ it.name }.toString())
        discard.value = null
    }

//    fun reset() {
//        _tiles.clear()
//        for (i in 0 until 14) {
//            _tiles.add(Tile())
//        }
//        discard.value = null
//    }

    suspend fun cvresult(context: Context, imageUri: Uri?) {
        isLoading.value = true
        cameraActive.value = false
        discard.value = null
        lateinit var response: Response<ResponseBody>

        val file = createTmpFileFromUri(context, imageUri ?: Uri.EMPTY, "image")
        val location = "hand"
        file?.let{
            withContext(retrofitExCatcher) {
                response = mahjongAPIs.cvresult(
                    location.toRequestBody("text/plain".toMediaType()),
                    image = MultipartBody.Part
                        .createFormData(
                            name = "image",
                            filename = it.name,
                            body = it.asRequestBody("image/*".toMediaType())
                        )
                )
                it.delete()
                if (response.isSuccessful) {
                    Log.i("kilo", "SUCCESS")
                    val tilesReceived = try {
                        JSONObject(response.body()?.string() ?: "").getJSONArray("tile_list")
                    } catch (e: JSONException) {
                        JSONArray()
                    }
                    reset()
                    
                    val filteredTiles = mutableListOf<String>()
                    for (i in 0 until minOf(tilesReceived.length(), 14)) {
                        val tileEntry = tilesReceived[i] as String
                        filteredTiles.add(tileEntry)
                    }
                    val sortedTiles = filteredTiles.sortedWith(tileComparator)
                    for (i in sortedTiles.indices) {
                        _tiles[i].name = sortedTiles[i]
                    }

                }
                else {
                    Log.e("cvresult", response.errorBody()?.string() ?: "Retrofit error")
                }
            }
        }
        isLoading.value = false
    }

    suspend fun recmove() {
        if (discard.value != null) return
        val tileNameArray = tiles.map{ it -> if (it.name == "") return else it.name}

        isLoading.value = true
        val jsonObj = mapOf(
            "tile_list" to mapOf(
                "hand" to tileNameArray
            )
        )

        Log.i("kilo", jsonObj.toString())

        val requestBody = JSONObject(jsonObj).toString().toRequestBody("application/json".toMediaType())

        lateinit var response: Response<ResponseBody>

        withContext(retrofitExCatcher) {
            response = mahjongAPIs.recmove(requestBody)
            if (response.isSuccessful) {
                val tileReceived = try {
                    JSONObject(response.body()?.string() ?: "").get("tile").toString()
                } catch (e: JSONException) {
                    null
                }
                Log.i("kilo", tileReceived.toString())

                discard.value = tileReceived.toString()
            }
            else {
                Log.e("recmove", response.errorBody()?.string() ?: "Retrofit error")
            }
        }
        isLoading.value = false
    }
}

private val tileComparator =  Comparator<String> { a, b ->
    when {
        (a == b) -> 0
        (a == "") -> 1
        (b == "") -> -1
        (a.length == 2 && b.length == 2) -> {
            when {
                (a[1] == b[1]) -> {
                    when {
                        (a[0] > b[0]) -> 1
                        else -> -1
                    }
                }
                (a[1] > b[1]) -> 1
                else -> -1
            }
        }
        else -> 1
    }
}