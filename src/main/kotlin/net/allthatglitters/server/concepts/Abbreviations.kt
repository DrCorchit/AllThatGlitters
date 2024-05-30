package net.allthatglitters.server.concepts

import net.allthatglitters.server.util.html.HtmlObject

enum class Abbreviations(val fullName: String) {
    DM("Storyteller"),
    Player("Player"),
    NPC("NPC"),
    PC("Character"),

    HP("HP"),
    MAX_HP("Maximum HP"),
    AP("wp"),
    MAX_AP("Maximum AP"),
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