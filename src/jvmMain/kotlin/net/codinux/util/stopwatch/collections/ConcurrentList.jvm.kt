package net.codinux.util.stopwatch.collections

import java.util.concurrent.CopyOnWriteArrayList

actual class ConcurrentList<E> : CopyOnWriteArrayList<E>(), List<E> {

    actual fun asCollection(): Collection<E> = toList()

}