package com.example.playlistmaker.history

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Node<T>(val value: T, var prev: Node<T>?=null, var next: Node<T>? = null) {
    override fun toString(): String {
        return "$value"
    }
}
class LinkedRepository<T>(private val maxSize: Int) {
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size: Int = 0
    private var map: MutableMap<T, Node<T>?> = HashMap<T, Node<T>?>() // второй параметр - родитель

    fun getSize(): Int {
        return this.size
    }
    fun add(item: T) {
        Log.d("CurrentIds", "Добавляем $item")
        if (item in map) {
            Log.d("CurrentIds", "Ключ в мапе: ${this.getMapKeys()}")
            if (map[item] == null) { // значит это голова — у головы нет предка, удаляем head
                this.removeHead()
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
            this.size--
        }
        var newNode = Node<T>(item)
        if (this.size == maxSize) {
            map.remove(head?.value)
            var head = this.removeHead() // возвращает потомка головы
            map[head?.value!!] = null
            var tail = this.tail
            map.put(item, tail)
            tail?.next = newNode
            newNode.prev = tail
            this.tail = newNode
        } else if (this.size == 0) {
            this.head = newNode
            this.tail = newNode
            map.put(item, null)
        } else {
            var tail = this.tail
            tail?.next = newNode
            newNode.prev = tail
            map.put(item, tail)
            this.tail = newNode
        }
        this.size++
        Log.d("CurrentIds", "Новый размер ${this.getSize()}")
    }
    fun removeHead(): Node<T>? { // возвращает потомка удалённой головы
        head = this.head
        if (head?.next == null) {
            this.map.remove(head?.value)
            this.tail = null
            this.head = null
            return null
        } else {
            var head = head
            this.map.remove(head?.value)
            this.head = head?.next
            this.head?.prev = null
            return this.head
        }
    }
    fun get(reversed: Boolean): ArrayList<T>? {
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

    fun getMapKeys(): String {
        return this.map.keys.toString()
    }

    fun restoreFromSharedPreferences(prefs_name: String,key: String, context: Context) {
        val gson = Gson()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        this.clear()
        val json = sharedPreferences.getString(key, "[]")
        if( (json != "null") || (json != "[]")) {
            val type = object : TypeToken<List<T>>() {}.type
            val list: List<T> = gson.fromJson(json, type)
            for (item in list) {
                add(item as T)
            }
        } else {
            this.clearSharedPreferences(prefs_name,key, context)
        }
    }

    fun saveToSharedPreferences(prefs_name: String,key: String, context: Context) {
        val gson = Gson()
        val json = gson.toJson(this.get(reversed = false))
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clearSharedPreferences(prefs_name: String,key: String, context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
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
