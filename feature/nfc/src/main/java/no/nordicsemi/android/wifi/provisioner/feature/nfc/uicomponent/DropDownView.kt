package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme

/**
 * Composable function to show the dropdown view.
 *
 * @param items The list of items to be shown in the dropdown.
 * @param label The label to be shown in the dropdown.
 * @param placeholder The placeholder to be shown in the dropdown.
 * @param defaultSelectedItem The default selected item in the dropdown.
 * @param onItemSelected The callback to be called when an item is selected.
 */
@Composable
internal inline fun <reified T> DropdownView(
    items: List<T>,
    label: String,
    placeholder: String,
    defaultSelectedItem: T? = null,
    crossinline onItemSelected: (T) -> Unit,
) {
    NfcDropdownMenu(
        items = items,
        label = label,
        defaultSelectedItem = defaultSelectedItem,
        placeholder = placeholder,
        onItemSelected = { onItemSelected(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> NfcDropdownMenu(
    items: List<T>,
    label: String,
    defaultSelectedItem: T? = null,
    placeholder: String,
    onItemSelected: (T) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedText by rememberSaveable { mutableStateOf(defaultSelectedItem) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = selectedText?.toString() ?: placeholder,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = placeholder)
                    },
                    label = { Text(text = label) },
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .exposedDropdownSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(item.toString())
                            },
                            onClick = {
                                selectedText = item
                                expanded = false
                                onItemSelected(item)
                            }
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.size(8.dp))
}

@Preview
@Composable
private fun NfcDropDownViewPreview() {
    NordicTheme {
        DropdownView(
            items = listOf("English", "Spanish", "French"),
            label = "Language",
            defaultSelectedItem = "English",
            placeholder = "Select language",
        ) {}
    }
}