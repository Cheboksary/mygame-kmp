package ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.models.Player
import data.models.PlayerState
import mygame_kmp.composeapp.generated.resources.Res
import mygame_kmp.composeapp.generated.resources.crown_vector
import mygame_kmp.composeapp.generated.resources.done_vector
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PlayerInLobbyCard(player: Player? = null) {
    Row(
        Modifier.fillMaxWidth()
            .size(56.dp)
            .padding(8.dp, 4.dp)
            .clip(
                RoundedCornerShape(CornerSize(8.dp))
            )
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (player == null) {
            Text(
                text = "",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
            )
        } else {
            Box(
                modifier = Modifier.size(40.dp).padding(4.dp, 0.dp),
                contentAlignment = Alignment.Center
            ) {
                if (player.state == PlayerState.LEADER) {
                    Image(vectorResource(Res.drawable.crown_vector), "статус игрока")
                } else if (player.isReady) {
                    Image(vectorResource(Res.drawable.done_vector), "игрок готов")
                }
            }
            Text(
                text = player.name,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
            )
        }
    }
}