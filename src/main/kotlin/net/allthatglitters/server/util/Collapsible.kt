package net.allthatglitters.server.util

private const val script = """<!-- Enables collapsible functionality -->
<script>
	var coll = document.getElementsByClassName("%s");
	var i;

	for (i = 0; i < coll.length; i++) {
	  coll[i].addEventListener("click", function() {
		this.classList.toggle("%s");
		var content = this.nextElementSibling;
		if (content.style.display === "block") {
		  content.style.display = "none";
		} else {
		  content.style.display = "block";
		}
	  });
	}
</script>
"""

object Collapsible {
    fun render(className: String, activeClassName: String): String {
        return script.format(className, activeClassName)
    }
}