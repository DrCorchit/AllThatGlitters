package net.allthatglitters.server.util

import com.google.gson.JsonElement
import net.allthatglitters.server.concepts.magic.Discipline
import java.io.File

object Util {

    val json =
        """[{
          "name": "",
          "rarity": "common",
          "discipline": "%s",
          "type": "evocation",
          "target": "self",
          "duration": 1,
          "effect": "",
          "training_reqs": {
            "slots": 3
          },
            "casting_reqs": {
            "time": "1 Action",
            "ap": 3,
          },
          "scaling": {
          }
        }]""".trimIndent()

    @JvmStatic
    fun main(vararg args: String) {
        Discipline.entries.forEach {
            val file = File(inputDir, "spells/${it.name}.json")
            if (!file.exists()) {
                val jsonStr = String.format(json, it.name)
                file.writeText(jsonStr)
            }
        }
    }
}

