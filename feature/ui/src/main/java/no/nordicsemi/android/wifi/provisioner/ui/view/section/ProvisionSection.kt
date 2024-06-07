package no.nordicsemi.android.wifi.provisioner.ui.view.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme

/**
 * A composable that displays a section with a title and a rationale.
 *
 * @param sectionTitle The title of the section.
 * @param sectionRational The rationale of the section.
 * @param onClick The action to be performed when the section is clicked.
 */
@Composable
fun ProvisionSection(
    sectionTitle: String,
    sectionRational: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(text = sectionTitle)
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = sectionRational,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun ProvisionSectionPreview() {
    NordicTheme {
        ProvisionSection(
            sectionTitle = "Provision over BLE",
            sectionRational = "Provision over BLE rationale.",
            onClick = {}
        )
    }
}