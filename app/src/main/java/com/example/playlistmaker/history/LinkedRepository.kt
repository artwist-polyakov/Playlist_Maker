package com.example.playlistmaker.history

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*
Историю треков храним связным списком, в котором каждый элемент содержит ссылку на предыдущий и следующий элементы.
В качестве элемента списка используем класс Node.

Для того, чтобы находить уже присутствующие элементы в списке, используем map, в котором ключом является значение элемента,
а значением - ссылка на родитель элемента в связном списке.
Для решения нашей задачи мы переопредлили методы hashCode и equals для класса Track: чтобы они сравнивались по trackId.
Таким образом, мы можем быстро проверить, есть ли элемент в списке, и если есть, то удалить его из списка за O(1).

При добавлении нового элемента в список, мы проверяем, есть ли он в списке. Если есть, то удаляем его из списка,
после чего добавляем его же в хвост. Если элемента нет в списке, то просто добавляем его в хвост списка.

Если размер связного списка превышает maxSize, то удаляем элемент из головы списка, после чего добавляем новый элемент в хвост.

Для вывода предусмотрен метод get() который умеет выводить список в прямом и обратном порядке.
Для вывода в прямом порядке надо вызвать get с параметром false, для вывода в обратном порядке - с параметром true.
 */

class Node<T>(val value: T, var prev: Node<T>? = null, var next: Node<T>? = null) {
    override fun toString(): String {
        return "$value"
    }
}

class LinkedRepository<T>(private val maxSize: Int) {
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
            this.size-- // если элемент в списке был, то из первого ифа выходим с уменьшенным размером списка
        }
        var newNode = Node<T>(item)
        if (this.size == maxSize) { // сценарий когда размер списка превышает maxSize
            map.remove(head?.value)
            var head = this.removeHead() // возвращает потомка головы
            map[head?.value!!] = null
            var tail = this.tail
            map.put(item, tail)
            tail?.next = newNode
            newNode.prev = tail
            this.tail = newNode
        } else if (this.size == 0) { // сценарий когда список пуст
            this.head = newNode
            this.tail = newNode
            map.put(item, null)
        } else { // общий сценрий
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

    fun restoreFromSharedPreferences(prefs_name: String, key: String, context: Context) {
        val gson = Gson()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        this.clear()
        val json = sharedPreferences.getString(key, "[]")
        if ((json != "null") || (json != "[]")) {
            val type = object : TypeToken<List<T>>() {}.type
            val list: List<T> = gson.fromJson(json, type)
            for (item in list) {
                add(item as T)
            }
        } else {
            this.clearSharedPreferences(prefs_name, key, context)
        }
    }

    fun saveToSharedPreferences(prefs_name: String, key: String, context: Context) {
        val gson = Gson()
        val json = gson.toJson(this.get(reversed = false))
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clearSharedPreferences(prefs_name: String, key: String, context: Context) {
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
