package net.allthatglitters.server.chapters.classes

import com.drcorchit.justice.utils.StringUtils.parseEnum
import net.allthatglitters.server.util.HasProperties
import net.allthatglitters.server.util.html.HtmlObject

enum class Size(
	val maxHeight: Int,
	val tileSize: Int,
	val terminalVelocity: Int,
	val notes: String
) : HasProperties {
	Tiny(2, 0, 45, "May only wield small weapons (i.e. daggers)."),

	Small(
		4,
		1,
		100,
		"May not wield great weapons, and versatile weapons may only be held in a two-handed grip."
	),
	Medium(8, 1, 180, ""),
	Large(16, 2, 300, "May wield 2H weapons in one hand."),
	Huge(32, 3, 500, "May wield 2H weapons in one hand. Unable to wield small or medium weapons."),
	Enormous(999, 4, 999, "May not wield ordinary weapons.");

	fun toRow(): HtmlObject {
		return HtmlObject("tr")
			.withContent("td", name)
			.withContent("td", "$maxHeight feet")
			.withContent("td", "${tileSize}x$tileSize")
			.withContent("td", "$terminalVelocity ft/s")
			.withContent("td", notes)
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"name" -> name
			"max_height" -> maxHeight
			"size" -> tileSize
			"terminal_velocity" -> terminalVelocity
			"notes" -> notes
			else -> null
		}
	}

	companion object : HasProperties {
		override fun getProperty(property: String): Any? {
			return property.parseEnum<Size>()
		}
	}
}