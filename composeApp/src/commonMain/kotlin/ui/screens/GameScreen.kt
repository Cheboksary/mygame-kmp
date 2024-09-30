package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import data.models.PlayerState
import kotlinx.coroutines.delay
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.arrow_back_24
import mygame_kmp.composeapp.generated.resources.baseline_refresh_24
import navigation.GameScreenComponent
import navigation.GameScreenEvent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource
import ui.elements.BackHandler

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameScreen(component: GameScreenComponent) {
    val closeReason by component.serverClosedTheConnectionByReason.collectAsState()
    val question by component.question.collectAsState()
    val myself by component.myself.collectAsState()
    val leaderIsDone by component.leaderIsDone.collectAsState()
    val refreshing by component.refreshing.collectAsState()
    val isDone by component.answered.collectAsState()

    val liarName by component.liarName.collectAsState()
    if (liarName.isNotBlank())
        component.onEvent(GameScreenEvent.PushToResultScreen)

    val timerInitialValue = 62
    var timer by rememberSaveable { mutableStateOf(60)}

    LaunchedEffect(leaderIsDone) {
        if (leaderIsDone) return@LaunchedEffect
        timer = timerInitialValue + 1
        while (timer > 0) {
            timer -= 1
            delay(1000)
        }
        if (myself.state == PlayerState.LEADER)
            component.onEvent(GameScreenEvent.OnLeaderDoneClicked)
    }
    val answer by component.answer.subscribeAsState()
    val openBackPressedDialog = rememberSaveable { mutableStateOf(false) }

    if (closeReason.isNotBlank())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        component.onEvent(GameScreenEvent.OnBackPressed)
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.arrow_back_24),
                        contentDescription = "Стрелка назад"
                    )
                }
            }
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
            BackHandler(backHandler = component.backHandler) {
                component.onEvent(GameScreenEvent.OnBackPressed)
            }
        }
    else
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            when (myself.state) {
                PlayerState.LEADER -> {
                    LeaderView(
                        leaderIsDone,
                        question,
                        refreshing,
                        timer,
                        onRefreshClicked = {
                            component.onEvent(GameScreenEvent.OnRefreshClicked)
                        },
                        onDoneClicked = {
                            component.onEvent(GameScreenEvent.OnLeaderDoneClicked)
                        }
                    )
                }

                PlayerState.PLAYER -> {
                    PlayerView(
                        leaderIsDone,
                        timer,
                        answer,
                        isDone,
                        onValueChanged = { newValue ->
                            component.onEvent(GameScreenEvent.UpdateAnswerField(newValue))
                        },
                        onDoneClicked = {
                            component.onEvent(GameScreenEvent.OnPlayerDoneClicked)
                        }
                    )
                }

                else -> {
                    LiarView(
                        question,
                        leaderIsDone,
                        timer,
                        answer,
                        isDone,
                        onValueChanged = { newValue ->
                            component.onEvent(GameScreenEvent.UpdateAnswerField(newValue))
                        },
                        onDoneClicked = {
                            component.onEvent(GameScreenEvent.OnPlayerDoneClicked)
                        }
                    )
                }
            }
            if (openBackPressedDialog.value)
                AlertDialog(
                    onDismissRequest = {
                        openBackPressedDialog.value = false
                    },
                    title = { Text("Покинуть лобби?") },
                    text = { Text("Вы не сможете переподключиться к начатой игре") },
                    confirmButton = {
                        TextButton(onClick = {
                            openBackPressedDialog.value = false
                            component.onEvent(GameScreenEvent.OnBackPressed)
                        }) {
                            Text("Подтвердить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            openBackPressedDialog.value = false
                        }) {
                            Text("Отмена")
                        }
                    },
                    modifier = Modifier.padding(6.dp)
                )
            BackHandler(backHandler = component.backHandler) {
                openBackPressedDialog.value = true
            }
        }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LeaderView(
    leaderIsDone: Boolean,
    question: String,
    refreshing: Boolean,
    timer: Int,
    onRefreshClicked: () -> Unit,
    onDoneClicked: () -> Unit
) {
    if (!leaderIsDone) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                "Именно так должны ответить все добряки:",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                question,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            if (timer > 35)
                if (refreshing)
                    CircularProgressIndicator()
                else
                    Button(onClick = {
                        onRefreshClicked()
                    }) {
                        Image(
                            vectorResource(Res.drawable.baseline_refresh_24),
                            contentDescription = "refresh question"
                        )
                    }
            Spacer(Modifier.height(32.dp))
            Text(
                text = if (timer >= 70) "1:${timer % 60}" else if (timer >= 60) "1:0${timer % 60}" else if (timer >= 10) "0:$timer" else "0:0$timer",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 34.sp
            )
        }
        Button(onClick = {
            onDoneClicked()
        }, enabled = timer > 0) {
            Text("Досрочный ответ",
                fontSize = 26.sp
            )
        }
    } else {
        Text("Не подсказывайте!\nЖдем ответы игроков...",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PlayerView(
    leaderIsDone: Boolean,
    timer: Int,
    answer: String,
    isDone: Boolean,
    onValueChanged: (String) -> Unit,
    onDoneClicked: () -> Unit
) {
    var lastAnswer by rememberSaveable { mutableStateOf("") }
    if (!leaderIsDone) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                "Вы добряк",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Ведущий говорит",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "кое-что",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Обсудим это через",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(26.dp))
            Text(
                text = if (timer >= 70) "1:${timer % 60}" else if (timer >= 60) "1:0${timer % 60}" else if (timer >= 10) "0:$timer" else "0:0$timer",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else
        if (!isDone) {
            val localFocusManager = LocalFocusManager.current
            var fieldIsEmpty by rememberSaveable { mutableStateOf(false) }
            Text("Обсудите с игроками\nслова ведущего\nПостарайтесь дать\nверный ответ",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = answer,
                onValueChange = {
                    onValueChanged(it)
                },
                singleLine = true,
                isError = fieldIsEmpty,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions {
                    fieldIsEmpty = answer.isEmpty()
                    localFocusManager.clearFocus()
                }
            )
            Button(onClick = {
                if (answer.isEmpty())
                    fieldIsEmpty = answer.isEmpty()
                else {
                    lastAnswer = answer
                    onDoneClicked()
                }
            }) {
                Text("Зафиксировать ответ",
                    fontSize = 26.sp
                )
            }
        } else {
            Text("Ваш ответ:",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Text(lastAnswer,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text("Ждем других добряков...",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
}

@Composable
fun LiarView(
    question: String,
    leaderIsDone: Boolean,
    timer: Int,
    answer: String,
    isDone: Boolean,
    onValueChanged: (String) -> Unit,
    onDoneClicked: () -> Unit
) {
    var lastAnswer by rememberSaveable { mutableStateOf("") }
    if (!leaderIsDone) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                "Вы врун",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Ведущий говорит",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                question,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Обсудим это через",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(26.dp))
            Text(
                text = if (timer >= 70) "1:${timer % 60}" else if (timer >= 60) "1:0${timer % 60}" else if (timer >= 10) "0:$timer" else "0:0$timer",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else {
        if (!isDone) {
            var fieldIsEmpty by rememberSaveable { mutableStateOf(false) }
            val localFocusManager = LocalFocusManager.current
            Text("Постарайтесь убедить\nигроков дать ваш\n'верный' ответ, а не\n$question",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = answer,
                onValueChange = {
                    onValueChanged(it)
                },
                singleLine = true,
                isError = fieldIsEmpty,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions {
                    fieldIsEmpty = answer.isEmpty()
                    localFocusManager.clearFocus()
                }
            )
            Button(onClick = {
                if (answer.isEmpty())
                    fieldIsEmpty = answer.isEmpty()
                else {
                    lastAnswer = answer
                    onDoneClicked()
                }
            }) {
                Text("Зафиксировать ответ",
                    fontSize = 26.sp
                )
            }
        } else {
            Text("Ваш ответ:",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Text(lastAnswer,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text("Ждем других добряков...",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}