package ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import navigation.StartScreenComponent
import navigation.StartScreenEvent

@Composable
fun StartScreen(component: StartScreenComponent) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            component.onEvent(StartScreenEvent.ClickButtonCreate)
        }) {
            Text("Создать игру")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            component.onEvent(StartScreenEvent.ClickButtonConnect)
        }) {
            Text("Подключиться по коду")
        }
    }
}