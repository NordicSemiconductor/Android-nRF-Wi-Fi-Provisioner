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

package com.nordicsemi.android.wifi.provisioning.home.view.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.components.DataItem
import com.nordicsemi.android.wifi.provisioning.home.view.components.ErrorText
import com.nordicsemi.android.wifi.provisioning.home.view.components.LoadingItem
import com.nordicsemi.wifi.provisioner.library.Error
import com.nordicsemi.wifi.provisioner.library.Loading
import com.nordicsemi.wifi.provisioner.library.Resource
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.VersionDomain
import no.nordicsemi.ui.scanner.ui.exhaustive

@Composable
internal fun VersionSection(version: Resource<VersionDomain>) {
    when (version) {
        is Error -> ErrorText(stringResource(id = R.string.error_version))
        is Loading -> LoadingItem()
        is Success -> VersionSection(version = version.data)
    }.exhaustive
}

@Composable
private fun VersionSection(version: VersionDomain) {
    DataItem(
        iconRes = R.drawable.ic_version,
        title = stringResource(id = R.string.dk_version),
        description = version.value
    )
}