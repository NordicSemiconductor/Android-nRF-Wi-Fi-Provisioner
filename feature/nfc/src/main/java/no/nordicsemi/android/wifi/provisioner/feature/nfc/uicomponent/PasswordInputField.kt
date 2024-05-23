package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Compose view to input password in OutlinedTextField.
 */
@Composable
fun PasswordInputField(
    input: String,
    label: String,
    placeholder: String,
    isError: Boolean = false,
    errorMessage: String = "",
    hint: String = "",
    showPassword: Boolean,
    onShowPassChange : (Boolean) -> Unit = {},
    onUpdate: (String) -> Unit,
) {
    var isShowPassword by remember { mutableStateOf(showPassword) }
    val textColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = if (input.isEmpty()) 0.5f else LocalContentColor.current.alpha
    )
    OutlinedTextField(
        value = input,
        onValueChange = { onUpdate(it) },
        visualTransformation = if (input.isEmpty()) {
            PlaceholderTransformation(placeholder)
        } else {
            if (isShowPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        },
        label = { Text(text = label) },
        placeholder = {
            Text(
                text = placeholder,
            )
        },
        supportingText = {
            Column {
                if (isError) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.alpha(1f)
                        )
                    }
                }
                if (hint.isNotEmpty() && !isError) {
                    Text(
                        text = hint,
                        modifier = Modifier.alpha(0.38f)
                    )
                }
            }
        },
        trailingIcon = {
            IconButton(onClick = {

                isShowPassword = !isShowPassword
                onShowPassChange(isShowPassword)
            }) {
                Icon(
                    imageVector = if (!isShowPassword)
                        Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(textColor),
        isError = isError,
    )
}