package net.allthatglitters.server.util.html

import net.allthatglitters.server.Generator
import net.allthatglitters.server.util.FileSubsection
import net.allthatglitters.server.util.Header
import net.allthatglitters.server.util.Subsection
import net.allthatglitters.server.util.Templatizer
import java.io.File

open class HtmlFile(val title: String, val fileName: String) {
	open val inputDir = Generator.inputDir
	val outputFile = File(Generator.versionedOutputDir, fileName)
	open val templatizer: Templatizer = Generator.templatizer

	val subsections = mutableListOf<Subsection>()
	val head = HtmlObject("head").withContent(HtmlObject("title").withContent(title))
	val body = HtmlObject("body")

	fun getLink(): HtmlObject {
		return HtmlObject("a").withAttribute("href", fileName)
			.withContent(title)
	}

	fun addFileSubsection(title: String, link: String): HtmlFile {
		val index = subsections.size + 1
		return addCustomSubsection(FileSubsection(this, title, link, index.toString()))
	}

	fun addCustomSubsection(subsection: Subsection): HtmlFile {
		subsections.add(subsection)
		return this
	}

	fun getOutline(): HtmlObject {
		val list = HtmlObject("ol")
		subsections.forEach { list.withContent(HtmlObject("li").withContent(it.linkTo())) }
		return list
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
		if (subsections.isEmpty()) {
			append(File(inputDir, fileName).readText())
		} else {
			//append(getOutline())
			subsections.forEach { appendSubsection(it) }
		}
		return this
	}

	fun render(): String {
		return "<!DOCTYPE html>\n" + HtmlObject("html")
			.withAttribute("lang", "en")
			.withContent(head)
			.withContent(body)
			.render()
			.let { templatizer.replace(it) }
	}

	fun save(outputFile: File = this.outputFile) {
		val content = render()
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