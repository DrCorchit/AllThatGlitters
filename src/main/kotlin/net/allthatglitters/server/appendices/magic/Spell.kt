package net.allthatglitters.server.appendices.magic

import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils.deserializeEnum
import com.drcorchit.justice.utils.json.JsonUtils.toJsonObject
import com.drcorchit.justice.utils.math.units.Measurement
import com.drcorchit.justice.utils.math.units.TimeUnits
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.allthatglitters.server.chapters.sheet.Attribute
import net.allthatglitters.server.concepts.*
import net.allthatglitters.server.concepts.requirement.*
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.html.HtmlObject

class Spell(
	name: String,
	val rarity: Rarity,
	val discipline: Discipline,
	val type: Type,
	val duration: Measurement<TimeUnits.Time>?,
	effect: String,
	trainingReqs: List<Requirement>,
	val castingReqs: List<Requirement>,
	val modifiers: Map<String, Any>,
) : Trainable(name, effect, trainingReqs), HasProperties {
	private val tag = name.normalize()

	fun render(): String {
		val output = HtmlObject("div").withClass("background")
		output.withContent(HtmlObject("a").withAttribute("id", tag))
		output.withContent(HtmlObject("h5").withContent(name))
		val category = discipline.describe(rarity, type)
		output.withContent(HtmlObject("p").withContent(HtmlObject("i").withContent(category)))

		//if (target != null) output.withBoldedEntry("Target", target.render())
		if (duration != null) {
			output.withBoldedEntry("Duration", duration.toString())
		}

		output.withBoldedEntry("Effect", effect)

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

	fun serialize(): JsonObject {
		val output = JsonObject()
		output.addProperty("name", name)
		output.addProperty("rarity", rarity.name)
		output.addProperty("discipline", discipline.name)
		output.addProperty("type", type.name)
		output.addProperty("effect", effect)


		val castingReqsInfo =
			castingReqs.map { it.serialize() }.associate { it.first to it.second }.toJsonObject()

		output.add("castingReqs", castingReqsInfo)

		return output
	}

	fun linkTo(text: String = name): HtmlObject {
		return HtmlObject("a")
			.withAttribute("href", "${AppendixSpells.outputFile}#$tag")
			.withContent(text)
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"name" -> name
			//TODO other properties
			else -> null
		}
	}

	override fun toString(): String {
		return "$name: ${discipline.describe(rarity, type)}"
	}

	companion object : HasProperties {
		fun deserialize(info: JsonObject): Spell {
			val name = info.get("name").asString
			val rarity = info.get("rarity").deserializeEnum<Rarity>()
			val discipline = info.get("discipline").deserializeEnum<Discipline>()
			val type = info.get("type").deserializeEnum<Type>()
			val target = info.get("target")?.let { Target.deserialize(it) }
			val duration = info.get("duration")?.let { TimeUnits.deserialize(it, round) }
			val effect = info.get("effect").asString

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
				duration,
				effect,
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
				"str", "dex", "spd", "int", "nst", "cha" -> {
					AttrReq(Attribute.parse(entry.key), entry.value.asInt)
				}

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
						override val reqAbilities: Set<Trainable>
							get() = entry.value.asJsonArray
								.map { AppendixSpells.lookupSpell(it.asString) }
								.toSet()
					}
				}
				//TODO parse skill reqs
				else -> {
					AppendixSpells.logger.warn("Unable to fully parse requirement: ${entry.key} : ${entry.value}")
					StringReq(entry.key, entry.value.asString)
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

		fun toLink(name: String): HtmlObject {
			return HtmlObject("a").withAttribute("href", "#${name.normalize()}").withContent(name)
		}

		override fun getProperty(property: String): Any {
			return AppendixSpells.lookupSpell(property)
		}
	}
}