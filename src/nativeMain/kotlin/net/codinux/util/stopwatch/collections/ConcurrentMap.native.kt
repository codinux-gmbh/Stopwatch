package net.codinux.util.stopwatch.collections

import kotlin.concurrent.AtomicReference

actual open class ConcurrentMap<K, V> {

    protected open val atomicMap = AtomicReference(mutableMapOf<K, V>())

    actual val keys: Set<K>
        get() = atomicMap.value.keys

    actual open fun get(key: K): V? =
        atomicMap.value[key]

    actual fun getOrPut(key: K, defaultValue: () -> V): V {
        val value = get(key)
        if (value != null) {
            return value
        }

        val newValue = defaultValue()

        do {
            val existing = atomicMap.value

            val putInTheMeantime = get(key)
            if (putInTheMeantime != null) {
                return putInTheMeantime
            }

            val updated = existing
            updated[key] = newValue
        } while (atomicMap.compareAndSet(existing, updated) == false)

        return newValue
    }

    actual open fun put(key: K, value: V): V? {
        val previousValue = get(key)

        do {
            val existing = atomicMap.value

            val updated = existing
            updated[key] = value
        } while (atomicMap.compareAndSet(existing, updated) == false)

        return previousValue
    }

    actual open fun remove(key: K): V? {
        var previousValue: V?

        do {
            val existing = atomicMap.value

            val updated = existing
            previousValue = updated.remove(key)
        } while (atomicMap.compareAndSet(existing, updated) == false)

        return previousValue
    }

    actual open fun clear() {
        @Suppress("ControlFlowWithEmptyBody")
        while (atomicMap.compareAndSet(atomicMap.value, mutableMapOf()) == false) { }
    }

}