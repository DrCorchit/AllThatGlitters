package net.allthatglitters.server

import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.html.HtmlFile

object Index : HtmlFile("All That Glitters", "index.html", generator.inputDir)