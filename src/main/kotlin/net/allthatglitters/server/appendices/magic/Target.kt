package net.allthatglitters.server.appendices.magic

import com.drcorchit.justice.utils.json.JsonUtils.deserializeEnum
import com.drcorchit.justice.utils.math.units.DistanceUnits
import com.drcorchit.justice.utils.math.units.Measurement
import com.google.gson.JsonElement

sealed class Target {

    abstract fun render(): String

    data object Self : Target() {
        override fun render(): String {
            return "Self"
        }
    }

    class Touch(val type: Type) : Target() {
        override fun render(): String {
            return "Touch ($type)"
        }
    }

    class Range(
        val type: Type,
        val count: Int = 1,
        val range: Measurement<DistanceUnits.Distance>,
        val lineOfSight: Boolean = true
    ) :
        Target() {
        override fun render(): String {
            val countStr = when (count) {
                0 -> "All ${type.plural}"
                1 -> "One ${type.singular}"
                else -> "$count ${type.plural}"
            }
            val los = if (lineOfSight) "" else "(Line-of-sight not required.)"
            return "$countStr within $range feet $los".trim()
        }
    }

    class Shape(
        val type: Target.Type,
        val shape: Type,
        val range: Measurement<DistanceUnits.Distance>,
        val radius: Measurement<DistanceUnits.Distance>
    ) :
        Target() {
        override fun render(): String {
            val part1 = "Affects all ${type.plural} that overlap a"
            val part2 = "${radius.toString("%.0f", true)} ${shape.dimension} ${shape.name.lowercase()}"
            val part3 = when (shape) {
                Type.Cone -> if (range.isZero()) "originating from the caster."
                else "originating from a point $range from the caster."

                Type.Line -> if (range.isZero()) "originating from the caster."
                else "from any two points at most $range from the caster."

                else -> if (range.isZero()) "centered on the caster."
                else "centered on a point $range from the caster."
            }
            return "$part1 $part2 $part3"
        }


        enum class Type(val dimension: String) {
            Cone("radius"), Cube("wide"), Cylinder("radius"), Line("long"), Sphere("radius");
        }
    }

    companion object {
        fun deserialize(ele: JsonElement): Target {
            try {
                if (ele.isJsonPrimitive) {
                    if (ele.asString == "self") return Self
                } else {
                    val obj = ele.asJsonObject
                    return if (obj.has("touch")) {
                        val type = obj.get("touch").deserializeEnum<Type>()
                        Touch(type)
                    } else if (obj.has("shape")) {
                        val type = obj.get("type").deserializeEnum<Type>()
                        val shape = obj.get("shape").deserializeEnum<Shape.Type>()
                        val range = DistanceUnits.deserialize(obj.get("range"))
                        val radius = DistanceUnits.deserialize(obj.get("radius"))
                        Shape(type, shape, range, radius)
                    } else {
                        val type = obj.get("type").deserializeEnum<Type>()
                        val count = obj.get("count")?.asInt ?: 1
                        val range = DistanceUnits.deserialize(obj.get("range"))
                        return Range(type, count, range)
                    }
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Could not deserialize spell target: $ele", e)
            }
            throw IllegalArgumentException("Could not deserialize spell target: $ele")
        }
    }

    enum class Type(val singular: String, val plural: String) {
        Point("point", "points"),
        Ally("ally", "allies"),
        Enemy("enemy", "enemies"),
        Object("object", "objects"),
        Creature("creature", "creatures"),
        Any("target", "targets");
    }
}