package net.allthatglitters.server.util

import com.google.gson.JsonElement
import net.allthatglitters.server.concepts.magic.Discipline
import java.io.File

object Util {

    val json =
        """[{
          "name": "",
          "rarity": "common",
          "discipline": "%s",
          "type": "evocation",
          "target": "self",
          "duration": 1,
          "effect": "",
          "training_reqs": {
            "slots": 3
          },
            "casting_reqs": {
            "time": "1 Action",
            "ap": 3,
          },
          "scaling": {
          }
        }]""".trimIndent()

    @JvmStatic
    fun main(vararg args: String) {
        Discipline.entries.forEach {
            val file = File(inputDir, "spells/${it.name}.json")
            if (!file.exists()) {
                val jsonStr = String.format(json, it.name)
                file.writeText(jsonStr)
            }
        }
    }

    inline fun <reified E : Enum<E>> deserializeEnum(ele: JsonElement?, def: E? = null): E {
        return parseEnum(ele?.asString, def)
    }

    inline fun <reified E : Enum<E>> parseEnum(str: String?, def: E? = null): E {
        return str.let { enumValues<E>().firstOrNull { it.name.equals(str, true) } }
            ?: def
            ?: throw NoSuchElementException("No enum value named $str")
    }

    inline fun <reified E : Unit> deserializeMeasurement(
        ele: JsonElement?,
        defUnit: E,
        parser: (String) -> E
    ): Measurement<E> {
        val p = ele?.asJsonPrimitive
        return if (p == null) {
            Measurement(0.0, defUnit)
        } else if (p.isString) {
            parseMeasurement(p.asString, parser)
        } else {
            Measurement(ele.asDouble, defUnit)
        }
    }
}

inline fun <reified E : Unit> parseMeasurement(
    str: String,
    parser: (String) -> E
): Measurement<E> {
    return str.split(" ")
        .let {
            check(it.size == 2) { "Invalid measurement: $str" }
            Measurement(it.first().toDouble(), parser.invoke(it.last()))
        }
}