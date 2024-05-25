package net.allthatglitters.server.concepts.magic

import net.allthatglitters.server.concepts.sheet.Attribute
import net.allthatglitters.server.concepts.sheet.Skill

enum class School(val adjective: String, val primaryAttr: Attribute, skillName: String) {
    Alchemy("alchemical", Attribute.INT, "alchemy"),
    Astrology("astrological", Attribute.INT, "literacy"),
    Biomancy("biological", Attribute.CHA, "nature"),
    Conjuration("conjuration", Attribute.CHA, "devotion"),
    Sorcery("elemental", Attribute.WILL, "sorcery"),
    Psionics("psychic", Attribute.WILL, "psyche");

    val skill = Skill.parse(skillName)
}

enum class Discipline(val school: School, val adjective: String) {
    Pharmacology(School.Alchemy, "beneficial"),
    Toxicology(School.Alchemy, "toxic"),

    Solar(School.Astrology, "solar"),
    Lunar(School.Astrology, "lunar"),
    Planetary(School.Astrology, "planetary"),

    Weal(School.Biomancy, "weal"),
    Wild(School.Biomancy, "wild"),
    Wyrd(School.Biomancy, "wyrd"),

    Invocation(School.Conjuration, "summoning"),
    Divination(School.Conjuration, "divination"),
    Necromancy(School.Conjuration, "necromantic"),

    Fire(School.Sorcery, "pyrokinetic"),
    Earth(School.Sorcery, "geokinetic"),
    Water(School.Sorcery, "hydrokinetic"),
    Air(School.Sorcery, "aerokinetic"),

    Illusion(School.Psionics, "illusion"),
    Telepathy(School.Psionics, "telepathic"),
    Telekinetics(School.Psionics, "telekinetic");

    fun describe(rarity: Rarity, type: Type): String {
        return when (this) {
            Pharmacology -> "$rarity potion"
            Toxicology -> "$rarity toxin"
            Solar, Lunar, Planetary -> "$rarity $adjective ${school.adjective} ${type.label}"
            Fire, Earth, Water, Air -> "$rarity ${school.adjective} ${type.label}"
            else -> "$rarity $adjective ${type.label}"
        }
    }
}