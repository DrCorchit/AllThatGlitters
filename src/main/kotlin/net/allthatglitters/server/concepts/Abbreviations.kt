package net.allthatglitters.server.concepts

enum class Abbreviations(val fullName: String) {
    DM("Game Master"),
    Player("Player"),
    NPC("NPC"),
    PC("Character"),

    HP("HP"),
    MAX_HP("Maximum HP"),
    AP("AP"),
    MAX_AP("Maximum AP"),
    BC("Block Chance"),
    DC("Dodge Chance"),
    Parry("Parry Chance"),
    MIT("Mitigation"),

    CT("Critical Threshold"),
    DT("Difficulty Threshold"),
    ATTR("Attribute"),

    WEIGHT_UNITS("pounds"),
    DISTANCE_UNITS("ft"),
    SPEED_UNITS("ft/s"),

    //Not Abbreviated
    SKILL("Skill"),
    EVENT("Event Roll"),
    ABILITY("Ability"),
    SLOT("Training Slots")
}