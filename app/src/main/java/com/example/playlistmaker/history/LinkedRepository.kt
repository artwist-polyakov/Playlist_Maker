package com.example.playlistmaker.history

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Node<T>(val value: T, var next: Node<T>? = null)
class LinkedRepository<T>(private val maxSize: Int) {
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size: Int = 0
    private var map: MutableMap<T, Node<T>?> = HashMap<T, Node<T>?>()


    fun add(item: T) {
        if (map.containsKey(item)) {
            if (map[item] == null) { // удаляем head
                this.removeHead()
                map.remove(item)
            } else {
                val parentNode = map[item]
                parentNode?.next = map[item]?.next
            }
            this.size--
        }
        if (this.size == maxSize) {
            head = this.removeHead()
            tail?.next = Node<T>(item)
            this.tail = tail?.next
        }
        if (this.size == 0) {
            head = Node<T>(item)
            tail = head
            this.size++
        } else {
            tail?.next = Node<T>(item)
            tail = tail?.next
            this.size++
        }
    }
    fun removeHead(): Node<T>? {
        if (head?.next == null) {
            this.map.remove(head?.value)
            return null
        } else {
            val head = head
            this.map.remove(head?.value)
            this.head = head?.next
            this.size--
            return this.head
        }
    }
    fun get(): MutableList<T?> {
        val list: MutableList<T?> = MutableList(size) { null }
        var t = this.head
        for (i in 0 until size) {
            list[size - 1 - i] = t!!.value
            t = t?.next
        }
        return list
    }

    fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
        this.map = HashMap<T, Node<T>?>()
    }

    fun restoreFromSharedPreferences(prefs_name: String,key: String, context: Context) {
        val gson = Gson()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        this.clear()
        val json = sharedPreferences.getString(key, "[]")
        if (json != null) {
            val type = object : TypeToken<List<T>>() {}.type
            val list: List<T> = gson.fromJson(json, type)
            for (item in list) {
                add(item)
            }
        }
    }

    fun saveToSharedPreferences(prefs_name: String,key: String, context: Context) {
        val gson = Gson()
        val json = gson.toJson(get())
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clearSharedPreferences(prefs_name: String,key: String, context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(key).apply()
    }

}
