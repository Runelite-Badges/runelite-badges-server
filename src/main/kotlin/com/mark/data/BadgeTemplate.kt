package com.mark.data

enum class BadgeType {
    INSTALLS,
    VERSION,

    ERROR;

    companion object {
        fun getType(name : String) = BadgeType.values().firstOrNull { it.name == name.uppercase() } ?: ERROR
    }

}


data class BadgeTemplate(
    val schemaVersion : Int = 1,
    var label : String = "",
    var message : String = "",
    val cacheSeconds : Int = 300,
    var isError : Boolean = false
)