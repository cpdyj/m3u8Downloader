import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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
}

fun Any?.toJsonTree() = JsonMapper.mapper.valueToTree<JsonNode>(this)!!

fun JsonNode.encode() = JsonMapper.mapper.writeValueAsString(this)!!

fun String.decodeJson() = JsonMapper.mapper.readTree(this)!!

infix fun <T> JsonNode.mapTo(type: Class<T>) = JsonMapper.mapper.treeToValue(this, type)!!


fun File.mkdirIfNotExists(thisIsFolder: Boolean = false): File {
    if (exists()) {
        return this
    } else {
        parentFile.mkdirIfNotExists(true)
        if (thisIsFolder) {
            mkdir()
        } else {
            createNewFile()
        }
    }
    return this
}

