package net.allthatglitters.server;

import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.staticfiles.Location
import java.io.File

val inputDir = File("src/main/resources/input")
val outputDir = File("src/main/resources/output")
val appendices = listOf(
    "appendix_spells.html" to "Appendix: Spells",
    "appendix_training.html" to "Appendix: Training",
    "appendix_weapons.html" to "Appendix: Weapons",
    "appendix_armor.html" to "Appendix: Armor &amp; Materials",
    "appendix_items.html" to "Appendix: Tools &amp; Consumables",
    "appendix_beastiary.html" to "Appendix: Beastiary",
)

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
        HtmlFile("index.html", "All That Glitters").appendBody().save()
        HtmlFile("phb_toc.html", "Player's Handbook").append("<h1>All That Glitters</h1>").appendTitle("h2").appendBody().save()

        val max = 7
        for (i in 1..max) {
            val chapter = HtmlFile("c$i.html", "Chapter $i")
            val nav = Navigation.render(i, max)
            chapter.appendTitle("h2").append(nav).appendBody().append(nav)
            if (i == 2 || i == 7) {
                chapter.append(Collapsible.render())
            }
            chapter.append(Footer.render()).save()
        }

        for (i in appendices.indices) {
            val prev = if (i > 0) {
                appendices[i - 1]
            } else null
            val next = if (i < appendices.size - 1) {
                appendices[i + 1]
            } else null
            val nav = Navigation.render(prev, next)
            HtmlFile(appendices[i].first, appendices[i].second).appendTitle().append(nav).appendBody().append(nav).save()
        }
    }
}