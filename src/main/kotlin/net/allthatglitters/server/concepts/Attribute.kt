package net.allthatglitters.server.concepts

enum class Attribute(val fullName: String) {
    STR("Strength"),
    SPD("Speed"),
    DEX("Dexterity"),
    INT("Intelligence"),
    CHA("Charisma"),
    WILL("Willpower");

    fun replace(string: String): String {
        return if (name.equals(string, true)) fullName else string
    }

    companion object {
        fun replaceAll(string: String): String {
            return entries.fold(string) { str, attr -> attr.replace(str) }
        }
    }
}