package com.mark.data

enum class BadgeTypePlugin(val label : String) {
    INSTALLS("Total Installs"),
    VERSION("Current Version"),
    AUTHOR("Author"),
    CREATED("Created on"),
    LAST_UPDATED("Last Updated"),
    RANK("Rank"),
    ERROR("Error");
    companion object {
        fun getType(name : String) = BadgeTypePlugin.values().firstOrNull { it.name == name.uppercase() } ?: ERROR
    }
}

data class BadgeTemplate(
    val schemaVersion : Int = 1,
    var label : String = "",
    var message : String = "",
    val cacheSeconds : Int = 300,
    var isError : Boolean = false
)