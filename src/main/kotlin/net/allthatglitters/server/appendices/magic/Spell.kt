package net.allthatglitters.server.appendices.magic

import com.drcorchit.justice.utils.json.JsonUtils.deserializeEnum
import com.drcorchit.justice.utils.math.units.Measurement
import com.drcorchit.justice.utils.math.units.TimeUnits
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.concepts.*
import net.allthatglitters.server.concepts.requirement.*
import net.allthatglitters.server.util.html.HtmlObject

class Spell(
	name: String,
	val rarity: Rarity,
	val discipline: Discipline,
	val type: Type,
	val target: Target?,
	val duration: Measurement<TimeUnits.Time>?,
	effect: String,
	val note: String?,
	trainingReqs: List<Requirement>,
	val castingReqs: List<Requirement>,
	val modifiers: ImmutableMap<String, Any>,
) : Ability(name, effect, trainingReqs) {
	val tag = makeTag(name)

	fun render(): String {
		val output = HtmlObject("div").withClass("background")
		output.withContent(HtmlObject("a").withAttribute("id", tag))
		output.withContent(HtmlObject("h5").withContent(name))
		val category = discipline.describe(rarity, type)
		output.withContent(HtmlObject("p").withContent(HtmlObject("i").withContent(category)))

		if (target != null) {
			output.withBoldedEntry("Target", target.render())
		}
		if (duration != null) {
			output.withBoldedEntry("Duration", duration.toString())
		}

		output.withBoldedEntry("Effect", effect)
		if (note != null) {
			output.withContent(
				HtmlObject("p").withStyle("margin-left: 25px")
					.withContent("Note: $note")
			)
		}

		val outerDiv = HtmlObject("div").withClass("background-inner")
		val button = HtmlObject("button").withClass("collapsible")
			.withContent("Additional Information")
		val innerDiv = HtmlObject("div").withClass("content")
		if (trainingReqs.isNotEmpty()) {
			innerDiv.withBoldedEntry("Training Requirements", "")
			innerDiv.withContent(HtmlObject("ul").withAll(trainingReqs))
		}

		if (castingReqs.isNotEmpty()) {
			innerDiv.withBoldedEntry("Casting Requirements", "")
			innerDiv.withContent(HtmlObject("ul").withAll(castingReqs))
		}

		if (modifiers.isNotEmpty()) {
			innerDiv.withBoldedEntry("Modifiers", "")
			val list = HtmlObject("ul")
			modifiers.forEach { list.withBoldedEntry(it.key, it.value.toString()) }
			innerDiv.withContent(list)
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

		fun deserialize(info: JsonObject): Spell {
			val name = info.get("name").asString
			val rarity = info.get("rarity").deserializeEnum<Rarity>()
			val discipline = info.get("discipline").deserializeEnum<Discipline>()
			val type = info.get("type").deserializeEnum<Type>()
			val target = info.get("target")?.let { Target.deserialize(it) }
			val duration = info.get("duration")?.let { TimeUnits.deserialize(it, round) }
			val effect = info.get("effect").asString
			val note = info.get("note")?.asString

			val trainingReqs = deserializeReqs(info.get("training_reqs"))
			val castingReqs = deserializeReqs(info.get("casting_reqs"))
			val modifiers = info.getAsJsonObject("modifiers")?.let {
				it.entrySet()
					.associate { entry -> mapModifiers(entry) }
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
				modifiers
			)
		}

		fun deserializeReqs(ele: JsonElement?): List<Requirement> {
			return if (ele == null) listOf()
			else if (ele.isJsonObject) {
				ele.asJsonObject.entrySet().map { entryToReq(it) }
			} else {
				throw UnsupportedOperationException("Cannot deserialize reqs from ${ele.javaClass}")
			}
		}

		fun entryToReq(entry: Map.Entry<String, JsonElement>): Requirement {
			return when (entry.key.lowercase()) {
				"str" -> AttrReq(Attribute.STR, entry.value.asInt)
				"dex" -> AttrReq(Attribute.DEX, entry.value.asInt)
				"spd" -> AttrReq(Attribute.SPD, entry.value.asInt)
				"int" -> AttrReq(Attribute.INT, entry.value.asInt)
				"nst" -> AttrReq(Attribute.NST, entry.value.asInt)
				"cha" -> AttrReq(Attribute.CHA, entry.value.asInt)
				"gold" -> GoldReq(entry.value.asInt)
				"slots" -> SlotsReq(entry.value.asInt)
				"time" -> TimeReq.parse(entry.value.asString)!!
				"wp" -> WillReq("Evocation Willpower", entry.value.asInt)
				"concentration" -> WillReq("Concentration Willpower", entry.value.asInt)
				"location" -> StringReq("Location", entry.value.asString)
				"limitation" -> StringReq("Limitation", entry.value.asString)
				"materials" -> StringReq("Materials", entry.value.asString)
				"spells" -> {
					object : AbilityReq("Required Spells") {
						override val reqAbilities: Set<Ability>
							get() = entry.value.asJsonArray
								.map { AppendixSpells.lookupSpell(it.asString) }
								.toSet()
					}
				}
				//TODO parse skill reqs
				else -> {
					AppendixSpells.logger.warn("Unable to fully parse requirement: ${entry.key} : ${entry.value}")
					OtherReq("${entry.key}: ${entry.value.asString}")
				}
			}
		}

		fun mapModifiers(entry: Map.Entry<String, JsonElement>): Pair<String, Any> {
			return when (entry.key) {
				//training reqs
				"slots" -> "Training Slots" to entry.value.asInt
				"spells" -> "Spells" to entry.value.asJsonArray
					.joinToString(", ") { ele -> toLink(ele.asString).render() }
				//casting reqs
				"time" -> "Casting Time" to TimeUnits.deserialize(entry.value, action)
				"wp" -> "Evocation WP" to entry.value.asInt
				"concentration" -> "Concentration WP" to entry.value
				//modifiers
				"upcast" -> "Upcasting" to entry.value.asString
				"dualcast" -> "Dual Casting" to entry.value.asString
				else -> (entry.key to entry.value.asString)
			}
		}

		fun makeTag(name: String): String {
			return name.replace(" +", "_").replace("[^a-zA-Z_]+", "")
		}

		fun toLink(name: String): HtmlObject {
			return HtmlObject("a").withAttribute("href", "#${makeTag(name)}").withContent(name)
		}
	}
}