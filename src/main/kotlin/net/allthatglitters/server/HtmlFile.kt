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

    fun save() {
        builder.append(Footer.render())
        val outputFile = File(outputDir, fileName)
        val new = outputFile.createNewFile()
        outputFile.writeText(builder.toString())
        if (new) println("Created file $outputFile")
        else println("Wrote file $outputFile")
    }

}