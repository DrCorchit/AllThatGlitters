package net.allthatglitters.server.util

const val footer = """</body>
</html>"""

object Footer {
    fun render(): StringBuilder {
        return StringBuilder(footer)
    }
}