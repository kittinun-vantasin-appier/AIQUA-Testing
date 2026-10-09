package com.github.kittinunf.aiqua_testing.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kittinunf.aiqua_testing.R
import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.home.HomeUiState
import com.github.kittinunf.aiqua_testing.home.HomeViewModel
import com.github.kittinunf.aiqua_testing.home.ProductRow
import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.home_error
import com.github.kittinunf.aiqua_testing.resources.home_title
import com.github.kittinunf.aiqua_testing.resources.inbox_title
import com.github.kittinunf.aiqua_testing.resources.retry
import com.github.kittinunf.aiqua_testing.ui.QuantityStepper
import com.github.kittinunf.aiqua_testing.ui.theme.stringResource

private const val COLUMNS = 3

/** [inboxSheet] is shown over Home while the inbox is open; it calls its `onDismiss` when the Shopper closes it. */
@Composable
fun HomeScreen(viewModel: HomeViewModel, inboxSheet: @Composable (onDismiss: () -> Unit) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isInboxOpen by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.onScreenViewed() }
    HomeContent(
        state = state,
        onInboxClick = { isInboxOpen = true },
        onRetry = viewModel::onRetryClick,
        onAdd = viewModel::onAddClick,
        onDecrement = viewModel::onDecrementClick,
        onRemove = viewModel::onRemoveClick,
    )
    if (isInboxOpen) inboxSheet { isInboxOpen = false }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState,
    onInboxClick: () -> Unit,
    onRetry: () -> Unit,
    onAdd: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onRemove: (Product) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(MR.strings.home_title),
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Medium,
                        textAlign = if (scrollBehavior.state.collapsedFraction > 0.5f) {
                            TextAlign.Center
                        } else {
                            TextAlign.Start
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = { InboxButton(state.inboxBadgeText, onInboxClick) },
                expandedHeight = 100.dp,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        // Content wins: once there are sections, keep showing them whatever else is going on.
        when {
            state.sections.isEmpty() && state.isLoading -> Box(
                modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.sections.isEmpty() && state.error != null -> Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(MR.strings.home_error),
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
                    Text(stringResource(MR.strings.retry))
                }
            }

            // A LazyColumn of card rows rather than a grid, so Category headers can stay sticky.
            else -> LazyColumn(modifier) {
                state.sections.forEach { section ->
                    stickyHeader(key = section.title) { SectionHeader(section.title) }
                    items(section.rows.chunked(COLUMNS), key = { it.first().product.id }) { rows ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rows.forEach { row ->
                                ProductCard(row, onAdd, onDecrement, onRemove, Modifier.weight(1f))
                            }
                            repeat(COLUMNS - rows.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InboxButton(badgeText: String?, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        BadgedBox(badge = { badgeText?.let { Badge { Text(it) } } }) {
            Icon(painterResource(R.drawable.ic_mail), contentDescription = stringResource(MR.strings.inbox_title))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun ProductCard(
    row: ProductRow,
    onAdd: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onRemove: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Everything for one Product lives inside one card: the stepper floats over the top of the emoji,
    // and the name and price sit along the bottom.
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier,
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                Text(
                    text = row.product.emoji,
                    fontSize = 40.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 16.dp),
                )
                QuantityStepper(
                    quantity = row.quantity,
                    canAdd = row.canAdd,
                    onAdd = { onAdd(row.product) },
                    onDecrement = { onDecrement(row.product) },
                    onRemove = { onRemove(row.product) },
                    // "+" sits in the corner; once in the Cart the stepper spans the top edge.
                    modifier = if (row.quantity == 0) {
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    } else {
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(6.dp)
                            .fillMaxWidth()
                            .height(32.dp)
                    },
                    compact = true,
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = row.product.name,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = row.priceText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
