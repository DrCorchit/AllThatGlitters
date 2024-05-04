package net.allthatglitters.server

const val header = """<!DOCTYPE html>
<html lang="en">
<head>
<link href='https://fonts.googleapis.com/css?family=Aboreto' rel='stylesheet'>
<link href='https://fonts.googleapis.com/css?family=Almendra Display' rel='stylesheet'>
<link rel="stylesheet" href="styles.css">
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>All That Glitters — Player's Handbook</title>
</head>
<body>
<h1>All That Glitters</h1>
"""

object Header {
    fun render(): StringBuilder {
        return StringBuilder(header)
    }
}