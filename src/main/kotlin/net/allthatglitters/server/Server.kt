package net.allthatglitters.server;

import com.drcorchit.justice.utils.logging.Logger
import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import java.io.File
import java.util.*

object Server {
	val logger = Logger.getLogger(Server::class.java)
	val serviceDir = File("src/main/resources/output")

	@JvmStatic
	fun main(vararg args: String) {
		val port = args[0].toInt()
		val app: Javalin = Javalin.create { config: JavalinConfig ->
			//config.compression.gzipOnly();
			config.staticFiles.add(serviceDir.path, Location.EXTERNAL)
			config.http.gzipOnlyCompression()
			config.events.serverStarted { logger.info("Started server at ${Date()}") }
			config.events.serverStopped { logger.info("Stopped server at ${Date()}") }
		}

		app.start(port)
	}
}

