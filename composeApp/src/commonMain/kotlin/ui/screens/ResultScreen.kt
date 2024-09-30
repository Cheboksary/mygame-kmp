package ui.screens

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.models.PlayerState
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.arrow_back_24
import mygame_kmp.composeapp.generated.resources.arrow_forward_24
import mygame_kmp.composeapp.generated.resources.exit_24
import navigation.ResultScreenComponent
import navigation.ResultTableEvent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource
import ui.elements.BackHandler
import ui.elements.FinalResultsPerRound

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun ResultScreen(component: ResultScreenComponent) {
    val liarWon by component.liarWon.collectAsState()
    val liarName by component.liarName.collectAsState()
    val resultTable by component.resultTable.collectAsState()


    var liarPointsByRound by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var playerPointsByRound by rememberSaveable { mutableStateOf(listOf<Int>()) }
    LaunchedEffect(resultTable) {
        if (resultTable.isNotEmpty()) {
            val liarPoints = mutableListOf<Int>()
            val playerPoints = mutableListOf<Int>()
            resultTable.forEach { setOfPlayers ->
                val uniqueAnswers = mutableSetOf<String>()
                var liarAnswer = ""
                var correctAnswer = ""
                setOfPlayers.forEach {
                    when (it.state) {
                        PlayerState.LEADER -> correctAnswer = it.answers.first()
                        PlayerState.LIAR -> liarAnswer = it.answers.first()
                        else -> uniqueAnswers.add(it.answers.first())
                    }
                }
                if (uniqueAnswers.size == 1) {
                    when (uniqueAnswers.first()) {
                        correctAnswer -> {
                            playerPoints.add(if (playerPoints.isEmpty()) 1 else playerPoints.last() + 1)
                            liarPoints.add(if (liarPoints.isEmpty()) 0 else liarPoints.last())
                        }
                        liarAnswer -> {
                            playerPoints.add(if (playerPoints.isEmpty()) 0 else playerPoints.last())
                            liarPoints.add(if (liarPoints.isEmpty()) 1 else liarPoints.last() + 1)
                        }
                    }
                } else {
                    playerPoints.add(if (playerPoints.isEmpty()) 0 else playerPoints.last())
                    liarPoints.add(if (liarPoints.isEmpty()) 0 else liarPoints.last())
                }
            }
            liarPointsByRound = liarPoints
            playerPointsByRound = playerPoints
        }
    }

    val resultPage = rememberSaveable { mutableStateOf(0) }
    val openBackPressDialog = rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        if (resultPage.value == 0) {
            Spacer(Modifier.height(30.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(10.dp, 32.dp, 10.dp, 12.dp)
            ) {
                Text(
                    if (liarWon) "Врун победил!" else "Добряки победили!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Вруном был:",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    liarName,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(30.dp))
        }
        else
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {
                Crossfade(targetState = resultPage.value) {
                    FinalResultsPerRound(round = it, resultTable = resultTable, liarPointsByRound, playerPointsByRound)
                }
            }
        // Row of bottom navigation buttons and points info between these buttons
        Row(
            horizontalArrangement = if (resultPage.value == 0) Arrangement.End else Arrangement.SpaceBetween,
            modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(10.dp,16.dp)
        ) {
            if (resultPage.value > 0) {
                Button(
                    onClick = { resultPage.value-- }
                ) {
                    Image(vectorResource(Res.drawable.arrow_back_24), "Previous page", modifier = Modifier.padding(ButtonDefaults.IconSpacing))
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row {
                        Text("Добряки",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${playerPointsByRound[resultPage.value -1]}",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row {
                        Text("Врун",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${liarPointsByRound[resultPage.value -1]}",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (resultTable.isEmpty())
                CircularProgressIndicator(modifier = Modifier.size(ButtonDefaults.IconSize))
            else if (resultPage.value < resultTable.size)
                Button(
                    onClick = { resultPage.value++ },
                    enabled = playerPointsByRound.isNotEmpty()
                ) {
                    if (playerPointsByRound.isEmpty())
                        CircularProgressIndicator(modifier = Modifier.size(ButtonDefaults.IconSize), color = ButtonDefaults.buttonColors().contentColor)
                    else Image(vectorResource(Res.drawable.arrow_forward_24),"Next page", modifier = Modifier.padding(ButtonDefaults.IconSpacing))
                }
            else
                Button(
                    onClick = { component.onEvent(ResultTableEvent.BackToLobby) }
                ) {
                    Image(vectorResource(Res.drawable.exit_24),"Exit to lobby", modifier = Modifier.padding(ButtonDefaults.IconSpacing))
                }
        }
    }

    if (openBackPressDialog.value)
        BasicAlertDialog(
            onDismissRequest = {
                openBackPressDialog.value = false
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
                        text = "Выйти в лобби?"
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(modifier = Modifier.wrapContentSize().align(Alignment.End)) {
                        TextButton(
                            onClick = {
                                openBackPressDialog.value = false
                            }
                        ) {
                            Text("Отмена",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        TextButton(
                            onClick = {
                                component.onEvent(ResultTableEvent.BackToLobby)
                            }
                        ) {
                            Text( "Подтвердить")
                        }
                    }
                }
            }
        }

    BackHandler(component.backHandler) {
        if (resultPage.value == 0)
            openBackPressDialog.value = true
        else resultPage.value--
    }
}