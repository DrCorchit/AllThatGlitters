package net.allthatglitters.server.appendices.magic

import net.allthatglitters.server.chapters.sheet.Attribute

enum class School(val adjective: String, val primaryAttr: Attribute) {
    Alchemy("alchemical", Attribute.INT),
    Astrology("astrological", Attribute.INT),
    Biomancy("natural", Attribute.NST),
    Psionics("psychic", Attribute.CHA),
    Conjuration("conjuration", Attribute.CHA),
    Elementurgy("elemental", Attribute.NST),
}

enum class College(val school: School, val adjective: String) {
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
    Telekinesis(School.Psionics, "telekinetic");

    companion object {
        fun parse(input: String): College {
            return entries.first { it.name.equals(input, true) }
        }
    }
}