package net.allthatglitters.server.util.html

class HtmlTable : HtmlObject("table") {

    fun withHeaders(vararg headers: String): HtmlTable {
        val row = HtmlObject("tr")
            .withAll(headers.map { HtmlObject("th").withContent(it) })

        return withContent(row) as HtmlTable
    }

    fun withRow(vararg data: String): HtmlTable {
        val row = HtmlObject("tr")
            .withAll(data.map { HtmlObject("td").withContent(it) })
        return withContent(row) as HtmlTable
    }
}