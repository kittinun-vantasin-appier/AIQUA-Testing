package com.github.kittinunf.aiqua_testing.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.github.kittinunf.aiqua_testing.resources.*
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.StringResource

/**
 * Material 3 with the shared palette from sharedLogic's moko-resources (the same colors iOS uses).
 * Each color has a light and a dark value, and Android picks the one for the current system mode.
 */
@Composable
fun GroceryTheme(content: @Composable () -> Unit) {
    // Every role is set explicitly, so lightColorScheme's own defaults never show, in light or dark mode.
    val colorScheme = lightColorScheme(
        primary = color(MR.colors.primary),
        onPrimary = color(MR.colors.on_primary),
        primaryContainer = color(MR.colors.primary_container),
        onPrimaryContainer = color(MR.colors.on_primary_container),
        inversePrimary = color(MR.colors.inverse_primary),
        secondary = color(MR.colors.secondary),
        onSecondary = color(MR.colors.on_secondary),
        secondaryContainer = color(MR.colors.secondary_container),
        onSecondaryContainer = color(MR.colors.on_secondary_container),
        tertiary = color(MR.colors.tertiary),
        onTertiary = color(MR.colors.on_tertiary),
        tertiaryContainer = color(MR.colors.tertiary_container),
        onTertiaryContainer = color(MR.colors.on_tertiary_container),
        error = color(MR.colors.error),
        onError = color(MR.colors.on_error),
        errorContainer = color(MR.colors.error_container),
        onErrorContainer = color(MR.colors.on_error_container),
        background = color(MR.colors.background),
        onBackground = color(MR.colors.on_background),
        surface = color(MR.colors.surface),
        onSurface = color(MR.colors.on_surface),
        surfaceVariant = color(MR.colors.surface_variant),
        onSurfaceVariant = color(MR.colors.on_surface_variant),
        surfaceTint = color(MR.colors.surface_tint),
        inverseSurface = color(MR.colors.inverse_surface),
        inverseOnSurface = color(MR.colors.inverse_on_surface),
        outline = color(MR.colors.outline),
        outlineVariant = color(MR.colors.outline_variant),
        surfaceBright = color(MR.colors.surface_bright),
        surfaceDim = color(MR.colors.surface_dim),
        surfaceContainerLowest = color(MR.colors.surface_container_lowest),
        surfaceContainerLow = color(MR.colors.surface_container_low),
        surfaceContainer = color(MR.colors.surface_container),
        surfaceContainerHigh = color(MR.colors.surface_container_high),
        surfaceContainerHighest = color(MR.colors.surface_container_highest),
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}

@Composable
private fun color(resource: ColorResource) = colorResource(resource.resourceId)

@Composable
fun stringResource(resource: StringResource): String = stringResource(resource.resourceId)