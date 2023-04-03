import handlers.*
import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.ofType
import io.reactivex.rxjava3.kotlin.withLatestFrom
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.*
import models.Command
import models.Event
import models.Game
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.optionals.getOrNull

fun main() {
    val lastID = AtomicInteger(0)
    val commandsSubject = PublishSubject.create<Triple<Int, DefaultWebSocketSession, Frame>>()
    val eventsSubject = PublishSubject.create<Event>()
    val gameSubject = BehaviorSubject.createDefault<Optional<Game>>(Optional.empty())

    // update zombie's position
    gameSubject
        .observeOn(Schedulers.computation())
        .map { g -> g.getOrNull()?.id ?: 0 }
        .distinctUntilChanged()
        .switchMap {
            // id == 0 only when game is null
            if (it == 0) {
                Observable.empty()
            } else {
                // update every 2 seconds
                Observable.interval(2, TimeUnit.SECONDS)
                    .withLatestFrom(gameSubject)
                    .map { (_, game) -> game }
            }
        }
        .subscribe {
            // send move event
            eventsSubject.onNext(Event.Move)
        }

    // merge events from user and internal events
    val events =
        Observable.merge(
            // map user commands to events
            commandsSubject
                .observeOn(Schedulers.io())
                // print errors to console
                .doOnError(::println)
                .observeOn(Schedulers.computation())
                .ofType<Triple<Int, DefaultWebSocketSession, Frame.Text>>()
                .map { (pid, session, frame) -> Triple(pid, session, Command.parse(frame.readText())) }
                .map(Event::commandToEvent),
            eventsSubject,
        )
            .observeOn(Schedulers.computation())
            .publish()
            .autoConnect()

    // start looping over the events
    events
        .serialize()
        .map(::eventToAction)
        .map(
            createActionHandler(
                Object(),
                { gameSubject.value?.getOrNull() },
                { gameSubject.onNext(Optional.ofNullable(it)) },
            )
        )
        .filter(Optional<GameNotifier>::isPresent)
        .map(Optional<GameNotifier>::get)
        .observeOn(Schedulers.io())
        .subscribe { it(eventsSubject::onNext) }

    // print game updates to console
    gameSubject
        .distinctUntilChanged()
        .observeOn(Schedulers.io())
        .subscribe(::println)

    // print events to console
    events
        .observeOn(Schedulers.io())
        .subscribe(::println)

    embeddedServer(Netty, port = 8080) {
        install(StatusPages)
        install(DefaultHeaders)
        install(WebSockets)

        routing {
            webSocket("/game") {
                // player ID
                val pid = lastID.getAndIncrement()

                for (frame in incoming) {
                    commandsSubject.onNext(
                        Triple(
                            pid,
                            this,
                            frame,
                        ),
                    )
                }
            }
        }
    }.start(wait = true)

    gameSubject.onComplete()
    commandsSubject.onComplete()
    eventsSubject.onComplete()
}