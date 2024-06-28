package net.allthatglitters.server.appendices.magic

import net.allthatglitters.server.chapters.sheet.Attribute

enum class School(val adjective: String, val primaryAttr: Attribute) {
    Alchemy("alchemical", Attribute.INT),
    Astrology("astrological", Attribute.INT),
    Biomancy("biological", Attribute.NST),
    Psionics("psychic", Attribute.NST),
    Conjuration("conjuration", Attribute.CHA),
    Elementurgy("elemental", Attribute.CHA),
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

    Fire(School.Elementurgy, "pyrokinetic"),
    Earth(School.Elementurgy, "geokinetic"),
    Water(School.Elementurgy, "hydrokinetic"),
    Air(School.Elementurgy, "aerokinetic"),

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

    companion object {
        fun parse(input: String): Discipline {
            return entries.first { it.name.equals(input, true) }
        }
    }
}