/*
 * Copyright (c) 2022, Nordic Semiconductor
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

package com.nordicsemi.android.wifi.provisioning.home.view

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.R

@Composable
private fun DataItem(
    @DrawableRes
    iconRes: Int,
    title: String,
    description: String
) {
    DataItem(iconRes = iconRes, title = title, description = description, isExpanded = null)
}

@Composable
fun DataItem(
    @DrawableRes
    iconRes: Int,
    title: String,
    description: String,
    expandableContent: @Composable () -> Unit
) {
    val isExpanded = remember { mutableStateOf(false) }

    Column {
        DataItem(iconRes = iconRes, title = title, description = description, isExpanded = isExpanded.value)

        AnimatedVisibility(
            visible = isExpanded.value,
            enter = expandIn(expandFrom = Alignment.Center, initialSize = {
                IntSize(it.width, 0)
            }),
            exit = shrinkOut(shrinkTowards = Alignment.Center, targetSize = {
                IntSize(it.width, 0)
            })
        ) {
            expandableContent()
        }
    }
}

@Composable
private fun DataItem(
    @DrawableRes
    iconRes: Int,
    title: String,
    description: String,
    isExpanded: Boolean?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(id = R.string.cd_data_item_icon)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title)

            Spacer(modifier = Modifier.size(4.dp))

            Text(text = description)
        }

        isExpanded?.let { ExpandedIcon(isExpanded = it) }
    }
}

@Composable
private fun ExpandedIcon(isExpanded: Boolean) {
    val icon = if (isExpanded) {
        Icons.Default.ArrowDropUp
    } else {
        Icons.Default.ArrowDropDown
    }
    Icon(imageVector = icon, contentDescription = "")
}

@Preview
@Composable
private fun DataItemPreview() {
    DataItem(iconRes = R.drawable.ic_version, title = "Title", description = "Description")
}

@Preview
@Composable
private fun DataItemExpandedPreview() {
    DataItem(iconRes = R.drawable.ic_version, title = "Title", description = "Description") { }
}
