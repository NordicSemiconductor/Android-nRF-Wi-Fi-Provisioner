package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.theme.nordicBlue


/** Compose view for the NFC record item in the outlined card.
 * @param headline The headline of the record.
 * @param description The description of the record.
 * @param icon The icon of the record.
 * @param content The content of the record.
 */
@Composable
fun NfcRecordOutlinedCardItem(
    headline: String,
    description: @Composable (RowScope.(TextStyle) -> Unit),
    icon: ImageVector,
    content: @Composable (RowScope.() -> Unit),
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.nordicBlue,
            modifier = Modifier.size(28.dp)
        )

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.alpha(0.7f)
            ) {
                description(MaterialTheme.typography.bodySmall)
            }
        }
        content()
    }

}

@Preview
@Composable
private fun NfcRecordOutlinedCardItemPreview() {
    NordicTheme {
        NfcRecordOutlinedCardItem(
            headline = "URI record",
            description = {
                Text(
                    text = "https://www.nordicsemi.no",
                    style = it,
                )
            },
            icon = Icons.Default.TipsAndUpdates,
        ) {
        }
    }
}
