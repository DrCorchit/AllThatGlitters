package net.allthatglitters.server.util.html

import net.allthatglitters.server.inputDir
import net.allthatglitters.server.outputDir
import net.allthatglitters.server.util.Header
import java.io.File

open class HtmlFile(val title: String, val fileName: String) {

    val head = HtmlObject("head").withContent(HtmlObject("title").withContent(title))
    val body = HtmlObject("body")

    fun appendHeader(): HtmlFile {
        head.withContent(Header)
        return this
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

    open fun appendBody(): HtmlFile {
        val text = File(inputDir, fileName).readText()
        return append(text)
    }

    fun render(version: String): String {
        return HtmlObject("html")
            .withAttribute("lang", "en")
            .withContent(head)
            .withContent(body)
            .render()
            .replace("{{version}}", version)
    }

    fun save(version: String) {
        saveTo(version, "version/$version/$fileName")
    }

    fun saveTo(version: String, destination: String) {
        val content = "<!DOCTYPE html>\n" + render(version)
        val outputFile = File(outputDir, destination)
        outputFile.parentFile.mkdirs()
        val new = outputFile.createNewFile()
        outputFile.writeText(content)
        if (new) println("Created file $outputFile")
        else println("Wrote file $outputFile")
    }
}