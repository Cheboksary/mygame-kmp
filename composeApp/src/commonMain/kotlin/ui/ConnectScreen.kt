package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.arrow_back_24
import navigation.ConnectScreenComponent
import navigation.ConnectScreenEvent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ConnectScreen(component: ConnectScreenComponent) {
    val userName by component.userName.subscribeAsState()
    val lobbyId by component.lobbyId.subscribeAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            component.onEvent(ConnectScreenEvent.ClickButtonBack)
        }) {
            Image(
                imageVector = vectorResource(Res.drawable.arrow_back_24),
                contentDescription = null
            )
        }
        TextField(
            value = userName,
            onValueChange = { changedValue ->
                component.onEvent(ConnectScreenEvent.UpdateUserNameText(changedValue)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            placeholder = { Text("как вас зовут?") }
        )
        OutlinedTextField(
            value = lobbyId,
            onValueChange = { changedValue ->
                component.onEvent(ConnectScreenEvent.UpdateLobbyIdText(changedValue))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            placeholder = { Text("код игры") }
        )
        Button(
            onClick = {
                component.onEvent(ConnectScreenEvent.ClickButtonConnect)
            }
        ) {
            Text("Подключиться")
        }
    }
}