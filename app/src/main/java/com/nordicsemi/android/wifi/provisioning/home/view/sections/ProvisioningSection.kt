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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.components.DataItem
import com.nordicsemi.android.wifi.provisioning.home.view.components.LoadingItem
import com.nordicsemi.android.wifi.provisioning.home.view.toDisplayString
import com.nordicsemi.wifi.provisioner.library.Error
import com.nordicsemi.wifi.provisioner.library.Loading
import com.nordicsemi.wifi.provisioner.library.Resource
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.WifiConnectionStateDomain
import no.nordicsemi.android.common.theme.view.ProgressItem
import no.nordicsemi.android.common.theme.view.ProgressItemStatus

@Composable
internal fun ProvisioningSection(status: Resource<WifiConnectionStateDomain>) {
    val lastStatus = rememberSaveable { mutableStateOf(WifiConnectionStateDomain.DISCONNECTED) }

    when (status) {
        is Error -> ProvisioningSection(WifiConnectionStateDomain.CONNECTION_FAILED, lastStatus.value, status.error.message)
        is Loading -> LoadingItem(modifier = Modifier.padding(vertical = 8.dp))
        is Success -> ProvisioningSection(status.data, lastStatus.value)
    }

    val newLastStatus = when (status) {
        is Error -> WifiConnectionStateDomain.CONNECTION_FAILED
        is Loading -> WifiConnectionStateDomain.DISCONNECTED
        is Success -> status.data
    }

    if (newLastStatus != lastStatus.value && newLastStatus != WifiConnectionStateDomain.CONNECTION_FAILED) {
        lastStatus.value = newLastStatus
    }
}

@Composable
private fun ProvisioningSection(
    status: WifiConnectionStateDomain,
    lastStatus: WifiConnectionStateDomain,
    errorMessage: String? = null
) {
    DataItem(
        iconRes = R.drawable.ic_upload_wifi,
        title = stringResource(id = R.string.provision_status),
        description = status.toDisplayString(),
        isInitiallyExpanded = true
    ) {
        Box(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
            ProgressList(status, lastStatus, errorMessage)
        }
    }
}

@Composable
private fun ProgressList(
    status: WifiConnectionStateDomain,
    lastStatus: WifiConnectionStateDomain,
    errorMessage: String?,
) {
    when (status) {
        WifiConnectionStateDomain.DISCONNECTED -> AuthenticationState()
        WifiConnectionStateDomain.AUTHENTICATION -> AuthenticationState()
        WifiConnectionStateDomain.ASSOCIATION -> AssociationState()
        WifiConnectionStateDomain.OBTAINING_IP -> ObtainingIpState()
        WifiConnectionStateDomain.CONNECTED -> ConnectedState()
        WifiConnectionStateDomain.CONNECTION_FAILED -> FailureState(lastStatus, errorMessage)
    }
}

@Composable
private fun AuthenticationState() = Column {
    ProgressItem(text = stringResource(id = R.string.state_authentication), status = ProgressItemStatus.WORKING)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_association), status = ProgressItemStatus.DISABLED)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_obtaining_ip), status = ProgressItemStatus.DISABLED)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.result_state), status = ProgressItemStatus.DISABLED)
}

@Composable
private fun AssociationState() = Column {
    ProgressItem(text = stringResource(id = R.string.state_authentication), status = ProgressItemStatus.SUCCESS)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_association), status = ProgressItemStatus.WORKING)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_obtaining_ip), status = ProgressItemStatus.DISABLED)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.result_state), status = ProgressItemStatus.DISABLED)
}

@Composable
private fun ObtainingIpState() = Column {
    ProgressItem(text = stringResource(id = R.string.state_authentication), status = ProgressItemStatus.SUCCESS)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_association), status = ProgressItemStatus.SUCCESS)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_obtaining_ip), status = ProgressItemStatus.WORKING)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.result_state), status = ProgressItemStatus.DISABLED)
}

@Composable
private fun ConnectedState() = Column {
    ProgressItem(text = stringResource(id = R.string.state_authentication), status = ProgressItemStatus.SUCCESS)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_association), status = ProgressItemStatus.SUCCESS)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_obtaining_ip), status = ProgressItemStatus.SUCCESS)
    Spacer(modifier = Modifier.size(8.dp))
    ProgressItem(text = stringResource(id = R.string.state_connected), status = ProgressItemStatus.SUCCESS)
}

@Composable
private fun FailureState(
    lastWorkingStatus: WifiConnectionStateDomain,
    errorMessage: String?
) = Column {
    val authenticationResult = if (WifiConnectionStateDomain.AUTHENTICATION < lastWorkingStatus) {
        ProgressItemStatus.SUCCESS
    } else {
        ProgressItemStatus.ERROR
    }
    ProgressItem(text = stringResource(id = R.string.state_authentication), status = authenticationResult)
    Spacer(modifier = Modifier.size(8.dp))

    val associationResult = if (WifiConnectionStateDomain.ASSOCIATION < lastWorkingStatus) {
        ProgressItemStatus.SUCCESS
    } else {
        ProgressItemStatus.ERROR
    }
    ProgressItem(text = stringResource(id = R.string.state_association), status = associationResult)
    Spacer(modifier = Modifier.size(8.dp))

    val obtainingIpResult = if (WifiConnectionStateDomain.OBTAINING_IP < lastWorkingStatus) {
        ProgressItemStatus.SUCCESS
    } else {
        ProgressItemStatus.ERROR
    }
    ProgressItem(text = stringResource(id = R.string.state_obtaining_ip), status = obtainingIpResult)
    Spacer(modifier = Modifier.size(8.dp))

    val message = if (!errorMessage.isNullOrBlank()) {
        errorMessage
    } else {
        stringResource(id = R.string.state_connection_failed)
    }
    ProgressItem(text = message, status = ProgressItemStatus.ERROR)
}
