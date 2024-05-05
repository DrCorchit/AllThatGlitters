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

val chapterTitles = listOf(
    "How do I play the game?",
    "How do I create a character?",
    "What's on my character sheet?",
    "How does my character become stronger?",
    "How does my character fight?",
    "What can my character do besides fighting?",
    "How does my character use magic?"
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
        HtmlFile("index.html", "All That Glitters")
            .appendTitle("h1").appendBody()
            .save()
        HtmlFile("phb_toc.html", "Player's Handbook")
            .appendElement("h1", "All That Glitters")
            .appendTitle("h2").appendBody()
            .save()

        val max = 7
        for (i in 1..max) {
            val nav = Navigation.render(i, max)
            HtmlFile("c$i.html", chapterTitles[i - 1])
                .appendElement("h2", "Chapter $i")
                .appendTitle().append(nav)
                .appendBody().append(nav)
                .append(Collapsible.render()).save()
        }

        for (i in appendices.indices) {
            val prev = if (i > 0) {
                appendices[i - 1]
            } else null
            val next = if (i < appendices.size - 1) {
                appendices[i + 1]
            } else null
            val nav = Navigation.render(prev, next)
            HtmlFile(appendices[i].first, appendices[i].second)
                .appendTitle().append(nav)
                .appendBody().append(nav).save()
        }
    }
}