package io.github.crowmisia.regipla.ble

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * 弱参照IdentityHashMap.
 */
class WeakIdentityHashMap<K : Any, V> : MutableMap<K, V> {
    private val delegate = HashMap<IdentityWeakReference<K>, V>()
    private val queue = ReferenceQueue<K>()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            reap()
            val entries = delegate.entries
            val ret = HashSet<MutableMap.MutableEntry<K, V>>(entries.size)
            entries.forEach { it.key.get() ?.let { key ->
                ret.add(object : MutableMap.MutableEntry<K, V> {
                    override val key = key
                    override val value = it.value
                    override fun setValue(newValue: V): V { throw UnsupportedOperationException() }
                })
            }}
            return ret
        }

    override val keys: MutableSet<K>
        get() {
            reap()
            val keys = delegate.keys
            val ret = HashSet<K>(keys.size)
            keys.mapNotNullTo(ret, transform = { key -> key.get() })
            return ret
        }

    override val values: MutableCollection<V>
        get() {
            reap()
            return delegate.values
        }

    override val size: Int
        get() {
            reap()
            return delegate.size
        }

    override fun clear() {
        delegate.clear()
        reap()
    }

    override fun containsKey(key: K): Boolean {
        reap()
        return delegate.containsKey(IdentityWeakReference(key, queue))
    }

    override fun containsValue(value: V): Boolean {
        reap()
        return delegate.containsValue(value)
    }

    override fun get(key: K): V? {
        reap()
        return delegate[IdentityWeakReference(key, queue)]
    }

    override fun isEmpty(): Boolean {
        reap()
        return delegate.isEmpty()
    }


    override fun put(key: K, value: V): V? {
        reap()
        return delegate.put(IdentityWeakReference(key, queue), value)
    }

    override fun putAll(from: Map<out K, V>) {
        reap()
        from.forEach {
            delegate.put(IdentityWeakReference(it.key, queue), it.value)
        }
    }

    override fun remove(key: K): V? {
        reap()
        return delegate.remove(IdentityWeakReference(key, queue))
    }

    private fun reap() {
        synchronized(queue) {
            var zombie = queue.poll()

            while (zombie != null) {
                delegate.remove(zombie)
                zombie = queue.poll()
            }
        }
    }

    class IdentityWeakReference<K>(o: K?, queue: ReferenceQueue<K>) : WeakReference<K>(o, queue) {
        private val hash = System.identityHashCode(o)

        override fun hashCode() = hash

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other is WeakReference<*>) {
                if (get() === other.get()) {
                    return true
                }
            }
            return false
        }
    }
}
