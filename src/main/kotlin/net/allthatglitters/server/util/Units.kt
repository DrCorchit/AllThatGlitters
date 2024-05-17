package net.allthatglitters.server.util

interface Unit {
    val singular: String
    val plural: String
    val ratio: Double
}

class Measurement<T : Unit>(val value: Double, val unit: T) {
    fun convert(newUnit: T): Measurement<T> {
        val newValue = value * newUnit.ratio / unit.ratio
        return Measurement(newValue, newUnit)
    }

    override fun toString(): String {
        return toString("%.0f")
    }

    fun toString(format: String): String {
        val valStr = String.format(format, value)
        val unitStr = if (value == 1.0) unit.singular else unit.plural
        return "$valStr $unitStr"
    }

    fun isZero(): Boolean {
        return value == 0.0
    }
}

enum class Distance(
    override val singular: String,
    override val plural: String,
    override val ratio: Double
) : Unit {
    CM("Centimeter", "Centimeters", 1.0),
    M("Meter", "Meters", CM.ratio * 100),
    KM("Kilometer", "Kilometers", M.ratio * 1000),
    IN("Inch", "Inches", 2.54),
    FT("Foot", "Feet", IN.ratio * 12),
    YD("Yard", "Yards", FT.ratio * 3),
    MI("Mile", "Miles", FT.ratio * 5280);
}

enum class Time(
    override val singular: String,
    override val plural: String,
    override val ratio: Double
) : Unit {
    SEC("Second", "Seconds", 1.0),
    ROUND("Round", "Rounds", SEC.ratio * 5),
    MIN("Minute", "Minutes", SEC.ratio * 60),
    HOUR("Hour", "Hours", MIN.ratio * 60),
    DAY("Day", "Days", HOUR.ratio * 24),
    MONTH("Month", "Months", DAY.ratio * 30),
    YEAR("Year", "Years", DAY.ratio * 365),
    CENTURY("Century", "Centuries", YEAR.ratio * 100),
    MILLENNIA("Millennium", "Millennia", YEAR.ratio * 1000)
}

enum class Mass(
    override val singular: String,
    override val plural: String,
    override val ratio: Double
) : Unit {
    GRAM("Gram", "Grams", 1.0),
    KG("Kilogram", "Kilograms", GRAM.ratio * 1000),
    TONNE("Tonne", "Tonnes", KG.ratio * 1000),
    LB("Pound", "Pounds", 453.59),
    TON("Ton", "Tons", LB.ratio * 2000)
}

