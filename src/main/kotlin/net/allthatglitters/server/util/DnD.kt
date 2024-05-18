package net.allthatglitters.server.util

import com.drcorchit.justice.utils.json.JsonUtils.deserializeEnum
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.File

class DnD(file: File) {
    val spells = file.readText().let { JsonParser.parseString(it) }
        .asJsonObject.getAsJsonArray("spell")
        .map { Spell.deserialize(it) }

    fun print() {
        val spellLevel = 1
        val spellsBySchool = spells.groupBy { it.school }
        val spellCounts = spellsBySchool.mapValues { entry ->
            val sorted = entry.value.groupBy { it.level }
                .mapValues { it.value.size }
                .toSortedMap()
            (0..9).forEach { i -> sorted.computeIfAbsent(i) { 0 } }
            sorted
        }

        //show all level N spells (grouped by school) with description
        spellsBySchool.map { entry ->
            println(entry.key)
            entry.value.filter { it.level == spellLevel }.forEach {
                val desc = it.effect.substring(0, it.effect.length.coerceAtMost(150))
                println(String.format("%40s --- %s", it.name, desc))
            }
        }

        //Show all level N spells sorted alphabetically
        spells.filter { it.level == spellLevel }
            .sortedBy { s1 -> s1.name }
            .forEach { println(String.format("%-40s", it.name)) }

        val str = StringBuilder()
        str.append("              |  0|  1|  2|  3|  4|  5|  6|  7|  8|  9|Total\n")
        spellCounts.forEach { (school, counts) ->
            var schoolTotal = 0
            str.append(String.format("%14s", school.name))
            counts.forEach { (_, count) ->
                schoolTotal += count
                str.append(String.format("% 4d", count))
            }
            str.append(String.format("%6d\n", schoolTotal))
        }

        //Last row -- totals by level
        str.append("              ")
        spells.groupBy { it.level }
            .values.map { it.size }
            .map { String.format("% 4d", it) }
            .forEach { str.append(it) }
        val total = spells.size
        str.append(String.format("%6d", total))

        println(str)
    }

    class Spell(val name: String, val level: Int, val school: School, val effect: String) {
        enum class School(val code: String) {
            Abjuration("A"),
            Conjuration("C"),
            Divination("D"),
            Enchantment("E"),
            Evocation("V"),
            Illusion("I"),
            Necromancy("N"),
            Transmutation("T");
        }

        companion object {
            fun deserialize(ele: JsonElement): Spell {
                try {
                    val obj = ele.asJsonObject
                    val name = obj.get("name").asString
                    val level = obj.get("level").asInt
                    val school = obj.get("school").deserializeEnum<School>()
                    val effects = obj.get("entries").asJsonArray.filter { it.isJsonPrimitive }.map { it.asString }
                    return Spell(name, level, school, effects.joinToString(" "))
                } catch (e: Exception) {
                    println(ele)
                    throw e
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            val dnd = DnD(File("src/main/resources/dnd_spells.json"))
            dnd.print()
        }
    }
}