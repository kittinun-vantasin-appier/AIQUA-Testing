package com.github.kittinunf.aiqua_testing.ui.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kittinunf.aiqua_testing.inbox.InboxMessage
import com.github.kittinunf.aiqua_testing.inbox.InboxUiState
import com.github.kittinunf.aiqua_testing.inbox.InboxViewModel
import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.inbox_empty
import com.github.kittinunf.aiqua_testing.resources.inbox_title
import com.github.kittinunf.aiqua_testing.ui.theme.stringResource

/** The inbox as a bottom sheet over Home. Its content is only composed while it's open, so each opening is a view. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxSheet(viewModel: InboxViewModel, onDismiss: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onScreenViewed() }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        InboxContent(state, onMessageClick = viewModel::onMessageClick)
    }
}

@Composable
private fun InboxContent(state: InboxUiState, onMessageClick: (InboxMessage) -> Unit) {
    Column {
        Text(
            stringResource(MR.strings.inbox_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
        if (state.isEmpty) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 64.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(MR.strings.inbox_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            // Space, not dividers, separates messages: 12dp inside each row plus 8dp between rows.
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.messages, key = { it.id }) { message ->
                    MessageItem(message, onClick = { onMessageClick(message) })
                }
            }
        }
    }
}

/**
 * Like a notification: unread messages have a bold title and a dot at the end, centred on the whole message; read
 * ones are dimmed. A plain Row rather than ListItem, which top-aligns its trailing slot once the text wraps.
 */
@Composable
private fun MessageItem(message: InboxMessage, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                message.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (message.isRead) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Still there, but invisible, once read, so the text doesn't widen when the message is tapped.
        Box(
            Modifier
                .padding(start = 16.dp)
                .size(8.dp)
                .background(
                    if (message.isRead) Color.Transparent else MaterialTheme.colorScheme.primary,
                    CircleShape,
                )
        )
    }
}
