package com.nordicsemi.android.wifi.provisioning.home.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun LoadingItem() {
    Row(modifier = Modifier
        .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline)
                .applyPlaceholder()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.size(2.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outline)
                    .fillMaxWidth()
                    .height(14.dp)
                    .applyPlaceholder()
            )

            Spacer(modifier = Modifier.size(8.dp))

            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outline)
                    .fillMaxWidth()
                    .height(14.dp)
                    .applyPlaceholder()
            )

            Spacer(modifier = Modifier.size(2.dp))
        }
    }
}
