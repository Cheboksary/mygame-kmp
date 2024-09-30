package ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.models.Player
import data.models.PlayerState
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.crown_vector
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FinalResultsPerRound(
    round: Int,
    resultTable: List<Set<Player>>,
    liarPointsByRound: List<Int>,
    playerPointsByRound: List<Int>
) {
    println("round:$round\nresultTable:$resultTable\nliarPointsByRound:$liarPointsByRound\nplayerPointsByRound:$playerPointsByRound")
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentHeight().padding(0.dp,8.dp)
        ) {
            Text("Итоги", fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Раунд $round", fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        for (player in resultTable[round - 1]) {
            var surfaceColor by rememberSaveable { mutableStateOf<Boolean?>(null) }
            if (player.state == PlayerState.LIAR || player.answers.first() == resultTable[round - 1].first { it.state == PlayerState.LIAR }.answers.first() &&
                player.answers.first() != resultTable[round - 1].first().answers.first())
                surfaceColor = false
            else if (player.state == PlayerState.LEADER || player.answers.first() == resultTable[round - 1].first().answers.first())
                surfaceColor = true
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 2.dp,
                color = when (surfaceColor) {
                    true -> MaterialTheme.colorScheme.surface
                    false -> MaterialTheme.colorScheme.errorContainer
                    null -> MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.padding(6.dp, 2.dp).wrapContentHeight().fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(10f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (player == resultTable[round - 1].first())
                                Image(
                                    vectorResource(Res.drawable.crown_vector),
                                    "leader's crown",
                                    modifier = Modifier.padding(4.dp).size(38.dp)
                                )
                            Text(
                                text = player.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = player.answers.first(),
                            color = when (surfaceColor) {
                                true -> MaterialTheme.colorScheme.onSurface
                                false -> MaterialTheme.colorScheme.onErrorContainer
                                null -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        modifier = Modifier.padding(0.dp, 4.dp, 4.dp, 4.dp).wrapContentWidth()
                            .weight(1f),
                        text =
                        if (player.state == PlayerState.LIAR)
                            if (round == 1 && liarPointsByRound.first() == 1 || (round > 1 && liarPointsByRound[round - 1] > liarPointsByRound[round - 2]))
                                "+1"
                            else ""
                        else
                            if (round == 1 && playerPointsByRound.first() == 1 || (round > 1 && playerPointsByRound[round - 1] > playerPointsByRound[round - 2]))
                                "+1"
                            else "",
                        color = when (surfaceColor) {
                            true -> MaterialTheme.colorScheme.onSurface
                            false -> MaterialTheme.colorScheme.onErrorContainer
                            null -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}