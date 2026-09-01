package com.bdaysquirrel.app.ui

data class ZodiacInfo(
    val name: String,
    val symbol: String,
)

internal fun zodiacInfo(day: Int, month: Int): ZodiacInfo = when (month.coerceIn(1, 12)) {
    1 -> if (day >= 20) ZodiacInfo("Водолей", "♒") else ZodiacInfo("Козерог", "♑")
    2 -> if (day >= 19) ZodiacInfo("Рыбы", "♓") else ZodiacInfo("Водолей", "♒")
    3 -> if (day >= 21) ZodiacInfo("Овен", "♈") else ZodiacInfo("Рыбы", "♓")
    4 -> if (day >= 20) ZodiacInfo("Телец", "♉") else ZodiacInfo("Овен", "♈")
    5 -> if (day >= 21) ZodiacInfo("Близнецы", "♊") else ZodiacInfo("Телец", "♉")
    6 -> if (day >= 21) ZodiacInfo("Рак", "♋") else ZodiacInfo("Близнецы", "♊")
    7 -> if (day >= 23) ZodiacInfo("Лев", "♌") else ZodiacInfo("Рак", "♋")
    8 -> if (day >= 23) ZodiacInfo("Дева", "♍") else ZodiacInfo("Лев", "♌")
    9 -> if (day >= 23) ZodiacInfo("Весы", "♎") else ZodiacInfo("Дева", "♍")
    10 -> if (day >= 23) ZodiacInfo("Скорпион", "♏") else ZodiacInfo("Весы", "♎")
    11 -> if (day >= 22) ZodiacInfo("Стрелец", "♐") else ZodiacInfo("Скорпион", "♏")
    else -> if (day >= 22) ZodiacInfo("Козерог", "♑") else ZodiacInfo("Стрелец", "♐")
}

internal fun birthYearLabel(year: Int?): String = year?.let { "$it год" } ?: "Год не указан"
