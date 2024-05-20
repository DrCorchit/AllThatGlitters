package net.allthatglitters.server.util

import net.allthatglitters.server.inputDir
import net.allthatglitters.server.util.html.Renderable
import java.io.File

object Header: Renderable {
    val header = File(inputDir, "header.html").readText()

    override fun render(): String {
        return header
    }
}