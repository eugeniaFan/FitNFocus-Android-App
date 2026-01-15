package com.example.fitnfocus.ui.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FnfScreenScaffold(
    title: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    contentPadding: PaddingValues = PaddingValues(FnfDefaults.screenPadding),
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    when {
                        titleContent != null -> titleContent()
                        title != null -> Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        else -> {}
                    } },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
//                windowInsets = WindowInsets.statusBars
//                    .only(WindowInsetsSides.Top),
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface,
                    navigationIconContentColor = colors.onSurface,
                    actionIconContentColor = colors.onSurface
                )
            )
        }
    ) { innerPadding ->
        // innerPadding + dein Standard Padding kombinieren:
        val combined = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr) + contentPadding.calculateStartPadding(
                androidx.compose.ui.unit.LayoutDirection.Ltr
            ),
            top = innerPadding.calculateTopPadding() + contentPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr) + contentPadding.calculateEndPadding(
                androidx.compose.ui.unit.LayoutDirection.Ltr
            ),
            bottom = innerPadding.calculateBottomPadding() + contentPadding.calculateBottomPadding()
        )

        content(combined)
    }
}