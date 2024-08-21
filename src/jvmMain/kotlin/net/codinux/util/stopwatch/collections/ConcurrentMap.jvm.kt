package net.codinux.util.stopwatch.collections

import java.util.concurrent.ConcurrentHashMap

actual class ConcurrentMap<K, V> actual constructor() {

    private val impl = ConcurrentHashMap<K, V>()

    actual val keys: Set<K>
        get() = impl.keys

    actual fun get(key: K): V? = impl.get(key)

    actual fun getOrPut(key: K, defaultValue: () -> V): V = impl.getOrPut(key, defaultValue)

    actual fun put(key: K, value: V): V? =
        if (key is Any && value is Any) {
            impl.put(key, value)
        } else {
            null
        }

    actual fun remove(key: K): V? = impl.remove(key)

    actual fun clear() = impl.clear()

}