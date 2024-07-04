package net.allthatglitters.server

import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.StringUtils.parseEnum
import com.drcorchit.justice.utils.logging.Logger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParser
import net.allthatglitters.server.appendices.abilities.Ability
import net.allthatglitters.server.appendices.abilities.AppendixAbilities
import net.allthatglitters.server.appendices.armor.AppendixArmor
import net.allthatglitters.server.appendices.armor.Armor
import net.allthatglitters.server.appendices.bestiary.AppendixBestiary
import net.allthatglitters.server.appendices.bestiary.Phylum
import net.allthatglitters.server.appendices.items.AppendixItems
import net.allthatglitters.server.appendices.items.Item
import net.allthatglitters.server.appendices.magic.AppendixSpells
import net.allthatglitters.server.appendices.magic.Spell
import net.allthatglitters.server.appendices.weapons.AppendixWeapons
import net.allthatglitters.server.appendices.weapons.Keyword
import net.allthatglitters.server.appendices.weapons.Weapon
import net.allthatglitters.server.chapters.classes.CharactersChapter
import net.allthatglitters.server.chapters.classes.Size
import net.allthatglitters.server.chapters.combat.CombatChapter
import net.allthatglitters.server.chapters.combat.DamageType
import net.allthatglitters.server.chapters.combat.StatusEffect
import net.allthatglitters.server.chapters.intro.IntroChapter
import net.allthatglitters.server.chapters.leveling.LevelingChapter
import net.allthatglitters.server.chapters.magic.MagicChapter
import net.allthatglitters.server.chapters.noncombat.NonCombatChapter
import net.allthatglitters.server.chapters.sheet.*
import net.allthatglitters.server.concepts.Abbreviation
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.Navigation
import net.allthatglitters.server.util.Templatizer
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File
import java.io.FileNotFoundException

class Generator(val version: String, val inputDir: File, val outputDir: File) : HasProperties {
	val versionedOutputDir = File(outputDir, "version/$version")
	val imagesDir = File(outputDir, "images")
	val strings: Map<String, String> =
		File(inputDir, "strings.json").readText()
			.let { JsonParser.parseString(it).asJsonObject }
			.entrySet().associate { it.key.normalize() to it.value.asString }

	val deserializer: Gson
	val templatizer = Templatizer.getDefault()
		.withRule(".*\\.html") {
			val file = File(inputDir, it.value.trim())
			if (file.exists()) {
				logger.info("  Inserting file: ${file.name}")
				file.readText()
			} else {
				throw FileNotFoundException("Substitution file not found: ${file.absolutePath}")
			}
		}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"version" -> version
			"strings" -> object : HasProperties {
				override fun getProperty(property: String): Any? {
					return strings[property]
				}
			}
			"files" -> object : HasProperties {
				override fun getProperty(property: String): Any {
					return lookupFile(property)
				}
			}
			"attr" -> Attribute.Companion
			"skills" -> Skill.Companion
			"size" -> Size.Companion
			"armor" -> Armor.Companion
			"items" -> Item.Companion
			"weapons" -> Weapon.Companion
			"weapon_keywords" -> Keyword.Companion
			"damage" -> DamageType.Companion
			"status", "status_effects" -> StatusEffect.Companion
			"spells" -> Spell.Companion
			"abilities" -> Ability.Companion
			"maneuvers" -> Ability.Companion
			"feats" -> Ability.Companion
			else -> try {
				property.parseEnum<Abbreviation>()
			} catch (e : Exception) {
				null
			}
		}
	}

	init {
		net.allthatglitters.server.concepts.round

		val builder = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
		builder.registerTypeAdapter(
			Phylum::class.java,
			JsonDeserializer { json, _, _ -> Phylum(json.asJsonObject) })
		deserializer = builder.create()
	}

	val appendices by lazy {
		listOf(
			AppendixSpells,
			AppendixAbilities,
			AppendixWeapons,
			AppendixArmor,
			AppendixItems,
			AppendixBestiary
		)
	}

	val chapters by lazy {
		listOf(
			IntroChapter,
			CharactersChapter,
			SheetChapter,
			LevelingChapter,
			CombatChapter,
			NonCombatChapter,
			MagicChapter
		)
	}

	private val files by lazy { (appendices + chapters).associateBy { it.fileName } }

	fun lookupFile(file: String): HtmlFile {
		val key = file.normalize() + ".html"
		return files[key] ?: throw NoSuchElementException("No file named \"$file.html\"")
	}

	fun copyFiles() {
		imagesDir.mkdir()
		File(inputDir, "images").listFiles()!!.forEach {
			it.copyTo(File(imagesDir, it.name), true)
		}
		logger.info("Copied images")

		File(inputDir, "styles.css")
			.copyTo(File(Server.serviceDir, "styles.css"), true)
		logger.info("Copied styles.css")
	}

	fun makeSheet() {
		File(Sheet.inputDir, "styles.css")
			.copyTo(File(versionedOutputDir, "sheet/styles.css"), true)
		logger.info("Copied sheet/styles.css")

		Sheet.appendHeader().appendBody().save()
		logger.info("Rendered character sheet")

		Sheet2.appendHeader().appendBody().save()
		logger.info("Rendered character sheet 2")
	}

	fun makeChapter(i: Int) {
		val nav = Navigation.forChapter(i)
		chapters[i - 1].appendHeader()
			.appendElement("h2", "Chapter $i")
			.appendTitle().append(nav)
			.appendBody().append(nav)
			.appendScripts()
			.save()
	}

	fun makeAppendix(i: Int) {
		val prev = if (i > 0) {
			appendices[i - 1]
		} else null
		val next = if (i < appendices.size - 1) {
			appendices[i + 1]
		} else null
		val nav = Navigation(
			prev?.let { it.title to it.fileName },
			next?.let { it.title to it.fileName })

		appendices[i]
			.appendHeader()
			.appendTitle().append(nav)
			.appendBody().append(nav)
			.appendScripts()
			.save()
	}

	fun generate() {
		copyFiles()

		makeSheet()

		Index.appendHeader()
			.appendTitle("h1")
			.appendBody()
			.save(File(Server.serviceDir, "index.html"))

		ToC.appendHeader()
			.appendElement("h1", "All That Glitters")
			.appendTitle("h2")
			.appendBody()
			.save()

		for (i in 1..chapters.size) makeChapter(i)
		for (i in appendices.indices) makeAppendix(i)
	}

	override fun toString(): String {
		return "Generator $inputDir --> $outputDir"
	}

	companion object {
		val logger = Logger.getLogger(Generator::class.java)
		val generator = Generator("0", File("src/main/resources/input"), Server.serviceDir)

		@JvmStatic
		fun main(vararg args: String) {
			generator.generate()
		}
	}
}