package extensions

import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun WebSocketSession.notify(message: String) {
    GlobalScope.launch(Dispatchers.IO) {
        send(message)
    }
}