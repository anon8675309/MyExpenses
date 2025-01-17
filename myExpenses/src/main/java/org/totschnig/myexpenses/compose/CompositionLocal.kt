package org.totschnig.myexpenses.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.totschnig.myexpenses.util.DebugCurrencyFormatter
import org.totschnig.myexpenses.util.ICurrencyFormatter
import java.time.format.DateTimeFormatter

data class Colors(
    val income: Color,
    val expense: Color,
    val transfer: Color,
    val iconTint: Color
)

val LocalColors = compositionLocalOf { Colors(
        income = Color.Red,
        expense = Color.Green,
        transfer = Color.Unspecified,
        iconTint = Color.DarkGray
    ) }

val LocalCurrencyFormatter = staticCompositionLocalOf<ICurrencyFormatter> { DebugCurrencyFormatter }

val LocalDateFormatter = staticCompositionLocalOf<DateTimeFormatter> { DateTimeFormatter.BASIC_ISO_DATE }
