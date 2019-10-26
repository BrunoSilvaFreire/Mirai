package me.ddevil.mirai

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

private val random = Random()
fun <K, V> hashMap(builder: HashMap<K, V>.() -> Unit): HashMap<K, V> {
    val map = HashMap<K, V>()
    map.builder()
    return map
}
