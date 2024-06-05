package net.allthatglitters.server.util.html

import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import net.allthatglitters.server.Generator.Companion.generator
import net.allthatglitters.server.util.FileSubsection
import net.allthatglitters.server.util.Header
import net.allthatglitters.server.util.Subsection
import net.allthatglitters.server.util.Templatizer
import org.jsoup.Jsoup
import java.io.File

abstract class HtmlFile(val title: String, val fileName: String, val inputDir: File) {
	open val logger = Logger.getLogger(HtmlFile::class.java)
	val outputFile = File(generator.versionedOutputDir, fileName)
	open val templatizer: Templatizer = generator.templatizer

	val subsections = mutableListOf<Subsection>()
	val head = HtmlObject("head").withContent(HtmlObject("title").withContent(title))
	val body = HtmlObject("body")

	fun getLink(text: String = title): HtmlObject {
		return HtmlObject("a").withAttribute("href", fileName)
			.withContent(text)
	}

	fun addSubsection(title: String, link: String = title.normalize()): HtmlFile {
		val index = subsections.size + 1
		subsections.add(FileSubsection(this, title, link, index.toString()))
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
		logger.info("Rendering $fileName")
		return "<!DOCTYPE html>\n" + HtmlObject("html")
			.withAttribute("lang", "en")
			.withContent(head)
			.withContent(body)
			.render()
			.let {
				try {
					templatizer.replace(it)
				} catch (e: Exception) {
					throw IllegalArgumentException("Error templatizing file: $outputFile", e)
				}
			}
	}

	fun save(outputFile: File = this.outputFile) {
		val content = render()
		val parsed = Jsoup.parse(content)
		val pretty = parsed.toString()

		outputFile.parentFile.mkdirs()
		val new = outputFile.createNewFile()
		outputFile.writeText(pretty)
		if (new) logger.info("Created file $outputFile")
		else logger.info("Wrote file $outputFile")
	}

	override fun toString(): String {
		return "$inputDir/$fileName -> $title"
	}
}