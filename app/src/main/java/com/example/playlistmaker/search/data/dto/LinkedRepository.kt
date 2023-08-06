package com.example.playlistmaker.search.data.dto

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



open class LinkedRepository<T>(
    private val maxSize: Int,
    private val sharedPreferences: SharedPreferences
) {

    private companion object {
        const val HISTORY_KEY = "HISTORY_KEY"
    }

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size: Int = 0
    private var map: MutableMap<T, Node<T>?> = HashMap() // второй параметр - родитель

    fun getSize(): Int {
        return this.size
    }

    fun add(item: T) {
        Log.d("CurrentIds", "Добавляем $item")
        if (item in map) { // сначала надо проверить, есть ли элемент в списке
            Log.d("CurrentIds", "Ключ в мапе: ${this.getMapKeys()}")
            if (map[item] == null) { // значит это голова — у головы нет предка, удаляем head
                this.head = this.removeHead()
            } else {
                val parentNode = map[item]
                parentNode?.next = map[item]?.next?.next
                if (parentNode?.next != null) {
                    parentNode.next?.prev = parentNode
                } else { // значит parentNode - это хвост
                    this.tail = parentNode
                }
                map.remove(item)
            }
            this.size-- // если элемент в списке был, то из первого ифа выходим с уменьшенным размером списка
        }
        val newNode = Node(item)
        when (this.size) {
            this.maxSize -> { // сценарий когда размер списка превышает maxSize
                map.remove(head?.value)
                val head = this.removeHead() // возвращает потомка головы
                this.size--
                map[head?.value!!] = null
                val tail = this.tail
                map[item] = tail
                tail?.next = newNode
                newNode.prev = tail
                this.tail = newNode
            }
            0 -> { // сценарий когда список пуст
                this.head = newNode
                this.tail = newNode
                map[item] = null
            }
            else -> { // общий сценрий
                val tail = this.tail
                tail?.next = newNode
                newNode.prev = tail
                map[item] = tail
                this.tail = newNode
            }
        }
        this.size++
        Log.d("CurrentIds", "Новый размер ${this.getSize()}")
    }

    private fun removeHead(): Node<T>? { // возвращает потомка удалённой головы
        head = this.head
        return if (head?.next == null) {
            this.map.remove(head?.value)
            this.tail = null
            this.head = null
            null
        } else {
            val head = head
            this.map.remove(head?.value)
            this.head = head?.next
            this.head?.prev = null
            this.head
        }
    }

    fun get(reversed: Boolean = false): ArrayList<T> {
        val list = ArrayList<T>()
        var node = if (reversed) tail else head
        while (node != null) {
            list.add(node.value)
            node = if (reversed) node.prev else node.next
        }
        return list
    }

    fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
        this.map = HashMap()
    }

    private fun getMapKeys(): String {
        return this.map.keys.toString()
    }

    fun restoreFromSharedPreferences() {
        val key = HISTORY_KEY
        val gson = Gson()
        this.clear()
        val json = sharedPreferences.getString(key, "[]")
        if ((json != "null") || (json != "[]")) {
            val type = object : TypeToken<List<T>>() {}.type
            val list: List<T> = gson.fromJson(json, type)
            for (item in list) {
                add(item)
            }
        } else {
            this.clearSharedPreferences()
        }
    }

    fun saveToSharedPreferences() {
        val key = HISTORY_KEY
        val gson = Gson()
        val json = gson.toJson(this.get(reversed = false))
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clearSharedPreferences() {
        val key = HISTORY_KEY
        sharedPreferences.edit().remove(key).apply()
    }

    override fun toString(): String {
        return "${this.head}, ${this.tail}, ${this.size}"
    }

    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this.get(reversed = true))
    }

}

class Node<T>(val value: T, var prev: Node<T>? = null, var next: Node<T>? = null) {
    override fun toString(): String {
        return "$value"
    }
}