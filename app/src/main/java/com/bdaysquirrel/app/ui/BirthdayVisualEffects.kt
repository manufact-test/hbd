package com.bdaysquirrel.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private val FxNightPlum = Color(0xFF171226)
private val FxDeepIndigo = Color(0xFF241A3A)
private val FxCardViolet = Color(0xFF2D2144)
private val FxRaisedViolet = Color(0xFF392A55)
private val FxDarkOutline = Color(0xFF100B1C)
private val FxCoral = Color(0xFFFF7A3D)
private val FxPeach = Color(0xFFFFB36B)
private val FxMint = Color(0xFF6FE7C8)
private val FxLavender = Color(0xFFA98BFF)
private val FxCream = Color(0xFFFFF4D8)
private val FxSoftCream = Color(0xFFE8DEC7)
private val FxMuted = Color(0xFFB8B1CA)
private val FxDarkText = Color(0xFF21162B)

internal data class BirthdayCardVisuals(
    val scale: Float,
    val backgroundBrush: Brush?,
    val borderColor: Color,
    val shadowColor: Color,
)

internal data class HeroPanelVisuals(
    val backgroundBrush: Brush,
    val borderColor: Color,
)

@Composable
internal fun AnimatedAppBackdrop(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "appBackdrop")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "appBackdropPhase",
    )

    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    lerp(FxNightPlum, FxDeepIndigo, phase * 0.72f),
                    lerp(FxDeepIndigo, Color(0xFF321D49), phase),
                    lerp(FxNightPlum, Color(0xFF211132), 1f - phase),
                ),
            ),
        ),
    )
}

@Composable
internal fun rememberHeroPanelVisuals(): HeroPanelVisuals {
    val transition = rememberInfiniteTransition(label = "heroGradient")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4_800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "heroGradientPhase",
    )

    return HeroPanelVisuals(
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                lerp(FxCardViolet, FxDeepIndigo, phase * 0.55f),
                FxCardViolet,
                lerp(FxCardViolet, FxLavender, 0.08f + phase * 0.10f),
            ),
            start = Offset.Zero,
            end = Offset(1_200f, 520f),
        ),
        borderColor = lerp(FxRaisedViolet, FxLavender, phase * 0.30f),
    )
}

@Composable
internal fun rememberBirthdayCardVisuals(
    birthdayId: Long,
    isToday: Boolean,
    burstOnEnter: Boolean,
): BirthdayCardVisuals {
    val pulseTransition = rememberInfiniteTransition(label = "birthdayPulse-$birthdayId")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "birthdayPulseValue-$birthdayId",
    )

    val entranceScale = remember(birthdayId) { Animatable(1f) }
    LaunchedEffect(burstOnEnter) {
        if (burstOnEnter) {
            entranceScale.snapTo(0.82f)
            entranceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.52f,
                    stiffness = 360f,
                ),
            )
        }
    }

    val todayScale = if (isToday) 1f + pulse * 0.012f else 1f
    return BirthdayCardVisuals(
        scale = entranceScale.value * todayScale,
        backgroundBrush = if (isToday) {
            Brush.horizontalGradient(
                colors = listOf(
                    lerp(FxCoral.copy(alpha = 0.34f), FxLavender.copy(alpha = 0.24f), pulse),
                    lerp(FxCardViolet, FxDeepIndigo, pulse * 0.42f),
                    lerp(FxLavender.copy(alpha = 0.28f), FxCoral.copy(alpha = 0.30f), pulse),
                ),
            )
        } else {
            null
        },
        borderColor = if (isToday) lerp(FxCoral, FxPeach, pulse) else FxRaisedViolet,
        shadowColor = if (isToday) FxCoral.copy(alpha = 0.72f) else FxDarkOutline,
    )
}

