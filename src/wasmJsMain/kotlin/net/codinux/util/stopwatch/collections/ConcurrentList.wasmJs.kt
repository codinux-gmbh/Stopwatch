package net.codinux.util.stopwatch.collections

// TODO: synchronize access
actual class ConcurrentList<E> {

    private val impl: MutableList<E> = mutableListOf()

    actual val size = impl.size

    actual fun isEmpty() = impl.isEmpty()

    actual fun add(element: E) = impl.add(element)

    actual fun asCollection(): Collection<E> = impl.toList()

}