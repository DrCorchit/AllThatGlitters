package net.allthatglitters.server

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject

enum class Rarity {
    Common, Scholastic, Esoteric, Forbidden
}

enum class School(adjective: String) {
    Alchemy("alchemic"),
    Astrology("astrological"),
    Biomancy("biological"),
    Conjuration("conjuration"),
    Enchantment("enchantment"),
    Elementurgy("elemental"),
    Necromancy("necromantic"),
    Psionics("psychic");

    companion object {
        fun deserialize(ele: JsonElement): School {
            val str = ele.asString
            return entries.first { str.equals(it.name, true) }
        }
    }
}

enum class Type {
    Evocation, Concentration, Ritual, Ceremony
}

class Spell(
    val rarity: Rarity,
    val school: School,
    val type: Type,
    val effect: String,
    val duration: String,
    val range: String,
    val trainingReqs: ImmutableMap<String, String>,
    val castingReqs: ImmutableMap<String, String>
) {

    companion object {
        fun deserialize(info: JsonObject): Spell {
            val rarity = info.get("rarity").let { Rarity.valueOf(it.asString) }
            val school = info.get("school").let { School.valueOf(it.asString) }
            val type = info.get("type").let { Type.valueOf(it.asString) }
            val effect = info.get("effect").asString
            val duration = info.get("duration").asString
            val range = info.get("range").asString
            val trainingReqs = info.getAsJsonObject("trainingReqs")
                .entrySet().stream()
                .collect(ImmutableMap.toImmutableMap({ key -> key.key }, { value -> value.value.toString() }))
            val castingReqs = info.getAsJsonObject("castingReqs")
                .entrySet().stream()
                .collect(ImmutableMap.toImmutableMap({ key -> key.key }, { value -> value.value.toString() }))

            return Spell(rarity, school, type, effect, duration, range, trainingReqs, castingReqs)
        }
    }
}