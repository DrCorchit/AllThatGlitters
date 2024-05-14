package net.allthatglitters.server;

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import net.allthatglitters.server.util.HtmlFile

object Serve {
    @JvmStatic
    fun main(vararg args: String) {
        val app: Javalin = Javalin.create { config: JavalinConfig ->
            //config.compression.gzipOnly();
            config.staticFiles.add(HtmlFile.outputDir.path, Location.EXTERNAL)
            config.http.gzipOnlyCompression()
        }

        app.start(8080)
    }
}

