/*
 * Copyright (c) 2024, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Compose view to input text in OutlinedTextField.
 */
@Composable
internal fun TextInputField(
    modifier: Modifier = Modifier,
    input: String,
    label: String,
    hint: String = "",
    placeholder: String = "",
    errorMessage: String = "",
    errorState: Boolean = false,
    onUpdate: (String) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = if (input.isEmpty()) 0.5f else LocalContentColor.current.alpha
    )
    OutlinedTextField(
        value = input,
        onValueChange = { onUpdate(it) },
        visualTransformation = if (input.isEmpty())
            PlaceholderTransformation(placeholder)  else VisualTransformation.None,

        modifier = modifier
            .fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = {
            Text(
                text = placeholder,
            )
        },
        supportingText = {
            Column {
                if (errorState) {
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
                if (hint.isNotEmpty() && !errorState) {
                    Text(
                        text = hint,
                        modifier = Modifier.alpha(0.38f)
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(textColor),
        isError = errorState,
    )
}

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    input: TextFieldValue,
    label: String,
    hint: String = "",
    placeholder: String = "",
    errorMessage: String = "",
    errorState: Boolean = false,
    onUpdate: (TextFieldValue) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = if (input.text.isEmpty()) 0.5f else LocalContentColor.current.alpha
    )
    OutlinedTextField(
        value = input,
        onValueChange = { onUpdate(it) },
        visualTransformation = if (input.text.isEmpty())
            PlaceholderTransformation(placeholder) else VisualTransformation.None,

        modifier = modifier
            .fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = {
            Text(
                text = placeholder,
            )
        },
        supportingText = {
            Column {
                if (errorState) {
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
                if (hint.isNotEmpty() && !errorState) {
                    Text(
                        text = hint,
                        modifier = Modifier.alpha(0.38f)
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(textColor),
        isError = errorState,
    )
}

class PlaceholderTransformation(private val placeholder: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return placeholderFilter(placeholder)
    }
}

fun placeholderFilter(placeholder: String): TransformedText {

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return 0
        }

        override fun transformedToOriginal(offset: Int): Int {
            return 0
        }
    }

    return TransformedText(AnnotatedString(placeholder), numberOffsetTranslator)
}

