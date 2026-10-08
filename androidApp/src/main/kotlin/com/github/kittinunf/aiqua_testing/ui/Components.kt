package com.github.kittinunf.aiqua_testing.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kittinunf.aiqua_testing.R
import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.add_to_cart
import com.github.kittinunf.aiqua_testing.resources.decrease_quantity
import com.github.kittinunf.aiqua_testing.resources.increase_quantity
import com.github.kittinunf.aiqua_testing.resources.remove_from_cart
import com.github.kittinunf.aiqua_testing.ui.theme.stringResource
import dev.icerock.moko.resources.StringResource

/**
 * "+" when the Product isn't in the Cart, otherwise `− n +`.
 * At 1 the − becomes ✕, which removes the Cart Line.
 * It wraps its content unless [modifier] gives it a width, in which case the buttons spread to the edges.
 * [compact] uses 32dp buttons (instead of Material's 48dp touch targets) so it fits a small tile.
 */
@Composable
fun QuantityStepper(
    quantity: Int,
    canAdd: Boolean,
    onAdd: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val buttonModifier = if (compact) Modifier.size(32.dp) else Modifier
    if (quantity == 0) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides if (compact) 0.dp else 48.dp) {
            FilledIconButton(onClick = onAdd, modifier = modifier.then(buttonModifier)) {
                Icon(
                    painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(MR.strings.add_to_cart)
                )
            }
        }
        return
    }
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer,
        LocalMinimumInteractiveComponentSize provides if (compact) 0.dp else 48.dp,
    ) {
        Row(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (quantity == 1) {
                IconButton(onClick = onRemove, modifier = buttonModifier) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(MR.strings.remove_from_cart),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            } else {
                IconButton(onClick = onDecrement, modifier = buttonModifier) {
                    Icon(
                        painterResource(R.drawable.ic_remove),
                        contentDescription = stringResource(MR.strings.decrease_quantity)
                    )
                }
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 16.dp),
            )
            IconButton(onClick = onAdd, enabled = canAdd, modifier = buttonModifier) {
                Icon(
                    painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(MR.strings.increase_quantity)
                )
            }
        }
    }
}

@Composable
fun ProductEmoji(emoji: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = 26.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(resource: StringResource) {
    TopAppBar(title = {
        Text(
            stringResource(resource),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium)
        )
    })
}
