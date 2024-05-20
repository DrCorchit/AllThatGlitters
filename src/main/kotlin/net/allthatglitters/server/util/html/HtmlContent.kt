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
        val output = StringBuilder()
        for (i in content.indices) {
            val ele = content[i]
            val nextEle = if (i + 1 < content.size) content[i + 1] else null
            output.append(content[i].render())
            val adjacentElements = ele !is HtmlString && nextEle != null && nextEle !is HtmlString
            if (adjacentElements) {
                //TODO someday, would be nice to actually output pretty/readable html
                output.append("\n")
            }
            //if (nextEle != null) output.append("\n")
        }
        return output.toString()
    }

    override fun toString(): String {
        return "[${content.first()}...${content.size}]"
        //return content.joinToString(", ", "[", "]") { it.render() }
    }
}