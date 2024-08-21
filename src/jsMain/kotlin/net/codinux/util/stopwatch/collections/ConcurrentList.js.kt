package net.codinux.util.stopwatch.collections

/**
 * JavaScript has only one thread, so no need to take care of any thread-safety structures
 */
actual class ConcurrentList<E> : ArrayList<E>(), List<E> {

    actual fun asCollection(): Collection<E> = toList()

}