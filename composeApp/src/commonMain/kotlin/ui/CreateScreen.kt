package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateScreen() {
    Column {
        Button(onClick = {
            TODO()
        }) {
        }
        OutlinedTextField(
            value = "",
            onValueChange = {},
        )
        Button(onClick = {
            TODO()
        }){
            Text("Создать игру")
        }
    }
}