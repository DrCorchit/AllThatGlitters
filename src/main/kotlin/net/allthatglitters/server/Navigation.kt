package net.allthatglitters.server

const val hr = "<hr />"
const val divStart = "<div class=\"row\" style=\"justify-content:space-evenly; margin: 0\" >"
const val prev =
    "<div class=\"column-shrink\"><a href=\"[c-1].html\">Retreat to [prev]</a></div>"
const val toc = "<div class=\"column-shrink\"><a href=\"phb_toc.html\">Back to the Table of Contents</a></div>"
const val next =
    "<div class=\"column-shrink\"><a href=\"[c+1].html\">Advance to [next]</a></div>"
const val divEnd = "</div>"

class Navigation(val index: Int) {
    fun render(max: Int): StringBuilder {
        val output = StringBuilder()
        output.append(hr).append("\n")
        output.append(divStart).append("\n")
        if (index > 1) {
            val prevActual = prev
                .replace("[c-1]", "c${index - 1}")
                .replace("[prev]", "Chapter ${index - 1}")
            output.append("\t").append(prevActual).append("\n")
        }
        output.append("\t").append(toc).append("\n")
        if (index < max) {
            val nextActual = next
                .replace("[c+1]", "c${index + 1}")
                .replace("[next]", "Chapter ${index + 1}")
            output.append("\t").append(nextActual).append("\n")
        }
        output.append(divEnd).append("\n")
        output.append(hr).append("\n")
        return output
    }
}