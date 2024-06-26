package net.allthatglitters.server.util

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.html.Renderable
import java.io.File

private val script = File(generator.inputDir, "collapsible.html").readText()

object Collapsible: Renderable {
    override fun render(): String {
        return script
    }
}