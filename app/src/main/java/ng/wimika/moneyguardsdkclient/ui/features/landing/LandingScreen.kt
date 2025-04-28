package ng.wimika.moneyguardsdkclient.ui.features.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import ng.wimika.moneyguardsdkclient.R
import ng.wimika.moneyguardsdkclient.ui.features.landing.FeatureCategory

@Serializable
object Landing

@Composable
fun LandingScreen(
    gotoLoginClick: (() -> Unit)? = null
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null
            )

            Box(
                modifier = Modifier.padding(top = 16.dp)
            )

            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCategory(
                        modifier = Modifier.weight(1f),
                        title = "Login",
                        icon = Icons.Default.AccountCircle,
                        onClick = { gotoLoginClick?.invoke() }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LandingScreenPreview() {
    MaterialTheme {
        LandingScreen()
    }
}