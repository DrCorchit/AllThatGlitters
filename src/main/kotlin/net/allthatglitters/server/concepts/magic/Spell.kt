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
    val effect: String,
    val note: String?,
    val trainingReqs: ImmutableMap<String, Any>,
    val castingReqs: ImmutableMap<String, Any>,
    val scaling: ImmutableMap<String, Any>,
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
                "<li><b>${it.key}</b>: ${it.value}</li>"
            }.joinToString("\n")
            innerDiv.withContent(HtmlObject("ul").withContent(trainingReqs))
        }

        if (castingReqs.isNotEmpty()) {
            innerDiv.withContent(HtmlObject("p").withContent("<b>Casting Requirements</b>:"))
            val castingReqs = castingReqs.map {
                "<li><b>${it.key}</b>: ${it.value}</li>"
            }.joinToString("\n")
            innerDiv.withContent(HtmlObject("ul").withContent(castingReqs))
        }

        if (scaling.isNotEmpty()) {
            innerDiv.withContent(HtmlObject("p").withContent("<b>Scaling</b>:"))
            val scaling = scaling.map {
                "<li><b>${it.key}</b>: ${it.value}</li>"
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
        val round = TimeUnits.add("rd", "Round", "Rounds", TimeUnits.SEC.ratio * 5)
        val action = TimeUnits.add("act", "Action", "Actions", TimeUnits.SEC.ratio * 5)
        val shortRest = TimeUnits.add("short", "Short Rest", "Short Rests", TimeUnits.HOUR.ratio)
        val longRest = TimeUnits.add("long", "Long Rest", "Long Rests", TimeUnits.HOUR.ratio * 8)

        fun deserialize(info: JsonObject): Spell {
            val name = info.get("name").asString
            val rarity = info.get("rarity").deserializeEnum<Rarity>()
            val discipline = info.get("discipline").deserializeEnum<Discipline>()
            val type = info.get("type").deserializeEnum<Type>()
            val target = info.get("target")?.let { Target.deserialize(it) }
            val duration = info.get("duration")?.let { TimeUnits.deserialize(it, round) }
            val effect = info.get("effect").asString
            val note = info.get("note")?.asString

            val trainingReqs = info.getAsJsonObject("training_reqs")?.let {
                it.entrySet()
                    .associate { entry -> mapReqs(entry) }
                    .let { map -> ImmutableMap.copyOf(map) }
            } ?: ImmutableMap.of()
            val castingReqs = info.getAsJsonObject("casting_reqs")?.let {
                it.entrySet()
                    .associate { entry -> mapReqs(entry) }
                    .let { map -> ImmutableMap.copyOf(map) }
            } ?: ImmutableMap.of()
            val scaling = info.getAsJsonObject("scaling")?.let {
                it.entrySet()
                    .associate { entry -> mapReqs(entry) }
                    .let { map -> ImmutableMap.copyOf(map) }
            } ?: ImmutableMap.of()

            return Spell(
                name,
                rarity,
                discipline,
                type,
                target,
                duration,
                effect,
                note,
                trainingReqs,
                castingReqs,
                scaling
            )
        }

        fun mapReqs(entry: Map.Entry<String, JsonElement>): Pair<String, Any> {
            return when (entry.key) {
                //training reqs
                "slots" -> "Training Slots" to entry.value.asInt
                "spells" -> "Spells" to entry.value.asJsonArray
                    .joinToString(", ") { ele -> toLink(ele.asString).render() }
                //casting reqs
                "time" -> "Casting Time" to TimeUnits.deserialize(entry.value, action)
                "ap" -> "Evocation AP" to entry.value.asInt
                "concentration" -> "Concentration AP" to entry.value
                //scaling
                "upcast" -> "Upcasting" to entry.value.asString
                "dualcast" -> "Dual Casting" to entry.value.asString
                else -> Attribute.parse(entry.key)
                    ?.let { it.fullName to entry.value.asInt }
                    ?: (entry.key to entry.value.asString)
            }
        }

        fun makeTag(name: String): String {
            return name.replace(" +", "_").replace("[^a-zA-Z_]+", "")
        }

        fun toLink(name: String): HtmlObject {
            return HtmlObject("a").withTag("href", "#${makeTag(name)}").withContent(name)
        }
    }
}