package net.codinux.util.stopwatch.collections

expect class ConcurrentMap<K, V>() {

    val keys: Set<K>

    fun get(key: K): V?

    fun getOrPut(key: K, defaultValue: () -> V): V

    fun put(key: K, value: V): V?

    fun remove(key: K): V?

    fun clear()

}