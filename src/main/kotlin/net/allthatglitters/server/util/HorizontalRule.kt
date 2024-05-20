package net.allthatglitters.server.util

import net.allthatglitters.server.util.html.Renderable

object HorizontalRule: Renderable {
    override fun render(): String {
        return "<hr />"
    }
}