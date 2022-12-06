package michigan.mahjong

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import kotlin.reflect.full.declaredMemberProperties

interface MahjongAPIs {
    @Multipart
    @POST("cvresult/")
    suspend fun cvresult(
        @Part("direction") direction: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @POST("recmove/")
    suspend fun recmove(@Body requestBody: RequestBody): Response<ResponseBody>
}

const val HAND_KEY = "HAND"
const val OPEN_KEY = "OPEN"
const val DISCARD_KEY = "DISCARD"
const val ALL_KEY = "ALL"

enum class TileGroup {
    DISCARD, OPEN, HAND
}

enum class Direction {
    LEFT, RIGHT, FRONT, BACK
}

object TileStore: CoroutineScope by MainScope() {

    private val _tiles = mutableStateListOf<Tile>();
    val tiles: List<Tile> = _tiles

    private val _discard_pile = mutableStateListOf<Tile>();
    val discard_pile: List<Tile> = _discard_pile

    private val _open_tiles = mutableStateListOf<Tile>();
    val open_tiles: List<Tile> = _open_tiles

    private val _total_tiles = mutableStateMapOf<Char, MutableList<Int>>(
        'm' to MutableList(10) { 4 },
        'p' to MutableList(10) { 4 },
        's' to MutableList(10) { 4 },
        'z' to MutableList(7) { 4 }
    );
    val total_tiles: Map<Char, MutableList<Int>> = _total_tiles


    private val _discard = mutableStateOf<String?>(null);
    val discard: MutableState<String?> = _discard

    private val _recent_discards = mutableStateListOf<String>();
    val recent_discards: List<String> = _recent_discards

    private val _text = mutableStateOf<String?>(null);
    val text: MutableState<String?> = _text

    private val _part_imgs = mutableStateListOf<Uri?>();
    val part_imgs: List<Uri?> = _part_imgs

    private val _isLoading = mutableStateOf(false)
    val isLoading: MutableState<Boolean> = _isLoading


    private val nFields = Tile::class.declaredMemberProperties.size

    private const val serverUrl = "https://18.222.50.157/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(serverUrl)
        .build()
    private val mahjongAPIs = retrofit.create(MahjongAPIs::class.java)

    private val retrofitExCatcher = CoroutineExceptionHandler { _, error ->
        Log.e("Retrofit exception", error.localizedMessage ?: "NETWORKING ERROR")
    }

    private fun findGroupType(groupType: TileGroup) : SnapshotStateList<Tile> {
        return when(groupType) {
            TileGroup.DISCARD -> _discard_pile
            TileGroup.OPEN -> _open_tiles
            TileGroup.HAND -> _tiles
        }
    }

    fun findType(groupType: TileGroup) : List<Tile> {
        return when(groupType) {
            TileGroup.DISCARD -> discard_pile
            TileGroup.OPEN -> open_tiles
            TileGroup.HAND -> tiles
        }
    }

    private fun findGroupLimit(tileGroup: TileGroup) : Int {
        return when(tileGroup) {
            TileGroup.HAND -> 14
            TileGroup.DISCARD -> 120
            TileGroup.OPEN -> 50
        }
    }

    private fun fillGroup(groupType: TileGroup, input: List<String>) {
        val group = findGroupType(groupType)
        for (i in 0 until minOf(input.size, findGroupLimit(groupType))) {
            group[i].name = input[i]
        }
    }

    fun setTile(name: String, groupType: TileGroup, index: Int) {
        val group = findGroupType(groupType)
        group[index].name = name
    }

    fun setup() {
        reset_total()
        _tiles.clear()
        _discard_pile.clear()
        _open_tiles.clear()
        _part_imgs.clear()
        _recent_discards.clear()
        discard.value = null
        for (i in 0 until findGroupLimit(TileGroup.HAND)) {
            _tiles.add(Tile())
        }
        for (i in 0 until findGroupLimit(TileGroup.OPEN)) {
            _open_tiles.add(Tile())
        }
        for (i in 0 until findGroupLimit(TileGroup.DISCARD)) {
            _discard_pile.add(Tile())
        }
    }

    fun reset(tileGroup: TileGroup) {
        fillGroup(tileGroup, List(findGroupLimit(tileGroup)) { "" })
        discard.value = null
    }

    fun reset_all() {
        enumValues<TileGroup>().forEach {
            fillGroup(it, List(findGroupLimit(it)) { "" })
        }
        discard.value = null
    }

    fun reset_total() {
        _total_tiles.clear()
        _total_tiles.putAll(mapOf(
            'm' to MutableList(10) { 4 },
            'p' to MutableList(10) { 4 },
            's' to MutableList(10) { 4 },
            'z' to MutableList(7) { 4 }
        ))
        Log.i("kilo", total_tiles['m']?.toString() ?: "WHA")
    }

