package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.arrow_back_24
import navigation.CreateScreenComponent
import navigation.CreateScreenEvent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CreateScreen(component: CreateScreenComponent) {
    val userName by component.userName.subscribeAsState()

    var fieldIsEmpty by rememberSaveable { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
    ) {
        Row(
            Modifier.weight(1f).fillMaxSize()
        ) {
            Button(
                onClick = {
                    component.onEvent(CreateScreenEvent.ClickButtonBack)
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
                    component.onEvent(CreateScreenEvent.UpdateUserNameText(changedValue))
                },
                singleLine = true,
                isError = fieldIsEmpty,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions {
                    fieldIsEmpty = userName.isEmpty()
                    localFocusManager.clearFocus()
                },
                placeholder = { Text("как вас зовут?") },
                modifier = Modifier
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Button(
                onClick = {
                    if (userName.isEmpty())
                        fieldIsEmpty = true
                    else {
                        fieldIsEmpty = false
                        component.onEvent(CreateScreenEvent.ClickButtonCreate)
                    }
                }) {
                Text("Создать игру")
            }
        }
    }
}