package data.client

import data.models.IncomingMsgType
import data.models.OutgoingMsgType
import data.models.Player
import data.models.PlayerState
import data.models.WSOutgoingMessage
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.websocket.WebSocketException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameStatesRepository(private val coroutineScope: CoroutineScope) {

    private val wsClient = KtorWebSocketClient
    private var closedByUser = false

    private val _serverClosedTheConnectionByReason = MutableStateFlow("")
    val serverClosedTheConnectionByReason = _serverClosedTheConnectionByReason.asStateFlow()

    private val _gameExceptionMessage = MutableStateFlow("")
    val gameExceptionMessage = _gameExceptionMessage.asStateFlow()

    private val _myself = MutableStateFlow(Player("",-1, PlayerState.PLAYER))
    val myself = _myself.asStateFlow()

    private val _playersList: MutableStateFlow<List<Player>> = MutableStateFlow(listOf())
    val playersList = _playersList.asStateFlow()

    private val _lobbyId: MutableStateFlow<String> = MutableStateFlow("")
    val lobbyId: StateFlow<String> = _lobbyId.asStateFlow()

    private val _gameIsStarted = MutableStateFlow(false)
    val gameIsStarted = _gameIsStarted.asStateFlow()

    private val _question: MutableStateFlow<String> = MutableStateFlow("")
    val question = _question.asStateFlow()

    private val _refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    private val _leaderIsDone: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val leaderIsDone = _leaderIsDone.asStateFlow()

    private val _answered: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val answered = _answered.asStateFlow()

    private val _liarName: MutableStateFlow<String> = MutableStateFlow("")
    val liarName = _liarName.asStateFlow()

    private val _liarWon: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val liarWon = _liarWon.asStateFlow()

    private val _resultTable: MutableStateFlow<List<Set<Player>>> = MutableStateFlow(listOf(setOf()))
    val resultTable = _resultTable.asStateFlow()

    fun connect(lobbyId: String?, playerName: String) {
        closedByUser = false
        clearStates()
        _serverClosedTheConnectionByReason.update { "" }
        if (lobbyId == null)
            _myself.update { Player(playerName,_myself.value.id,PlayerState.LEADER) }
        else
            _myself.update { Player(playerName,_myself.value.id,PlayerState.PLAYER) }
        wsClient.getIncomingMessages(lobbyId, playerName).onEach { incomingData ->
            when (incomingData.type) {
                IncomingMsgType.LOBBY_STATE -> {
                    if (_myself.value.id < 0)
                        _myself.update { Player(_myself.value.name,incomingData.playersList!!.last().id,_myself.value.state) }
                    _playersList.update { incomingData.playersList!! }
                    if (incomingData.lobbyId.isNotBlank()) {
                        _lobbyId.update { incomingData.lobbyId }
                    }
                }

                IncomingMsgType.EXCEPTION -> _gameExceptionMessage.update { incomingData.msg }

                IncomingMsgType.GAME_START -> {
                    _question.update { "" }
                    _leaderIsDone.update { false }
                    _resultTable.update { listOf() }
                    _liarWon.update { false }
                    _liarName.update {""}
                    _answered.update { false }
                    _gameIsStarted.update { true }
                }

                IncomingMsgType.YOUR_LIAR -> _myself.update { Player(_myself.value.name,_myself.value.id,PlayerState.LIAR) }

                IncomingMsgType.QUESTION -> {
                    _refreshing.update { false }
                    _question.update { incomingData.msg }
                }

                IncomingMsgType.LEADER_DONE -> _leaderIsDone.update { true }

                IncomingMsgType.READY -> {
                    markReadyOrNot(incomingData.player!!.id,true)
                    if (incomingData.player.id == _myself.value.id)
                        _myself.update { Player(_myself.value.name,_myself.value.id,_myself.value.state, isReady = true) }
                    //_myself.update { _myself.value.copy(isReady = true) }
                }

                IncomingMsgType.NOT_READY -> {
                    markReadyOrNot(incomingData.player!!.id,false)
                    if (incomingData.player.id == _myself.value.id)
                        _myself.update { Player(_myself.value.name,_myself.value.id,_myself.value.state, isReady = false) }
                }

                IncomingMsgType.ANSWER_ACCEPTED -> {}

                IncomingMsgType.NEXT_ROUND -> {
                    _leaderIsDone.update { false }
                    _answered.update { false }
                }

                IncomingMsgType.GAME_OVER -> {
                    _playersList.update { incomingData.playersList!! }
                    _resultTable.update { incomingData.resultTable!! }
                    _gameIsStarted.update { false }
                    if (_myself.value.state != PlayerState.LEADER)
                        _myself.update { Player(_myself.value.name,_myself.value.id, state = PlayerState.PLAYER, isReady = false) }
                }

                IncomingMsgType.LIAR_WON -> {
                    _liarWon.update { true }
                    _liarName.update { incomingData.player!!.name }
                }
                IncomingMsgType.PLAYERS_WON -> {
                    _liarName.update { incomingData.player!!.name }
                }
                IncomingMsgType.GAME_OVER_BY_VOTE -> TODO()
            }
        }.catch { e ->
            if (!closedByUser)
                when (e) {
                    is ClientExceptions.SessionClosedException -> {
                        _serverClosedTheConnectionByReason.update {
                            if (e.reason?.message?.isNotEmpty() == true)
                                e.reason.message
                            else {
                                e.reason.toString()
                            }
                        }
                        clearStates()
                        println("in repo. session closed exception caught")
                    }

                    is ConnectTimeoutException -> {
                        println("in repo. connectTimeout cancellation: ${e.stackTraceToString()}")
                        _serverClosedTheConnectionByReason.update { "connect timeout" }
                    }

                    is SocketTimeoutException -> {
                        _serverClosedTheConnectionByReason.update { "socket timeout exception" }
                    }

                    is WebSocketException -> {
                        _serverClosedTheConnectionByReason.update { "сервер не отвечает (websocket exception)" }
                    }

                    is TimeoutCancellationException -> {
                        println("in repo. timeout cancellation: ${e.stackTraceToString()}")
                        _serverClosedTheConnectionByReason.update { "timeout cancellation" }
                    }
                    /*is CancellationException -> {
                        unableToReadCloseReason = false
                        println("in repo. CancellationException: ${e.stackTraceToString()}")
                    }
                    is ClosedReceiveChannelException -> {
                        unableToReadCloseReason = false
                        println("in repo. closedReceiveChannel : ${e.message}")
                    }*/
                    else -> _serverClosedTheConnectionByReason.update { "Unable to read close reason ${e.stackTraceToString()}" }
                }
        }.onCompletion { e ->
            if (!closedByUser)
                when (e) {
                    is ClientExceptions.SessionClosedException -> {
                        _serverClosedTheConnectionByReason.update {
                            if (e.reason?.message?.isNotEmpty() == true)
                                e.reason.message
                            else {
                                e.reason.toString()
                            }
                        }
                        clearStates()
                        println("in repo fin. session closed exception caught")
                    }
                    /*is CancellationException -> {
                        unableToReadCloseReason = false
                        println("in repo fin. CancellationException: ${e.stackTraceToString()}")
                    }
                    is ClosedReceiveChannelException -> {
                        unableToReadCloseReason = false
                        println("in repo fin. closedReceiveChannel : ${e.message}")
                    }*/
                    else ->
                        if (_serverClosedTheConnectionByReason.value.isBlank())
                            _serverClosedTheConnectionByReason.update { "Unable to read close reason" }
                }
            println("in repo fin. session is over in finally ${e?.stackTraceToString() ?: "no throwable"}")
        }.launchIn(coroutineScope)
    }

    fun clearExceptionMessage() {
        _gameExceptionMessage.update { "" }
    }

    fun start() {
        CoroutineScope(coroutineScope.coroutineContext).launch {
            sendMessage(message = WSOutgoingMessage(OutgoingMsgType.GAME_START))
        }
    }

    fun close() {
        closedByUser = true
        CoroutineScope(coroutineScope.coroutineContext).launch {
            wsClient.close()
        }
        clearStates()
    }

    fun leaderDone() {
        _leaderIsDone.update { true }
        CoroutineScope(coroutineScope.coroutineContext).launch {
            sendMessage(message = WSOutgoingMessage(OutgoingMsgType.LEADER_DONE))
        }
    }

    fun refreshQuestion() {
        _refreshing.update { true }
        CoroutineScope(coroutineScope.coroutineContext).launch {
            sendMessage(message = WSOutgoingMessage(OutgoingMsgType.REFRESH_QUESTION))
        }
    }

    fun answer(answer: String) {
        _answered.update { true }
        CoroutineScope(coroutineScope.coroutineContext).launch {
            sendMessage(message = WSOutgoingMessage(OutgoingMsgType.ANSWER, msg = answer))
        }
    }

    suspend fun notReady() {
        sendMessage(message = WSOutgoingMessage(OutgoingMsgType.NOT_READY))
    }

    suspend fun ready() {
        sendMessage(message = WSOutgoingMessage(OutgoingMsgType.READY))
    }

    private suspend fun sendMessage(message: WSOutgoingMessage) {
        wsClient.sendMessage(message)
    }

    private fun markReadyOrNot(playerId: Int, isReady: Boolean) {
        val old = _playersList.value.find { it.id == playerId } ?: return
        val new = old.copy(isReady = isReady)
        val newList = _playersList.value.toMutableList()
        newList[newList.indexOf(old)] = new
        _playersList.update { newList.toList() }
    }

    private fun clearStates() {
        _myself.update { Player("",-1, PlayerState.PLAYER) }
        _playersList.update { listOf() }
        _lobbyId.update { "" }
        _question.update { "" }
        _refreshing.update { false }
        _leaderIsDone.update { false }
        _answered.update { false }
        _gameIsStarted.update { false }
        _liarName.update { "" }
        _liarWon.update { false }
        _resultTable.update { listOf(setOf()) }
    }
}