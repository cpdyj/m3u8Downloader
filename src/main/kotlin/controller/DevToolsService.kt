package controller

import FileList
import SingleFile
import com.fasterxml.jackson.databind.JsonNode
import decodeJson
import encode
import io.vertx.core.http.WebsocketVersion
import io.vertx.core.http.impl.headers.VertxHttpHeaders
import io.vertx.kotlin.core.http.webSocketAbsAwait
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import toJsonTree
import vertx
import waitForResponse
import java.util.*
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

object DevToolsService {
    private val vertxContext by lazy { vertx.orCreateContext }
    val scope by lazy { CoroutineScope(SupervisorJob() + vertxContext.dispatcher()) }
    private val httpClient by lazy { vertx.createHttpClient() }

    private var watchJob: Job? = null

    fun watchIt() {
        val pattern = Regex("([^./]+)\\.(m3u8|mp4)(\\?|$|#)")
        scope.launch {
            watchJob?.cancel()
            watchJob = launch {
                runCatching {
                    pStatus("devtools connecting")
                    val ws = httpClient.webSocketAbsAwait(
                        GlobalStatus.devToolsWebsocketEndpoint.value,
                        VertxHttpHeaders(),
                        WebsocketVersion.V13,
                        emptyList()
                    )
                    pStatus("devtools connected")
                    ws.writeTextMessage(mapOf("id" to 1, "method" to "Network.enable").toJsonTree().encode())
                    val continuationRef=AtomicReference<Continuation<Unit>?>(null)
//                    var continuation: Continuation<Unit>? = null
                    val waitList = LinkedList<JsonNode>()
                    ws.textMessageHandler {
                        //                        println("recv: $it")
                        if (it.indexOf("m3u8") > -1) {
//                            println(it)
//                            println(pattern.find(it)?.groupValues)
                        }
                        waitList.addLast(it.decodeJson())
                        println("2 out")
                        continuationRef.get()?.resume(Unit)?: println("continuation is null")
                        continuationRef.set(null)
//                        continuation?.resume(Unit)?: println("continuation is null")
//                        continuation=null
                    }
                    ws.closeHandler { continuationRef.get()?.resume(Unit) }
                    withContext(Dispatchers.JavaFx) {
                        GlobalStatus.medias.clear()
                    }
                    while (isActive && !ws.isClosed) {
                        println(1)
                        var node = waitList.takeIf{it.isNotEmpty()}?.removeFirst()
                        if (node==null){
                            suspendCancellableCoroutine<Unit> {
                                it.invokeOnCancellation { ws.close() }
//                            continuation=it
                                continuationRef.set(it)
                                println(2)
                            }
                            if (waitList.isNotEmpty()){
                                node=waitList.removeFirst()
                                println(4)
                            }
                        }
                        println(3)
                        println("null?= ${node==null} $isActive")
                        if (node == null || !isActive) {
                            break
                        } else {
                            val url = node.takeIf { it["method"]?.asText() == "Network.requestWillBeSent" }
                                ?.get("params")?.get("request")?.get("url")?.asText() ?: continue
                            println(pattern.findAll(url).firstOrNull())
                            pattern.findAll(url).firstOrNull()?.groupValues?.let {
                                println(it + "aaaaaaaaaaaaaaaaaa")
                                when (it[2]) {
                                    "m3u8" -> FileList(it[1], url)
                                    "mp4" -> SingleFile(it[1], url)
                                    else -> null
                                }?.let {
                                    withContext(Dispatchers.JavaFx) {
                                        GlobalStatus.medias.add(it)
                                    }
                                }
                            }
                        }
                    }
                    runCatching { ws.close() }
                    pStatus("watch stop")
                }.onFailure { println(it);pStatus("watch stop") }
            }.also { it.invokeOnCompletion { println(it) } }
        }
    }


    private var updateTabListJob: Job? = null
    fun updateTabList() {
        scope.launch {
            pStatus("do updateTabList")
            check(vertxContext == vertx.orCreateContext)
            updateTabListJob = launch {
                runCatching {
                    pStatus("do send http request")
                    val res = httpClient.getAbs(GlobalStatus.devToolsUrl.value).setTimeout(1000).waitForResponse()
                    val tabs = GlobalStatus.tabs
                    val list = res.body().await().toString().decodeJson()
                        .asSequence()
                        .filter { it["type"].asText() == "page" }
                        .map {
                            runCatching {
                                Tab(
                                    it["title"].asText(),
                                    it["url"].asText(),
                                    it["webSocketDebuggerUrl"].asText()
                                )
                            }.getOrNull()
                        }.filterNotNull().toList()
                    withContext(Dispatchers.JavaFx) {
                        pStatus("fill list")
                        ensureActive()
                        tabs.clear()
                        tabs.addAll(list)
                    }
                }.onFailure { if (it is TimeoutException) pStatus("timeout") else throw it }
                    .onSuccess { pStatus("done") }
            }.also { it.invokeOnCompletion { updateTabListJob = null } }
            Unit
        }

    }
}