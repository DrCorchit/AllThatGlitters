package net.allthatglitters.server

import java.io.File

class HtmlFile(val fileName: String) {

    val text = File(inputDir, fileName).readText()
    val builder = StringBuilder()
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

    fun appendBody(): HtmlFile {
        builder.append(text)
        return this
    }



    fun save() {
        builder.append(Footer.render())
        val outputFile = File(outputDir, fileName)
        outputFile.createNewFile()
        outputFile.writeText(builder.toString())
    }

}