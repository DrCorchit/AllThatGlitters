package net.allthatglitters.server;

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import net.allthatglitters.server.util.outputDir

object Serve {
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

