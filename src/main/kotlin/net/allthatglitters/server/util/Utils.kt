package net.allthatglitters.server.util

import net.allthatglitters.server.util.html.HtmlObject

fun String.bold(): String {
    return HtmlObject("b").withContent(this).render()
}

fun String.italicise(): String {
    return HtmlObject("i").withContent(this).render()
}