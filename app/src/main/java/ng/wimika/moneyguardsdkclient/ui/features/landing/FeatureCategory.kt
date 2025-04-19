package ng.wimika.moneyguardsdkclient.ui.features.landing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun FeatureCategory(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(
            onClick = onClick,
            role = Role.Button
        ),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(32.dp)
        ) {
            Text(title)
        }
    }
}


@Preview
@Composable
private fun FeatureCategoryPreview() {
    MaterialTheme {
        FeatureCategory(title = "Login", onClick = {})
    }
}