@Composable
internal fun PixelBurstOverlay(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(1f) }
    val colors = remember { listOf(FxCoral, FxPeach, FxMint, FxLavender, FxCream) }

    LaunchedEffect(active) {
        if (active) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            )
        }
    }

    if (progress.value >= 1f) return

    Canvas(modifier = modifier) {
        val center = Offset(size.width * 0.5f, size.height * 0.52f)
        val maxDistance = min(size.width, size.height) * 0.68f
        val eased = FastOutSlowInEasing.transform(progress.value.coerceIn(0f, 1f))
        val alpha = (1f - progress.value).coerceIn(0f, 1f)
        val basePixel = 4.dp.toPx()

        repeat(24) { index ->
            val angle = (2.0 * PI * index / 24.0) + ((index % 3) - 1) * 0.11
            val distance = maxDistance * eased * (0.62f + (index % 5) * 0.09f)
            val pixelSize = basePixel * (0.72f + (index % 4) * 0.18f) *
                (1f - progress.value * 0.36f)
            val x = center.x + cos(angle).toFloat() * distance - pixelSize / 2f
            val y = center.y + sin(angle).toFloat() * distance - pixelSize / 2f

            drawRect(
                color = colors[index % colors.size].copy(alpha = alpha),
                topLeft = Offset(x, y),
                size = Size(pixelSize, pixelSize),
            )
        }
    }
}

@Composable
internal fun YearlessBirthdayPickerDialog(
    initialMonth: Int,
    initialDay: Int,
    locale: Locale,
    onDismiss: () -> Unit,
    onConfirm: (month: Int, day: Int) -> Unit,
) {
    var selectedMonth by rememberSaveable { mutableIntStateOf(initialMonth.coerceIn(1, 12)) }
    var selectedDay by rememberSaveable { mutableIntStateOf(initialDay.coerceAtLeast(1)) }
    val daysInMonth = YearMonth.of(2000, selectedMonth).lengthOfMonth()

    LaunchedEffect(selectedMonth) {
        if (selectedDay > daysInMonth) selectedDay = daysInMonth
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
            color = FxDeepIndigo,
            contentColor = FxCream,
            border = BorderStroke(2.dp, FxRaisedViolet),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Выбери день и месяц",
                    style = MaterialTheme.typography.titleLarge,
                    color = FxCream,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "МЕСЯЦ",
                    style = MaterialTheme.typography.labelLarge,
                    color = FxMint,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))

                (1..12).chunked(3).forEach { monthRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        monthRow.forEach { month ->
                            val selected = month == selectedMonth
                            TextButton(
                                onClick = { selectedMonth = month },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = if (selected) FxCoral else FxCardViolet,
                                    contentColor = if (selected) FxDarkText else FxSoftCream,
                                ),
                            ) {
                                Text(
                                    text = monthShortLabel(month, locale),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(7.dp))
                }

                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "ДЕНЬ",
                    style = MaterialTheme.typography.labelLarge,
                    color = FxMint,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))

                (1..daysInMonth).chunked(7).forEach { dayRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        dayRow.forEach { day ->
                            val selected = day == selectedDay
                            TextButton(
                                onClick = { selectedDay = day },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                shape = CutCornerShape(topStart = 5.dp, bottomEnd = 5.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = if (selected) FxLavender else Color.Transparent,
                                    contentColor = if (selected) FxDarkText else FxSoftCream,
                                ),
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                )
                            }
                        }
                        repeat(7 - dayRow.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = FxMuted)
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 3.dp))
                    Button(
                        onClick = { onConfirm(selectedMonth, selectedDay) },
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FxCoral,
                            contentColor = FxDarkText,
                        ),
                    ) {
                        Text("Готово")
                    }
                }
            }
        }
    }
}

private fun monthShortLabel(month: Int, locale: Locale): String {
    val raw = java.time.Month.of(month)
        .getDisplayName(TextStyle.SHORT, locale)
        .replace(".", "")
    return raw.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(locale) else char.toString()
    }
}
