package net.allthatglitters.server;

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import java.io.File

val inputDir = File("src/main/resources/input")
val outputDir = File("src/main/resources/output")

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        val app: Javalin = Javalin.create { config: JavalinConfig ->
            //config.compression.gzipOnly();
            config.staticFiles.add(outputDir.path, Location.EXTERNAL)
            config.http.gzipOnlyCompression()
        }

        app.start(8080)
    }
}

object Generate {
    @JvmStatic
    fun main(vararg args: String) {
        HtmlFile("index.html").appendBody().save()
        HtmlFile("phb_toc.html").appendBody().save()
        val nav0 = Navigation(0).render(0)
        HtmlFile("appendices.html").append(nav0).appendBody().append(nav0).save()

        val max = 7
        for (i in 1..max) {
            val chapter = HtmlFile("c$i.html")
            val nav = Navigation(i)
            chapter.append(nav.render(max)).append("<h2>Chapter $i</h2>").appendBody().append(nav.render(max))
            if (i == 2 || i == 7) {
                chapter.append(Collapsible.render())
            }
            chapter.append(Footer.render()).save()
        }
    }
}