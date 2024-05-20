package net.allthatglitters.server.util.html

class HtmlContent : Renderable {
	private val content = mutableListOf<Renderable>()

	fun hasContent(): Boolean {
		return content.isNotEmpty()
	}

	fun withAll(html: Iterable<Renderable>): HtmlContent {
		content.addAll(html)
		return this
	}

	fun withContent(html: Renderable): HtmlContent {
		if (html !is HtmlString || html.content.isNotEmpty()) {
			//No empty strings.
			content.add(html)
		}
		return this
	}

	override fun asContent(): HtmlContent {
		return this
	}

	fun preface(obj: HtmlObject): HtmlContent {
		val first = content.first()
		if (first is HtmlString) {
			content[0] = obj.withContent(first.content)
		} else {
			content.add(0, obj)
		}
		return this
	}

	fun wrapRaw(tag: String = "p"): HtmlContent {
		content.replaceAll {
			if (it is HtmlString) HtmlObject(tag).withContent(it.content) else it
		}
		return this
	}

	override fun render(): String {
		return render(0)
	}

	fun render(indent: Int): String {
		val output = StringBuilder()
		for (i in content.indices) {
			val ele = content[i]
			val nextEle = if (i + 1 < content.size) content[i + 1] else null

			when (ele) {
				is HtmlObject -> ele.render(indent)
				is HtmlContent -> ele.render(indent)
				else -> ele.render()
			}.let { output.append(it) }

			val adjacentElements = ele !is HtmlString && nextEle != null && nextEle !is HtmlString
			if (adjacentElements) output.append("\n")
		}
		return output.toString()
	}

	override fun toString(): String {
		return when (content.size) {
			0 -> "[]"
			1 -> "[${content.first()}]"
			else -> "[${content.first()}...${content.size - 1}]"
		}
	}
}