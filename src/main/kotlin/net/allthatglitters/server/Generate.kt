package net.allthatglitters.server

import net.allthatglitters.server.appendices.Spells
import net.allthatglitters.server.util.Collapsible
import net.allthatglitters.server.util.HtmlFile
import net.allthatglitters.server.util.Navigation

object Generate {
    val version = "0.1"
    val appendices = listOf(
        Spells("spells/psionics.json"),
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
        HtmlFile("index.html", "All That Glitters")
            .appendHeader()
            .appendTitle("h1")
            .appendBody()
            .appendFooter()
            .saveTo(version, "index.html")
        HtmlFile("phb_toc.html", "Player's Handbook")
            .appendHeader()
            .appendElement("h1", "All That Glitters")
            .appendTitle("h2")
            .appendBody()
            .appendFooter()
            .save(version)

        HtmlFile("character_sheet.html", "Character Sheet").appendBody().save(version)

        val max = 7
        for (i in 1..max) {
            val nav = Navigation.render(i, max)
            HtmlFile("c$i.html", chapterTitles[i - 1])
                .appendHeader()
                .appendElement("h2", "Chapter $i")
                .appendTitle().append(nav)
                .appendBody().append(nav)
                .append(Collapsible.render())
                .appendFooter()
                .save(version)
        }

        for (i in appendices.indices) {
            makeAppendix(i)
        }
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
            .appendFooter()
            .save(version)
    }
}