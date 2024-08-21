package net.codinux.util.stopwatch.collections

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

expect class ConcurrentList<E>() {

    val size: Int

    fun isEmpty(): Boolean

    fun add(element: E): Boolean

    fun asCollection(): Collection<E>

}

@OptIn(ExperimentalContracts::class)
inline fun <T> ConcurrentList<T>?.isNullOrEmpty(): Boolean {
    contract {
        returns(false) implies (this@isNullOrEmpty != null)
    }

    return this == null || this.isEmpty()
}