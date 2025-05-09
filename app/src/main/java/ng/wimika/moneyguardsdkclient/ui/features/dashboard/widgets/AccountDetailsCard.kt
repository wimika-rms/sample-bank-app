package ng.wimika.moneyguardsdkclient.ui.features.dashboard.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun AccountDetailsCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF232323)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {0
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Basic",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "14042332459",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB0B0B0),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Home, tint = Color.DarkGray, contentDescription = null)
                    Text(
                        text = "Wimika Demo Bank",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(end = 32.dp)
            ) {
                Text(
                    text = "Available Balance",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB0B0B0)
                )
                Text(
                    text = "₦ 100,062.37",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }


//            Row(
//                modifier = Modifier
//                    .align(Alignment.BottomStart)
//                    .padding(start = 20.dp, bottom = 20.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                IconButton(onClick = { /* TODO: Show/hide balance */ }) {
//                    Icon(
//                        imageVector = Icons.Default.Visibility,
//                        contentDescription = "Show Balance",
//                        tint = androidx.compose.ui.graphics.Color.White
//                    )
//                }
//                androidx.compose.material3.IconButton(onClick = { /* TODO: Share account */ }) {
//                    androidx.compose.material3.Icon(
//                        imageVector = androidx.compose.material.icons.Icons.Default.Share,
//                        contentDescription = "Share Account",
//                        tint = androidx.compose.ui.graphics.Color.White
//                    )
//                }
//            }
        }
    }
}


@Preview
@Composable
private fun AccountDetailsCardPreview() {
    MaterialTheme {
        AccountDetailsCard()
    }
}