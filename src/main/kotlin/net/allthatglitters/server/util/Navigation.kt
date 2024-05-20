package net.allthatglitters.server.util

import net.allthatglitters.server.util.html.HtmlFile

const val hr = "<hr />"
const val divStart = "<div class=\"row\" style=\"justify-content:space-evenly; margin: 0\" >"
const val prevText =
    "<div class=\"column-shrink\"><a href=\"{{prev-link}}\" rel=\"prev\">{{prev}}</a></div>"
const val toc = "<div class=\"column-shrink\"><a href=\"phb_toc.html\">Back to the Table of Contents</a></div>"
const val nextText =
    "<div class=\"column-shrink\"><a href=\"{{next-link}}\" rel=\"next\">{{next}}</a></div>"
const val divEnd = "</div>"

object Navigation {
    fun render(current: Int, max: Int): StringBuilder {
        val prev = if (current > 1) {
            "c${current - 1}.html" to "Retreat to Chapter ${current - 1}"
        } else null
        val next = if (current < max) {
            "c${current + 1}.html" to "Advance to Chapter ${current + 1}"
        } else null

        return render(prev, next)
    }

    fun render(prev: HtmlFile?, next: HtmlFile?): StringBuilder {
        return render(prev?.let { prev.fileName to prev.title },
            next?.let { next.fileName to next.title })
    }

    fun render(prev: Pair<String, String>?, next: Pair<String, String>?): StringBuilder {
        val output = StringBuilder()
        output.append(hr).append("\n")
        output.append(divStart).append("\n")
        if (prev != null) {
            val prevActual = prevText
                .replace("{{prev-link}}", prev.first)
                .replace("{{prev}}", prev.second)
            output.append("\t").append(prevActual).append("\n")
        }
        output.append("\t").append(toc).append("\n")
        if (next != null) {
            val nextActual = nextText
                .replace("{{next-link}}", next.first)
                .replace("{{next}}", next.second)
            output.append("\t").append(nextActual).append("\n")
        }
        output.append(divEnd).append("\n")
        output.append(hr).append("\n")
        return output
    }
}