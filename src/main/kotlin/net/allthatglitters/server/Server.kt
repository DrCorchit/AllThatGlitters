package net.allthatglitters.server;

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import java.io.File

object Server {
    val serviceDir = File("src/main/resources/output")

    @JvmStatic
    fun main(vararg args: String) {
        val port = args[0].toInt()
        val app: Javalin = Javalin.create { config: JavalinConfig ->
            //config.compression.gzipOnly();
            config.staticFiles.add(serviceDir.path, Location.EXTERNAL)
            config.http.gzipOnlyCompression()
        }

        app.start(port)
    }
}

