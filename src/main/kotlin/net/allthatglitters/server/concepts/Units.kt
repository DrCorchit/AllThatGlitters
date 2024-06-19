package net.allthatglitters.server.concepts

import com.drcorchit.justice.utils.math.units.TimeUnits

val round = TimeUnits.add("rd", "Round", "Rounds", TimeUnits.SEC.ratio * 5)
val action = TimeUnits.add("act", "Action", "Actions", TimeUnits.SEC.ratio * 5)
val shortRest = TimeUnits.add("short", "Short Rest", "Short Rests", TimeUnits.HOUR.ratio)
val longRest = TimeUnits.add("long", "Long Rest", "Long Rests", TimeUnits.HOUR.ratio * 8)

