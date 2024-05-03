package net.allthatglitters.server

const val footer = """</body>
</html>"""

object Footer {
    fun render(): StringBuilder {
        return StringBuilder(footer)
    }
}