package net.allthatglitters.server.appendices

enum class Attribute(val fullName: String) {
    STR("Strength"),
    SPD("Speed"),
    DEX("Dexterity"),
    INT("Intelligence"),
    CHA("Charisma"),
    WILL("Willpower");

    fun replace(string: String): String {
        return if (fullName.equals(string, true)) fullName else string
    }

    companion object {
        fun replaceAll(string: String): String {
            return entries.fold(string) { str, attr -> attr.replace(str) }
        }
    }
}