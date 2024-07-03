package net.allthatglitters.server.concepts

import net.allthatglitters.server.concepts.requirement.Requirement

open class Trainable(val name: String,
                     val effect: String,
                     val trainingReqs: Collection<Requirement>) {

    override fun toString(): String {
        return "$name: $effect"
    }
}