package net.allthatglitters.server.util.html

class HtmlContent : Renderable {
    private val content = mutableListOf<Renderable>()

    fun withContent(html: Renderable): HtmlContent {
        content.add(html)
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
        return content.joinToString("") { it.render() }
    }

    override fun toString(): String {
        return content.joinToString(", ", "[", "]") { it.render() }
    }
}