package net.allthatglitters.server.util.html

import net.allthatglitters.server.util.Footer
import net.allthatglitters.server.util.Header
import java.io.File

val inputDir = File("src/main/resources/input")
val outputDir = File("src/main/resources/output")

open class HtmlFile(val title: String, val fileName: String) {

    private val builder = StringBuilder()

    fun appendHeader(): HtmlFile {
        builder.append(Header.render())
        return this
    }

    fun appendFooter(): HtmlFile {
        builder.append(Footer.render())
        return this
    }

    fun append(html: HtmlObject): HtmlFile {
        return append(html.render())
    }

    fun append(builder: StringBuilder): HtmlFile {
        return append(builder.toString())
    }

    fun append(string: String): HtmlFile {
        builder.append(string)
        return this
    }

    fun appendElement(tag: String, text: String): HtmlFile {
        builder.append("<$tag>$text</$tag>\n")
        return this
    }

    fun appendTitle(tag: String = "h3"): HtmlFile {
        return appendElement(tag, title)
    }

    open fun appendBody(): HtmlFile {
        builder.append(File(inputDir, fileName).readText())
        return this
    }

    fun save(version: String) {
        saveTo(version, "version/$version/$fileName")
    }

    fun saveTo( version: String, destination: String) {
        val content = builder.toString()
            .replace("{{version}}", version)
        val outputFile = File(outputDir, destination)
        outputFile.parentFile.mkdirs()
        val new = outputFile.createNewFile()
        outputFile.writeText(content)
        if (new) println("Created file $outputFile")
        else println("Wrote file $outputFile")
    }
}