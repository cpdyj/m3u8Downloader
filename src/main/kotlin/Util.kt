import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import java.io.File
import java.net.URL

fun String?.toFileOrNull() = runCatching { File(this) }.getOrNull()

fun String?.toUrlOrNull() = runCatching { URL(this) }.getOrNull()

fun registerShutdownHook(o: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(Thread(o))
}


object JsonMapper {
    val mapper = io.vertx.core.json.jackson.DatabindCodec.prettyMapper()!!

    init {
        io.vertx.core.json.jackson.DatabindCodec.prettyMapper().registerKotlinModule()
        io.vertx.core.json.jackson.DatabindCodec.mapper().registerKotlinModule()
    }

    val kindMapper = mapper.copy().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    fun getMapper(ignoreUnknow: Boolean): ObjectMapper = if (ignoreUnknow) kindMapper else mapper
}

fun Any?.toJsonTree() = JsonMapper.mapper.valueToTree<JsonNode>(this)!!

fun JsonNode.encode() = JsonMapper.mapper.writeValueAsString(this)!!

fun String.decodeJson() = JsonMapper.mapper.readTree(this)!!

fun <T> JsonNode.mapTo(type: Class<T>, ignoreUnknow: Boolean = false) =
    JsonMapper.getMapper(ignoreUnknow).treeToValue(this, type)!!

inline fun <reified T> JsonNode.mapTo(obj: T, ignoreUnknow: Boolean = false) =
    JsonMapper.getMapper(ignoreUnknow).readerForUpdating(obj).treeToValue<T>(this)


fun File.makeTreeIfNotExists(thisIsFolder: Boolean = false): File {
    if (exists()) {
        return this
    } else {
        parentFile.makeTreeIfNotExists(true)
        if (thisIsFolder) {
            mkdir()
        } else {
            createNewFile()
        }
    }
    return this
}

