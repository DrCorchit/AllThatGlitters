package net.allthatglitters.server.util.html

class HtmlTable(attributes: MutableMap<String, String> = mutableMapOf()) :
	HtmlObject("table", attributes) {
	private val defaultRowAttributes = mutableMapOf<String, String>()

	fun withDefaultRowAttribute(key: String, value: String): HtmlTable {
		defaultRowAttributes[key] = value
		return this
	}

	fun withHeaders(vararg headers: String): HtmlTable {
		val row = HtmlObject("tr")
			.withAll(headers.map {
				val header = HtmlObject("th").withContent(it)
				header
			})

		return withContent(row) as HtmlTable
	}

	fun withRow(vararg data: String): HtmlTable {
		val row = HtmlObject("tr")
			.withAll(data.map { HtmlObject("td").withContent(it) })
		return withContent(row) as HtmlTable
	}

	fun nextRow(attributes: MutableMap<String, String> = defaultRowAttributes): TableRow {
		val row = TableRow(attributes)
		withContent(row)
		return row
	}

	inner class TableRow(attributes: MutableMap<String, String> = mutableMapOf()) :
		HtmlObject("tr", attributes) {
		private val defaultHeaderAttributes = mutableMapOf<String, String>()
		private val defaultDataAttributes = mutableMapOf<String, String>()

		fun withDefaultHeaderAttribute(key: String, value: String): TableRow {
			defaultHeaderAttributes[key] = value
			return this
		}

		fun withDefaultDataAttribute(key: String, value: String): TableRow {
			defaultDataAttributes[key] = value
			return this
		}

		fun withHeader(
			content: String,
			attributes: MutableMap<String, String> = defaultHeaderAttributes
		): TableRow {
			withContent(HtmlObject("th", attributes).withContent(content))
			return this
		}

		fun withData(
			content: String,
			attributes: MutableMap<String, String> = defaultDataAttributes
		): TableRow {
			withContent(HtmlObject("td", attributes).withContent(content))
			return this
		}

		fun withHeader(
			content: Renderable,
			attributes: MutableMap<String, String> = defaultHeaderAttributes
		): TableRow {
			withContent(HtmlObject("th", attributes).withContent(content))
			return this
		}

		fun withData(
			content: Renderable,
			attributes: MutableMap<String, String> = defaultDataAttributes
		): TableRow {
			withContent(HtmlObject("td", attributes).withContent(content))
			return this
		}

		fun nextRow(): TableRow {
			return table().nextRow()
		}

		fun table(): HtmlTable {
			return this@HtmlTable
		}
	}

}