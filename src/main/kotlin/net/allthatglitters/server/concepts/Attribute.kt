package net.allthatglitters.server.concepts

enum class Attribute(val fullName: String) {
    STR("Strength"),
    SPD("Speed"),
    DEX("Dexterity"),
    INT("Intelligence"),
    CHA("Charisma"),
    WILL("Willpower");

    companion object {
        fun parse(str: String): Attribute? {
            return entries.firstOrNull { it.name.equals(str, true) }
        }
    }
}