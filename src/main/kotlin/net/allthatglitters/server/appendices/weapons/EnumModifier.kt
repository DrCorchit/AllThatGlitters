package net.allthatglitters.server.appendices.weapons

enum class EnumModifier(
	override val displayName: String,
	val description: String
) : Modifier {
	CutAndThrust(
		"Cut-and-Thrust",
		"The weapon can deal either piercing or slashing damage."
	),
	Nimble(
		"Nimble",
		"Dexterity can be used to provide the weapon's damage, instead of strength."
	),
	Improvised(
		"Improvised",
		"The item is not designed as a weapon. The damage die is rolled with advantage."
	),
	Light(
		"Light",
		"The weapon can be used alongside a greatshield, or dual wielded alongside another light weapon."
	),
	TwoHanded(
		"Two-Handed",
		"The weapon must be handled with two hands when attacking."
	),
	Sharp(
		"Sharp",
		"The critical threshold for the weapon is reduced by 1. This property is temporarily lost after critical failing an attack roll, but can be regained by using a whetstone."
	),
	Polearm(
		"Polearm",
		"The weapon can hit targets 10 feet away instead of 5, and is compatible with the <i>Polarm Charge</i> ability."
	),
	Throwable(
		"Throwable",
		"The weapon does not gain the <i>Improvised</i> trait when thrown."
	),
	XBow(
		"Crossbow",
		"The weapon is a mechanical crossbow. It requires 30 seconds to reload, and its damage does not benefit from the user's attribute scores."
	),
	Firearm(
		"Firearm",
		"The weapon is a firearm and deals force damage. It requires 1 minute to reload, and its damage does not benefit from the user's attribute scores."
	);

	val regex: Regex = displayName.toRegex(RegexOption.IGNORE_CASE)

	companion object {
		fun parse(str: String): EnumModifier? {
			return entries.firstOrNull { it.regex.matches(str) }
		}
	}
}