package net.allthatglitters.server.concepts.magic

import com.drcorchit.justice.utils.json.JsonUtils.deserializeEnum
import com.drcorchit.justice.utils.math.units.Measurement
import com.drcorchit.justice.utils.math.units.TimeUnits
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.allthatglitters.server.concepts.Attribute
import net.allthatglitters.server.util.HtmlObject

class Spell(
    val name: String,
    val rarity: Rarity,
    val discipline: Discipline,
    val type: Type,
    val target: Target?,
    val duration: Measurement<TimeUnits.Time>?,
    val trainingReqs: ImmutableMap<String, JsonElement>,
    val castingReqs: ImmutableMap<String, JsonElement>,
    val effect: String,
    val note: String?,
    val scaling: ImmutableMap<String, String>,
) {
    val tag = makeTag(name)

    fun render(): String {
        val output = HtmlObject("div").withTag("class", "background")
        output.withContent(HtmlObject("a").withTag("id", tag))
        output.withContent(HtmlObject("h5").withContent(name))
        val category = discipline.describe(rarity, type)
        output.withContent(HtmlObject("p").withContent(HtmlObject("i").withContent(category)))

        if (target != null) {
            val targetText = "<b>Target</b>: ${target.render()}"
            output.withContent(HtmlObject("p").withContent(targetText))
        }
        if (duration != null) {
            output.withContent(HtmlObject("p").withContent("<b>Duration</b>: $duration"))
        }

        output.withContent(HtmlObject("p").withContent("<b>Effect</b>: $effect"))
        if (note != null) {
            output.withContent(
                HtmlObject("p").withTag("style", "margin-left: 25px")
                    .withContent("Note: $note")
            )
        }

        val outerDiv = HtmlObject("div").withTag("class", "background-2")
        val button = HtmlObject("button").withTag("class", "collapsible-2")
            .withContent("Additional Information")
        val innerDiv = HtmlObject("div").withTag("class", "content")
        if (trainingReqs.isNotEmpty()) {
            innerDiv.withContent(HtmlObject("p").withContent("<b>Training Requirements</b>:"))
            val trainingReqs = trainingReqs.map {
                val key = when (it.key) {
                    "slots" -> "Training Slots"
                    "spells" -> "Spells"
                    else -> Attribute.replaceAll(it.key)
                }
                val value = when (it.key) {
                    "spells" -> it.value.asJsonArray
                        .joinToString(", ") { ele -> toLink(ele.asString).render() }

                    else -> it.value.toString()
                }

                "<li><b>$key</b>: $value</li>"

            }.joinToString("\n")
            innerDiv.withContent(HtmlObject("ul").withContent(trainingReqs))
        }

        if (castingReqs.isNotEmpty()) {
            innerDiv.withContent(HtmlObject("p").withContent("<b>Casting Requirements</b>:"))
            val castingReqs = castingReqs.map {
                val key = when (it.key) {
                    "time" -> "Casting Time"
                    "ap" -> "Evocation AP"
                    "concentration" -> "Concentration AP"
                    else -> it.key
                }
                "<li><b>$key</b>: ${it.value}</li>"
            }.joinToString("\n")
            innerDiv.withContent(HtmlObject("ul").withContent(castingReqs))
        }

        if (scaling.isNotEmpty()) {
            innerDiv.withContent(HtmlObject("p").withContent("<b>Scaling</b>:"))
            val scaling = scaling.map {
                val key = when (it.key) {
                    "upcast" -> "Upcasting"
                    "dualcast" -> "Dual Casting"
                    else -> it.key
                }
                "<li><b>$key</b>: ${it.value}</li>"
            }.joinToString("\n")
            innerDiv.withContent(HtmlObject("ul").withContent(scaling))
        }

        if (innerDiv.hasContent()) {
            outerDiv.withContent(button)
            outerDiv.withContent(innerDiv)
            output.withContent(outerDiv)
        }

        return output.render()
    }

    override fun toString(): String {
        return "$name: ${discipline.describe(rarity, type)}"
    }

    companion object {
        val round: TimeUnits.Time = TimeUnits.add("rd", "Round", "Rounds", TimeUnits.SEC.ratio * 5)

        fun deserialize(info: JsonObject): Spell {
            val name = info.get("name").asString
            val rarity = info.get("rarity").deserializeEnum<Rarity>()
            val discipline = info.get("discipline").deserializeEnum<Discipline>()
            val type = info.get("type").deserializeEnum<Type>()
            val target = info.get("target")?.let { Target.deserialize(it) }
            val duration = info.get("duration")?.let { TimeUnits.deserialize(it, round) }
            val trainingReqs = info.getAsJsonObject("training_reqs")?.let {
                it.entrySet().stream()
                    .collect(
                        ImmutableMap.toImmutableMap(
                            { entry -> entry.key },
                            { entry -> entry.value })
                    )
            } ?: ImmutableMap.of()
            val castingReqs = info.getAsJsonObject("casting_reqs")?.let {
                it.entrySet().stream()
                    .collect(
                        ImmutableMap.toImmutableMap(
                            { entry -> entry.key },
                            { entry -> entry.value })
                    )
            } ?: ImmutableMap.of()
            val effect = info.get("effect").asString
            val note = info.get("note")?.asString
            val scaling = info.getAsJsonObject("scaling")?.let {
                it.entrySet().stream()
                    .collect(
                        ImmutableMap.toImmutableMap(
                            { entry -> entry.key },
                            { entry -> entry.value.asString })
                    )
            } ?: ImmutableMap.of()

            return Spell(
                name,
                rarity,
                discipline,
                type,
                target,
                duration,
                trainingReqs,
                castingReqs,
                effect,
                note,
                scaling
            )
        }

        fun makeTag(name: String): String {
            return name.replace(" +", "_").replace("[^a-zA-Z_]+", "")
        }

        fun toLink(name: String): HtmlObject {
            return HtmlObject("a").withTag("href", "#${makeTag(name)}").withContent(name)
        }
    }
}