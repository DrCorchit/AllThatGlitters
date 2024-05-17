package net.allthatglitters.server.concepts.magic

enum class Type(val label: String) {
    Evocation("evocation"),
    Concentration("concentration spell"),
    Ritual("ritual"),
    Ceremony("ceremony"),
    Enchantment("enchantment");
}