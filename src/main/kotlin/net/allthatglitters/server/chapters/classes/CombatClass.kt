package net.allthatglitters.server.chapters.classes

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.allthatglitters.server.util.html.HtmlContent
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlString
import net.allthatglitters.server.util.html.Renderable
import net.allthatglitters.server.util.italicise

class CombatClass(
	val name: String,
	val altName: String?,
	val description: HtmlContent,
	val backstoryPrompts: ImmutableList<String>,
	val alignmentSuggestion: String?,
	val coreAbilityName: String,
	val coreAbilityDescription: HtmlContent,
	val limitations: String?,
	val levelingBonuses: ImmutableMap<Int, HtmlContent>,
	val startingEquipment: Renderable
) : Renderable {

	override fun render(): String {
		try {
			return renderUnsafe()
		} catch (e: Exception) {
			throw Exception("Error rendering class $name", e)
		}
	}

	fun renderUnsafe(): String {
		val output = HtmlObject.background()

		val nameHtml = HtmlObject("h5")
			.withContent(HtmlObject("a").withAttribute("id", name))
			.withContent(name)
		//if (altName != null) nameHtml.withContent(" ($altName)")
		output.withContent(nameHtml)
		output.withContent(description.wrapRaw("p"))
		output.withContent(
			HtmlObject("p")
				.withContent("While writing the backstory for a ${name.lowercase()}, consider the following:")
		)
		val prompts = HtmlObject("ol")
			.withAll(backstoryPrompts.map { HtmlObject("li").withContent(it) })
		output.withContent(prompts)

		if (alignmentSuggestion != null) {
			output.withBoldedEntry("Alignment Suggestion", alignmentSuggestion)
		}

		val coreAbilityName =
			HtmlObject.boldedEntry("p", "Core Ability", "${coreAbilityName.italicise()}. ")
		coreAbilityDescription.preface(coreAbilityName).wrapRaw("p")
		output.withContent(coreAbilityDescription)

		if (limitations != null) {
			output.withBoldedEntry("Limitations", limitations)
		}

		val outerDiv = HtmlObject("div").withClass("background-inner")
		val button = HtmlObject("button").withClass("collapsible")
			.withContent("Additional Information")
		val innerDiv = HtmlObject("div").withClass("content")

		innerDiv.withBoldedEntry("Leveling Bonuses", "")
		levelingBonuses.forEach {
			val atLevel = HtmlObject("p").withContent("At level ${it.key}, ")
			//innerDiv.withContent(it.value.preface(atLevel))
			it.value.preface(atLevel)
			innerDiv.withContent(it.value)
		}
		innerDiv.withBoldedEntry("Starting Equipment", "")
		innerDiv.withContent(startingEquipment)

		outerDiv.withContent(button)
		outerDiv.withContent(innerDiv)
		output.withContent(outerDiv)

		return output.render()
	}

	companion object {
		fun deserialize(obj: JsonObject): CombatClass {
			val name = obj.get("name").asString
			val altName = obj.get("altName")?.asString
			val description = obj.get("description")
				.let { Renderable.deserialize(it, Renderable.wrapInParagraph) }
				.asContent()
			val backstoryPrompts = obj.getAsJsonArray("backstoryPrompts")
				.map { it.asString }
				.let { ImmutableList.copyOf(it) }
			val alignmentSuggestion = obj.get("alignment")?.asString
			val coreAbilityName = obj.get("coreAbilityName").asString
			val coreAbilityDescription =
				obj.get("coreAbilityDescription")
					.let { Renderable.deserialize(it) }
					.asContent()
			val limitations = obj.get("limitations")?.asString
			val levelingBonuses = obj.getAsJsonObject("levelingBonuses").entrySet()
				.associate { it.key.toInt() to Renderable.deserialize(it.value).asContent() }
				.let { ImmutableMap.copyOf(it) }

			val equObj = JsonObject()
			val baseArr = JsonArray()
			equObj.add("_", baseArr)

			val se = obj.get("startingEquipment") ?: JsonObject()
			val startingEquipment = if (se.isJsonArray) {
				println("$name uses jsonarray inventory")
				obj.getAsJsonArray("startingEquipment")
					?.map {
						if (it.isJsonArray) {
							val arr = it.asJsonArray
							if (arr.isEmpty) {
								throw IllegalArgumentException("Starting equipment for combat class $name is invalid")
							}
							val first = arr.remove(0)
								.let { first -> HtmlString(first.asString) }
							equObj.add(first.content, arr)
							val list = HtmlObject("ul")
								.withAll(arr.map { innerItem ->
									HtmlString(innerItem.asString).wrap("li")
								})
							HtmlContent().withContent(first).withContent(list)
						} else {
							baseArr.add(it.asString)
							HtmlString(it.asString)
						}.wrap("li")
					}?.let { HtmlObject("ul").withAll(it) } ?: HtmlString("TODO")
			} else {
				val inv = se.asJsonObject
				val content = HtmlContent()
				if (inv.has("_")) {
					val personalAffects = inv.getAsJsonArray("_")
						.map { HtmlString(it.asString).wrap("li") }
						.let { HtmlObject("ul").withAll(it) }
					content.withContent(personalAffects)
					inv.remove("_")
				}
				inv.entrySet().map {
					content.withContent(HtmlObject("p").withContent(it.key))
					val items = it.value.asJsonArray
						.map { item -> HtmlString(item.asString).wrap("li") }
						.let { items -> HtmlObject("ul").withAll(items) }
					content.withContent(items)
				}
				content
			}

			return CombatClass(
				name,
				altName,
				description,
				backstoryPrompts,
				alignmentSuggestion,
				coreAbilityName,
				coreAbilityDescription,
				limitations,
				levelingBonuses,
				startingEquipment
			)
		}
	}
}