    fun calculateRemaining() {
        reset_total()
        enumValues<TileGroup>().forEach {
            val group = findGroupType(it)
            for (tile in group) {
                Log.i("kilo", tile.name)
                if (tile.name != "") {
                    val number : Int = tile.name[0].digitToInt()
                    val type : Char = tile.name[1]
                    _total_tiles[type]?.set(number - 1, maxOf((_total_tiles[type]?.get(number - 1)?.minus(1)) ?: 0, 0))
                }
            }
        }
        Log.i("kilo", total_tiles['m']?.toString() ?: "WHA")
    }

    fun clearPartImgs() {
        _part_imgs.clear()
    }

    fun addPartUri(imageUri: Uri?) {
        _part_imgs.add(imageUri)
    }

    suspend fun multiple_cvresult(context: Context, tileGroup: TileGroup) {
        if (_part_imgs.size != 4) {
            return
        }
        isLoading.value = true
        discard.value = null
        val frontPhoto = cvresult(context, _part_imgs[0], tileGroup, Direction.FRONT)
        val rightPhoto = cvresult(context, _part_imgs[1], tileGroup, Direction.RIGHT)
        val backPhoto = cvresult(context, _part_imgs[2], tileGroup, Direction.BACK)
        val leftPhoto = cvresult(context, _part_imgs[3], tileGroup, Direction.LEFT)
        if (tileGroup == TileGroup.DISCARD) {
            _recent_discards.clear()
            for (i in 0 until 2) {
                if (frontPhoto.getOrNull(i) != null) {
                    _recent_discards.add(frontPhoto[i])
                }
                if (rightPhoto.getOrNull(i) != null) {
                    _recent_discards.add(rightPhoto[i])
                }
                if (backPhoto.getOrNull(i) != null) {
                    _recent_discards.add(backPhoto[i])
                }
                if (leftPhoto.getOrNull(i) != null) {
                    _recent_discards.add(leftPhoto[i])
                }
            }
        }
        val result = listOf(frontPhoto,rightPhoto,backPhoto,leftPhoto).flatten()
        _part_imgs.clear()
        fillGroup(tileGroup, result)
        isLoading.value = false
    }

    suspend fun cvresult(
        context: Context,
        imageUri: Uri?,
        tileGroup: TileGroup,
        input_direction: Direction = Direction.FRONT
    ): List<String> {
        isLoading.value = if (tileGroup == TileGroup.HAND) true else isLoading.value
        discard.value = null
        text.value = null

        lateinit var response: Response<ResponseBody>

        var result: List<String> = listOf()
        val file = createTmpFileFromUri(context, imageUri ?: Uri.EMPTY, "image")
        val direction = when(input_direction) {
            Direction.FRONT -> "up"
            Direction.BACK -> "left"
            Direction.LEFT -> "right"
            Direction.RIGHT -> "left"
        }
        file?.also{
            withContext(retrofitExCatcher) {
                response = mahjongAPIs.cvresult(
                    direction.toRequestBody("text/plain".toMediaType()),
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

                    reset(tileGroup)

                    val filteredTiles = mutableListOf<String>()
                    for (i in 0 until minOf(tilesReceived.length(), findGroupLimit(tileGroup))) {
                        val tileEntry = tilesReceived[i] as String
                        filteredTiles.add(tileEntry)
                    }
                    var sortedTiles: List<String> = filteredTiles
                    if (tileGroup == TileGroup.HAND) {
                        sortedTiles = filteredTiles.sortedWith(tileComparator)
                        fillGroup(TileGroup.HAND, sortedTiles)
                    }

                    result = sortedTiles
                }
                else {
                    Log.e("cvresult", response.errorBody()?.string() ?: "Retrofit error")
                }
            }
        }
        isLoading.value = if (tileGroup == TileGroup.HAND) false else isLoading.value
        return result
    }

    suspend fun recmove() {
        isLoading.value = true

        if (discard.value != null) {
            isLoading.value = false
            return
        }
        val handArray = tiles.map{ if (it.name == "") {isLoading.value = false;return} else it.name }
        val discardArray = discard_pile.map{ if (it.name != "") it.name else "" }.filter { it != "" }
        val openArray = open_tiles.map{ if (it.name != "") it.name else "" }.filter { it != "" }


        val jsonObj = mapOf(
            "tile_list" to mapOf(
                "hand" to handArray,
                "discard" to discardArray,
                "open" to openArray
            )
        )

        Log.i("kilo", jsonObj.toString())

        val requestBody = JSONObject(jsonObj).toString().toRequestBody("application/json".toMediaType())

        lateinit var response: Response<ResponseBody>

        withContext(retrofitExCatcher) {
            response = mahjongAPIs.recmove(requestBody)
            if (response.isSuccessful) {
                val responseDictionary = try {
                    JSONObject(response.body()?.string() ?: "")
                } catch (e: JSONException) {
                    null
                }

                discard.value = responseDictionary?.get("tile").toString()
                text.value = responseDictionary?.get("text").toString()
            }
            else {
                Log.e("recmove", response.errorBody()?.string() ?: "Retrofit error")
            }
        }

        calculateRemaining()
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