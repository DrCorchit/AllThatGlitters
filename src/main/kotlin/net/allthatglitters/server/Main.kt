package net.allthatglitters.server;

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import java.io.File

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        val app: Javalin = Javalin.create { config: JavalinConfig ->
            //config.compression.gzipOnly();
            val f = File("src/main/resources/generated")
            config.staticFiles.add(f.path, Location.EXTERNAL)
            config.http.gzipOnlyCompression()
        }

        app.start(8080)
    }
}

object Generate {
    @JvmStatic
    fun main(vararg args: String) {
        val outputDir = File("src/main/resources/served")

        val max = 7
        for (i in 1..max) {
            val body = File("src/main/resources/chapters/c$i.html").readText()
            val nav = Navigation(i)
            val builder = Header(i).render()
            builder.append(nav.render(max))
            builder.append("<h2>Chapter $i</h2>")
            builder.append(body)
            builder.append(nav.render(max))
            builder.append(Footer.render())

            val output = File(outputDir, "c$i.html")
            output.writeText(builder.toString())
        }

        val appendix = File("src/main/resources/chapters/appendices.html").readText()
        val nav = Navigation(0)
        val builder = Header(0).render()
        builder.append(nav.render(0))
        builder.append(appendix)
        builder.append(nav.render(0))
        builder.append(Footer.render())

        val output = File(outputDir, "appendices.html")
        output.writeText(builder.toString())
    }
}