package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun NfcTextRow(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = text,
            modifier = Modifier.alpha(0.7f),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview
@Composable
private fun NfcTextRowPreview() {
    NordicTheme {
        NfcTextRow(
            title = "Language",
            text = "en",
        )
    }
}
