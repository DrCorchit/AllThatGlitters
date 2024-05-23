package net.allthatglitters.server;

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location

object Serve {
    @JvmStatic
    fun main(vararg args: String) {
        val port = args[0].toInt()
        val app: Javalin = Javalin.create { config: JavalinConfig ->
            //config.compression.gzipOnly();
            config.staticFiles.add(Generator.outputDir.path, Location.EXTERNAL)
            config.http.gzipOnlyCompression()
        }

        app.start(port)
    }
}

