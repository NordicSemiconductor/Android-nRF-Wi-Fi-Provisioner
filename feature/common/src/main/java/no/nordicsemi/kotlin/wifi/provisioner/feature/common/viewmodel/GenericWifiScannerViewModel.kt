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

package no.nordicsemi.kotlin.wifi.provisioner.feature.common.viewmodel

import androidx.lifecycle.ViewModel
import no.nordicsemi.android.common.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiAggregator
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiScannerViewEntity
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.NavigateUpEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSortOptionSelected
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiScannerViewEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiSelectedEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiSortOption

abstract class GenericWifiScannerViewModel(
    protected val navigationManager: Navigator,
    protected val wifiAggregator: WifiAggregator
) : ViewModel() {

    protected val _state = MutableStateFlow(WifiScannerViewEntity())
    val state = _state.asStateFlow()

    fun onEvent(event: WifiScannerViewEvent) {
        when (event) {
            NavigateUpEvent -> navigateUp()
            is WifiSelectedEvent -> navigateUp(event.wifiData)
            is OnSortOptionSelected -> onSortOptionSelected(event.sortOption)
        }
    }

    private fun onSortOptionSelected(sortOption: WifiSortOption) {
        _state.value = _state.value.copy(sortOption = sortOption)
    }

    protected abstract fun navigateUp()

    protected abstract fun navigateUp(wifiData: WifiData)
}