package net.allthatglitters.server.util.html

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.FileSubsection
import net.allthatglitters.server.util.Header
import net.allthatglitters.server.util.Subsection
import java.io.File

open class HtmlFile(val title: String, val fileName: String) {
	open val inputDir = Generator.inputDir
	val outputFile = File(Generator.versionedOutputDir, fileName)

	val head = HtmlObject("head").withContent(HtmlObject("title").withContent(title))
	val body = HtmlObject("body")

//	open fun getHeader(): Renderable {
//		return Header
//	}
//
//	open fun getBody(): Renderable {
//		return HtmlString(File(inputDir, fileName).readText())
//	}

	fun getSubsection(title: String, link: String, prefix: String): Subsection {
		return FileSubsection(this, title, link, prefix)
	}

	fun getSubsection(title: String, link: String, generator: () -> String): Subsection {
		return object : Subsection(this, title, link) {
			override fun render(): String {
				return generator.invoke()
			}
		}
	}



	fun appendSubsection(subsection: Subsection) {
		append(subsection.makeHeader())
		append(subsection)
	}

	fun append(renderable: Renderable): HtmlFile {
		body.withContent(renderable)
		return this
	}

	fun append(string: String): HtmlFile {
		body.withContent(string)
		return this
	}

	fun appendElement(tag: String, text: String): HtmlFile {
		return append(HtmlObject(tag).withContent(text))
	}

	fun appendTitle(tag: String = "h3"): HtmlFile {
		return appendElement(tag, title)
	}

	open fun appendHeader(): HtmlFile {
		head.withContent(Header)
		return this
	}

	open fun appendBody(): HtmlFile {
		return append(File(inputDir, fileName).readText())
	}

	fun render(): String {
		return HtmlObject("html")
			.withAttribute("lang", "en")
			.withContent(head)
			.withContent(body)
			.render()
			.replace("{{version}}", Generator.version)
	}

	fun save() {
		val content = "<!DOCTYPE html>\n" + render()
		outputFile.parentFile.mkdirs()
		val new = outputFile.createNewFile()
		outputFile.writeText(content)
		if (new) println("Created file $outputFile")
		else println("Wrote file $outputFile")
	}

	override fun toString(): String {
		return "$inputDir/$fileName -> $title"
	}
}