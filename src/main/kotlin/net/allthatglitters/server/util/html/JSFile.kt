package net.allthatglitters.server.util.html

import java.io.File

class JSFile(val path: String): Renderable {
	override fun render(): String {
		return File(base, path).readText()
	}

	companion object {
		val base = File("src/main/js")
	}
}
