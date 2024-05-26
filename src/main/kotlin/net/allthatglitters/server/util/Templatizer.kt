package net.allthatglitters.server.util

class Templatizer {
	val rules: MutableMap<Regex, (String) -> String> = mutableMapOf()

	fun withRule(regex: Regex, rule: (String) -> String): Templatizer {
		rules[regex] = rule
		return this
	}

	fun replace(template: String): String {
		return templateRegex.replace(template) { matchResult ->
			val value = matchResult.groupValues[1]
			rules.entries.firstNotNullOf {
				if (it.key.matches(value)) {
					println("Matched rule ${it.key}")
					it.value.invoke(value)
				} else null
			}
		}

	}

	companion object {
		val templateRegex = "\\{\\{(.*)}}".toRegex()
	}
}