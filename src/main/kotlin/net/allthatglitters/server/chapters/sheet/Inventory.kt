package net.allthatglitters.server.chapters.sheet

import net.allthatglitters.server.appendices.armor.Armor
import net.allthatglitters.server.appendices.items.Item
import net.allthatglitters.server.appendices.weapons.Weapon

class Inventory(val gold: Int,
                val weapons: MutableSet<Weapon>,
                val armor: MutableSet<Armor>,
                val items: MutableSet<Item>) {

}