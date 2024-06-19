package net.allthatglitters.server.chapters.combat

import com.drcorchit.justice.utils.StringUtils.normalize
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.appendices.magic.AppendixSpells.spells
import net.allthatglitters.server.appendices.magic.Spell
import net.allthatglitters.server.chapters.classes.CharactersChapter.races
import net.allthatglitters.server.util.bold
import net.allthatglitters.server.util.deserialize
import net.allthatglitters.server.util.html.HtmlFile
import net.allthatglitters.server.util.html.HtmlObject
import net.allthatglitters.server.util.html.HtmlTable
import java.io.File

object CombatChapter : HtmlFile(
	"How does my character fight?",
	"c5.html",
	File(generator.inputDir, "chapters/5_combat")
) {

	val damageTypes by lazy {
		File(inputDir, "damage_types.json").deserialize {
			DamageType.deserialize(it)
		}.associateBy { it.name.normalize() }
	}

	fun lookupDamageType(str: String): DamageType {
		return damageTypes[str.normalize()]
			?: throw NoSuchElementException("No such damage type: $str")
	}

	val statusEffects by lazy {
		File(inputDir, "status_effects.json").deserialize {
			StatusEffect.deserialize(it)
		}.associateBy { it.name.normalize() }
	}

	fun lookupStatusEffect(str: String): StatusEffect {
		return statusEffects[str.normalize()]
			?: throw NoSuchElementException("No such status effect: $str")
	}

	override val templatizer = generator.templatizer.extend()
		.withRule("damage_types") {
			val list = HtmlObject("ul")
			damageTypes.values.forEach {
				list.withContent("li", it.toBlurb())
			}
			list.render()
		}
		.withRule("status_effects") {
			val table =
				HtmlTable().withHeaders("Name", "Effect", "Causes", "Recovery Strategy", "Notes")
			statusEffects.values.forEach {
				table.withRow(it.name, it.effect, it.causes, it.recovery, it.notes)
			}
			table.render()
		}

	init {
		addSubsection("The Battlefield", "battlefield")
		addSubsection("Beginning Combat", "beginning")
		addSubsection("Taking Actions", "actions")
		addSubsection("Attack and Defense", "attacking")
		addSubsection("Moving", "moving")
		addSubsection("Grappling", "grappling")
		addSubsection("Defeat", "defeat")
		addSubsection("Damage Types", "damage_types")
		addSubsection("Status Effects", "status_effects")
	}

	override fun appendBody(): HtmlFile {
		append(File(inputDir, "0_intro.html").readText())
		append(getOutline())
		subsections.forEach { appendSubsection(it) }
		return this
	}
}