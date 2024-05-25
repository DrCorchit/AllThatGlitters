package net.allthatglitters.server.concepts.magic

import net.allthatglitters.server.concepts.sheet.Attribute
import net.allthatglitters.server.concepts.sheet.Skill

enum class School(val adjective: String, val primaryAttr: Attribute) {
    //Medicine, Literacy
    Alchemy("alchemical", Attribute.INT),
    //Literacy, Sorcery
    Astrology("astrological", Attribute.INT),
    //Nature
    Biomancy("biological", Attribute.CHA),
    //Persuasion
    Conjuration("conjuration", Attribute.CHA),
    //Sorcery
    Elementurgy("elemental", Attribute.WILL),
    //
    Psionics("psychic", Attribute.WILL);


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
}