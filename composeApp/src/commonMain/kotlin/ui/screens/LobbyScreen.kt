package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import data.models.PlayerState
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.arrow_back_24
import mygame_kmp.composeapp.generated.resources.done_vector
import navigation.LobbyScreenComponent
import navigation.LobbyScreenEvent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource
import ui.elements.BackHandler
import ui.elements.PlayerInLobbyCard

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(component: LobbyScreenComponent) {
    val closeReason by component.serverClosedTheConnectionByReason.collectAsState()
    val onReadyClicked by component.onReadyClicked.subscribeAsState()
    val exceptionMessage by component.exceptionMessage.collectAsState()
    val myself by component.myself.collectAsState()
    val players by component.players.collectAsState()
    val lobbyId by component.lobbyId.collectAsState()

    val gameIsStarted by component.gameIsStarted.collectAsState()
    if (gameIsStarted) component.onEvent(LobbyScreenEvent.PushToGameScreen)

    BackHandler(component.backHandler) {
        component.onEvent(LobbyScreenEvent.ClickButtonBack)
    }

    if (players.isEmpty() && closeReason.isBlank())
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    else
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        component.onEvent(LobbyScreenEvent.ClickButtonBack)
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.arrow_back_24),
                        contentDescription = "Стрелка назад"
                    )
                }
            }
            if (closeReason.isNotBlank()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    Text("Сервер прервал соединение:",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.size(12.dp))
                    Text(closeReason,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(16.dp, 14.dp)
                ) {
                    Text(
                        "КОД ИГРЫ:",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = lobbyId,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier.padding(16.dp, 16.dp)
                ) {
                    for (player in players) {
                        PlayerInLobbyCard(player)
                    }
                    repeat(8 - players.size) {
                        PlayerInLobbyCard()
                    }
                }
                if (myself.state == PlayerState.LEADER) {
                    val openDialogNotReady = remember { mutableStateOf(false) }
                    val openDialogNotEnough = remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            if (players.find { !it.isReady } != null){
                                openDialogNotReady.value = true
                            } else if (players.size < 3) {
                                openDialogNotEnough.value = true
                            } else
                                component.onEvent(LobbyScreenEvent.ClickButtonStart)
                        },
                        modifier = Modifier.padding(16.dp),
                        colors = ButtonColors(
                            containerColor = ButtonDefaults.buttonColors().containerColor,
                            contentColor = ButtonDefaults.buttonColors().containerColor,
                            disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
                            disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
                        )
                    ) {
                        Text(
                            "Начать",
                            color = ButtonDefaults.buttonColors().contentColor
                        )
                    }
                    if (openDialogNotReady.value)
                        BasicAlertDialog(
                            onDismissRequest = {
                                openDialogNotReady.value = false
                            }
                        ) {
                            Surface(
                                modifier = Modifier.wrapContentSize(),
                                shape = MaterialTheme.shapes.medium,
                                tonalElevation = AlertDialogDefaults.TonalElevation
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Не все игроки готовы"
                                    )
                                }
                            }
                        }
                    if (openDialogNotEnough.value)
                        BasicAlertDialog(
                            onDismissRequest = {
                                openDialogNotEnough.value = false
                            }
                        ) {
                            Surface(
                                modifier = Modifier.wrapContentSize(),
                                shape = MaterialTheme.shapes.medium,
                                tonalElevation = AlertDialogDefaults.TonalElevation
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Недостаточно игроков"
                                    )
                                }
                            }
                        }
                    if (exceptionMessage.isNotEmpty())
                        BasicAlertDialog(
                            onDismissRequest = {
                                component.onEvent(LobbyScreenEvent.ClearGameExceptionMessage)
                            }
                        ) {
                            Surface(
                                modifier = Modifier.wrapContentSize(),
                                shape = MaterialTheme.shapes.medium,
                                tonalElevation = AlertDialogDefaults.TonalElevation
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = exceptionMessage
                                    )
                                }
                            }
                        }
                }
                else
                    Button(
                        onClick = {
                            component.onEvent(LobbyScreenEvent.ClickButtonReady)
                        },
                        modifier = Modifier.padding(16.dp),
                        colors = ButtonColors(
                            containerColor = if (myself.isReady) MaterialTheme.colorScheme.tertiaryContainer
                            else ButtonDefaults.buttonColors().containerColor,
                            contentColor = if (myself.isReady) MaterialTheme.colorScheme.tertiaryContainer
                            else ButtonDefaults.buttonColors().containerColor,
                            disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
                            disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
                        )
                    ) {
                        Text(
                            "Готов",
                            color = if (myself.isReady) MaterialTheme.colorScheme.onTertiaryContainer
                            else ButtonDefaults.buttonColors().contentColor
                        )
                        if (onReadyClicked)
                            CircularProgressIndicator(
                                color = if (myself.isReady) MaterialTheme.colorScheme.onTertiaryContainer
                                else ButtonDefaults.buttonColors().contentColor,
                                modifier = Modifier.padding(
                                    ButtonDefaults.IconSpacing,
                                    0.dp,
                                    0.dp,
                                    0.dp
                                ).size(ButtonDefaults.IconSize)
                            )
                        else if (myself.isReady)
                            Image(
                                vectorResource(Res.drawable.done_vector), "игрок готов",
                                modifier = Modifier.padding(
                                    ButtonDefaults.IconSpacing,
                                    0.dp,
                                    0.dp,
                                    0.dp
                                ).size(ButtonDefaults.IconSize)
                            )
                    }
            }
        }
}