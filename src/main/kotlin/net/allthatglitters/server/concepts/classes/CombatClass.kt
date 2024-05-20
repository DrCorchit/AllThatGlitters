package net.allthatglitters.server.concepts.classes

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
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
        val output = HtmlObject("div").withAttribute("class", "background")

        val nameHtml = HtmlObject("h5")
            .withContent(HtmlObject("a").withAttribute("id", name))
            .withContent(name)
        if (altName != null) nameHtml.withContent("($altName)")
        output.withContent(nameHtml)
        output.withContent(description.wrapRaw("p"))
        output.withContent(
            HtmlObject("p")
                .withContent("While writing the backstory for an ${name.lowercase()}, consider the following:")
        )
        val prompts = HtmlObject("ol")
            .withAll(backstoryPrompts.map { HtmlObject("li").withContent(it) })
        output.withContent(prompts)

        if (alignmentSuggestion != null) {
            output.withBoldedEntry("Alignment Suggestion", alignmentSuggestion)
        }

        val p = HtmlObject.boldedEntry("p", "Core Ability", "${coreAbilityName.italicise()}. ")
        coreAbilityDescription.preface(p).wrapRaw("p")
        output.withContent(coreAbilityDescription)

        if (limitations != null) {
            output.withBoldedEntry("Limitations", limitations)
        }

        val outerDiv = HtmlObject("div").withAttribute("class", "background-2")
        val button = HtmlObject("button").withAttribute("class", "collapsible-2")
            .withContent("Additional Information")
        val innerDiv = HtmlObject("div").withAttribute("class", "content")

        innerDiv.withBoldedEntry("Leveling Bonuses", "")
        levelingBonuses.forEach {
            val p = HtmlObject("p").withContent("At level ${it.key}, ")
            innerDiv.withContent(it.value.preface(p))
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
            val startingEquipment = obj.getAsJsonArray("startingEquipment")
                ?.map {
                    if (it.isJsonArray) {
                        val first = it.asJsonArray.remove(0)
                            .let { first -> HtmlString(first.asString) }
                        val list = HtmlObject("ul")
                            .withAll(it.asJsonArray.map { innerItem ->
                                HtmlString(innerItem.asString).wrap("li")
                            })
                        HtmlContent().withContent(first).withContent(list)
                    } else {
                        HtmlString(it.asString)
                    }.wrap("li")
                }?.let { HtmlObject("ul").withAll(it) } ?: HtmlString("TODO")
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