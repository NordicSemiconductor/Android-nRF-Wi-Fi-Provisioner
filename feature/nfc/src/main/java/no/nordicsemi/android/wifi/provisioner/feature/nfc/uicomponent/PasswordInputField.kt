package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Compose view to input password in OutlinedTextField.
 */
@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    input: String,
    label: String,
    placeholder: String,
    error: String? = null,
    hint: String? = null,
    showPassword: Boolean,
    onShowPassChange: (Boolean) -> Unit = {},
    onUpdate: (String) -> Unit,
) {
    var isShowPassword by remember { mutableStateOf(showPassword) }
    val textColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = if (input.isEmpty()) 0.5f else LocalContentColor.current.alpha
    )
    val visibilityIcon =
        if (isShowPassword) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
    OutlinedTextField(
        modifier = modifier,
        value = input,
        onValueChange = { onUpdate(it) },
        visualTransformation = when {
            input.isEmpty() -> PlaceholderTransformation(placeholder)
            isShowPassword -> VisualTransformation.None
            else -> PasswordVisualTransformation()
        },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password
        ),
        supportingText = {
            error?.let { errorMessage ->
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
            } ?: run {
                if (hint != null) {
                    Text(
                        text = hint,
                        modifier = Modifier.alpha(0.38f)
                    )
                }
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    isShowPassword = !isShowPassword
                    onShowPassChange(isShowPassword)
                }
            ) {
                Icon(
                    imageVector = visibilityIcon,
                    contentDescription = null
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(textColor),
        isError = error != null,
    )
}