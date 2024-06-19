package net.allthatglitters.server.concepts

import net.allthatglitters.server.util.html.HtmlObject

enum class Abbreviations(val fullName: String) {
    DM("Storyteller"),
    P("Player"),
    NPC("NPC"),
    PC("Character"),

    HP("HP"),
    MAX_HP("Maximum HP"),
    WP("willpower"),
    MAX_WP("Maximum Willpower"),
    BC("Block Chance"),
    DC("Dodge Chance"),
    Parry("Parry Chance"),
    MIT("Mitigation"),
    CT("Critical Threshold"),
    DT("Difficulty Threshold");

    fun getReplacement(): String {
        return HtmlObject("span")
            .withContent(fullName)
            .withAttribute("data-tooltip", "TODO")
            .render()
    }
}