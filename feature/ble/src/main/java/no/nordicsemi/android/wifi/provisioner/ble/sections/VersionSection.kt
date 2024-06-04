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

package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.wifi.provisioner.ble.domain.VersionDomain
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.DataItem
import no.nordicsemi.android.wifi.provisioner.ui.ErrorDataItem
import no.nordicsemi.android.wifi.provisioner.ui.LoadingItem
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error

@Composable
fun VersionSection(version: Resource<VersionDomain>) {
    when (version) {
        is Error -> ErrorSection(version.error)
        is Loading -> LoadingItem()
        is Success -> VersionSection(version = version.data)
    }
}

@Composable
private fun ErrorSection(error: Throwable) {
    ErrorDataItem(
        iconRes = R.drawable.ic_version,
        title = stringResource(id = R.string.dk_version),
        error = error
    )
}

@Composable
private fun VersionSection(version: VersionDomain) {
    DataItem(
        imageVector = Icons.Default.Code,
        title = stringResource(id = R.string.dk_version),
        description = version.value.toString()
    )
}
