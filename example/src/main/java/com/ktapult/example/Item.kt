package com.ktapult.example

class Item(
    val id: Int,
    val value: String,
    var counter: Int = 0
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Item && other.id == id
    }

    override fun toString(): String {
        return "Item(id=$id,value=$value,counter=$counter)"
    }
}