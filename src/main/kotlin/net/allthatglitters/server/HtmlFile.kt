package net.allthatglitters.server

import java.io.File

class HtmlFile(val fileName: String, val title: String) {

    private val text = File(inputDir, fileName).readText()
    private val builder = StringBuilder()

    init {
        builder.append(Header.render())
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

    fun appendBody(): HtmlFile {
        builder.append(text)
        return this
    }

    fun save(version: String) {
        saveTo(version, "version/$version/$fileName")
    }

    fun saveTo( version: String, destination: String) {
        val content = builder.append(Footer.render()).toString()
            .replace("{{version}}", version)
        val outputFile = File(outputDir, destination)
        outputFile.parentFile.mkdirs()
        val new = outputFile.createNewFile()
        outputFile.writeText(content)
        if (new) println("Created file $outputFile")
        else println("Wrote file $outputFile")
    }
}