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
            val f = File("src/main/resources")
            config.staticFiles.add(f.path, Location.EXTERNAL)
            config.http.gzipOnlyCompression()
        }

        app.start(8080)
    }
}