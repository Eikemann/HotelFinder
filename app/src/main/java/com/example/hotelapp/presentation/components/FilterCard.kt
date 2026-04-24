package com.example.hotelapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp


@Composable
fun FilterCard(
    filterName: String,
    filterIcon : Int,
    onClick: () -> Unit,
    isActive : Boolean = false


){
    val containerColor =
        if (isActive)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainerLow

    val contentColor =
        if (isActive)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurface

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = 8.dp
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
    ){
        Row(
            horizontalArrangement = Arrangement.Start

        ){
            Icon(
                imageVector = ImageVector.vectorResource(filterIcon),
                contentDescription = "",
            )
            Text(
                text = filterName,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 8.dp)

            )
        }
    }

}