package io.github.crowmisia.regipla.ble

import kotlin.reflect.KProperty

/**
 * 遅延初期化フィールドプロパティ.
 */
class FieldProperty<in R : Any, T : Any>(private val initializer: R.() -> T = { throw IllegalStateException("Not initialized.") }) {
    private val map = WeakIdentityHashMap<R, T>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T =
            map[thisRef] ?: setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T): T {
        map[thisRef] = value
        return value
    }
}
