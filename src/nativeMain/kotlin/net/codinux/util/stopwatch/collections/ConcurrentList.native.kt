package net.codinux.util.stopwatch.collections

import kotlin.concurrent.AtomicReference

actual open class ConcurrentList<E> {

    protected open val atomicList = AtomicReference(mutableListOf<E>())

    actual val size: Int
        get() = atomicList.value.size

    actual fun isEmpty(): Boolean = size == 0

    actual fun add(element: E): Boolean {
        do {
            val existing = atomicList.value

            val updated = existing
            updated.add(element)
        } while (atomicList.compareAndSet(existing, updated) == false)

        return true
    }

    actual fun asCollection(): Collection<E> = atomicList.value.toList()
}