package net.allthatglitters.server.appendices

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMap.toImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.allthatglitters.server.util.HtmlFile
import net.allthatglitters.server.util.HtmlObject
import java.io.File

enum class Rarity {
    Common, Academic, Esoteric, Forbidden
}

enum class School(adjective: String, primaryAttr: Attribute) {
    Alchemy("alchemic", Attribute.INT),
    Astrology("astrological", Attribute.INT),
    Biomancy("biological", Attribute.CHA),
    Conjuration("conjuration", Attribute.CHA),
    Enchantment("enchantment", Attribute.INT),
    Elementurgy("elemental", Attribute.WILL),
    Necromancy("necromantic", Attribute.WILL),
    Psionics("psychic", Attribute.WILL);

    companion object {
        fun deserialize(ele: JsonElement): School {
            val str = ele.asString
            return entries.first { str.equals(it.name, true) }
        }
    }
}

enum class Type {
    Evocation, Concentration, Ritual, Ceremony
}

enum class Target {
    Self, Touch, Ally, Enemy, Object, Any, Point, Shape
}

enum class Shape {
    Cone, Cube, Cylinder, Line, Sphere
}

class Spell(
    val name: String,
    val rarity: Rarity,
    val school: School,
    val type: Type,
    val target: Target,
    val shape: Shape?,
    val range: String?,
    val radius: String?,
    val duration: String?,
    val trainingReqs: ImmutableMap<String, String>,
    val castingReqs: ImmutableMap<String, String>,
    val effect: String,
    val scaling: ImmutableMap<String, String>,
) {

    fun render(): String {
        val output = mutableListOf<HtmlObject>()
        output.add(HtmlObject("h5").withContent(name))
        val category = "$rarity $school $type"
        output.add(HtmlObject("p").withContent(HtmlObject("i").withContent(category)))

        val targetType = shape?.name ?: target.name
        var targetText = "<b>Target</b>: $targetType"
        if (range != null && radius != null) {
            targetText += " (Range: $range Radius: $radius)"
        } else if (range != null) targetText += " (Range: $range)"
        else if (radius != null) targetText += " (Radius: $radius)"
        output.add(HtmlObject("p").withContent(targetText))
        if (duration != null) output.add(HtmlObject("p").withContent("<b>Duration</b>: $duration"))

        if (trainingReqs.isNotEmpty()) {
            val trainingReqs = "<b>Training Requirements</b>: " + trainingReqs.map {
                val key = when (it.key) {
                    "slots" -> "Training Slots"
                    "spells" -> "Spells"
                    else -> Attribute.replaceAll(it.key)
                }
                "$key: ${it.value}"
            }.joinToString(", ")
            output.add(HtmlObject("p").withContent(trainingReqs))
        }

        if (castingReqs.isNotEmpty()) {
            val castingReqs = "<b>Casting Requirements</b>: " + castingReqs.map {
                val key = when (it.key) {
                    "actions" -> "Actions"
                    "ap" -> "Evocation AP"
                    "concentration" -> "Concentration AP"
                    else -> it.key
                }
                "$key: ${it.value}"
            }
            output.add(HtmlObject("p").withContent(castingReqs))
        }

        if (scaling.isNotEmpty()) {
            val scaling = "<b>Scaling</b>: " + scaling.map {
                val key = when (it.key) {
                    "upcast" -> "Upcasting"
                    "dualcast" -> "Dual Casting"
                    else -> it.key
                }
                "$key: ${it.value}"
            }
            output.add(HtmlObject("p").withContent(scaling))
        }

        return output.joinToString("\n")
    }

    companion object {
        fun deserialize(info: JsonObject): Spell {
            val name = info.get("name").asString
            val rarity = info.get("rarity").let { Rarity.valueOf(it.asString) }
            val school = info.get("school").let { School.valueOf(it.asString) }
            val type = info.get("type").let { Type.valueOf(it.asString) }
            val target = info.get("target").let { Target.valueOf(it.asString) }
            val shape = if (target == Target.Shape) info.get("shape")
                .let { Shape.valueOf(it.asString) } else null
            val range = info.get("range")?.asString
            val radius = info.get("radius")?.asString
            val duration = info.get("duration")?.asString
            val trainingReqs = info.getAsJsonObject("training_reqs")?.let {
                it.entrySet().stream()
                    .collect(toImmutableMap({ entry -> entry.key }, { entry -> entry.value.toString() }))
            } ?: ImmutableMap.of()
            val castingReqs = info.getAsJsonObject("casting_reqs")?.let {
                it.entrySet().stream()
                    .collect(toImmutableMap({ entry -> entry.key }, { entry -> entry.value.toString() }))
            } ?: ImmutableMap.of()
            val effect = info.get("effect").asString
            val scaling = info.getAsJsonObject("scaling")?.let {
                it.entrySet().stream()
                    .collect(toImmutableMap({ entry -> entry.key }, { entry -> entry.value.asString }))
            } ?: ImmutableMap.of()

            return Spell(
                name,
                rarity,
                school,
                type,
                target,
                shape,
                range,
                radius,
                duration,
                trainingReqs,
                castingReqs,
                effect,
                scaling
            )
        }
    }
}

class Spells(private val spellsFileName: String): HtmlFile("Appendix: Spells", "appendix_spells.html") {

    override fun appendBody(): HtmlFile {
        return append(renderSpells())
    }

    fun renderSpells(): String {
        val json = File(spellsFileName).readText()
            .let { JsonParser.parseString(it) }
            .asJsonArray

        return json.joinToString("\n") { Spell.deserialize(it.asJsonObject).render() }
    }
}