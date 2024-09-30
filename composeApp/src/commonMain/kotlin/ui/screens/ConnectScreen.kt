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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.arrow_back_24
import navigation.ConnectScreenComponent
import navigation.ConnectScreenEvent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ConnectScreen(component: ConnectScreenComponent) {
    val userName by component.userName.subscribeAsState()
    val lobbyId by component.lobbyId.subscribeAsState()
    var nameFieldIsEmpty by rememberSaveable { mutableStateOf(false) }
    var lobbyIdFieldIsEmpty by rememberSaveable { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Button(
                onClick = {
                    component.onEvent(ConnectScreenEvent.ClickButtonBack)
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
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = userName,
                onValueChange = { changedValue ->
                    component.onEvent(ConnectScreenEvent.UpdateUserNameText(changedValue))
                },
                singleLine = true,
                isError = nameFieldIsEmpty,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions {
                    nameFieldIsEmpty = userName.isEmpty()
                    localFocusManager.moveFocus(FocusDirection.Down)
                },
                placeholder = { Text("как вас зовут?") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = lobbyId,
                onValueChange = { changedValue ->
                    component.onEvent(ConnectScreenEvent.UpdateLobbyIdText(changedValue))
                },
                singleLine = true,
                isError = lobbyIdFieldIsEmpty,
                keyboardActions = KeyboardActions {
                    lobbyIdFieldIsEmpty = lobbyId.isEmpty()
                    localFocusManager.clearFocus()
                },
                placeholder = { Text("код игры") }
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Button(
                onClick = {
                    if (userName.isEmpty() || lobbyId.isEmpty()) {
                        nameFieldIsEmpty = userName.isEmpty()
                        lobbyIdFieldIsEmpty = lobbyId.isEmpty()
                    }
                    else
                        component.onEvent(ConnectScreenEvent.ClickButtonConnect)
                }
            ) {
                Text("Подключиться")
            }
        }
    }
}