package net.allthatglitters.server

import net.allthatglitters.server.concepts.classes.ClassesChapter
import net.allthatglitters.server.concepts.magic.Spells
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.Navigation
import net.allthatglitters.server.util.html.HtmlFile
import java.io.File

object Generate {
    val version = "0"
    val appendices = listOf(
        Spells(),
        HtmlFile("Appendix: Training", "appendix_training.html"),
        HtmlFile("Appendix: Weapons", "appendix_weapons.html"),
        HtmlFile("Appendix: Armor &amp; Materials", "appendix_armor.html"),
        HtmlFile("Appendix: Tools &amp; Consumables", "appendix_items.html"),
        HtmlFile("Appendix: Beastiary", "appendix_beastiary.html"),
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

    @JvmStatic
    fun main(vararg args: String) {
        HtmlFile("All That Glitters", "index.html")
            .appendHeader()
            .appendTitle("h1")
            .appendBody()
            .saveTo(version, "index.html")
        HtmlFile("Player's Handbook", "phb_toc.html")
            .appendHeader()
            .appendElement("h1", "All That Glitters")
            .appendTitle("h2")
            .appendBody()
            .save(version)

        HtmlFile("Character Sheet", "character_sheet.html").appendBody().save(version)

        val max = 7
        for (i in 1..max) {
            makeChapter(i, max)
        }

        for (i in appendices.indices) {
            makeAppendix(i)
        }
    }

    fun makeChapter(i: Int, max: Int) {
        val nav = Navigation.render(i, max)
        val chapter = if (i == 2) {
            ClassesChapter(chapterTitles[1], "c2.html")
        } else {
            HtmlFile(chapterTitles[i - 1], "c$i.html")
        }
        chapter.appendHeader()
            .appendElement("h2", "Chapter $i")
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .append(Collapsible)
            .save(version)
    }

    fun makeAppendix(i: Int) {
        val prev = if (i > 0) {
            appendices[i - 1]
        } else null
        val next = if (i < appendices.size - 1) {
            appendices[i + 1]
        } else null
        val nav = Navigation.render(prev, next)

        appendices[i]
            .appendHeader()
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .save(version)
    }
}

val outputDir = File("src/main/resources/output")
val inputDir = File("src/main/resources/input")