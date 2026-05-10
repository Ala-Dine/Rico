package com.univeloued.rico.domain.model

enum class RecordType(val displayName: String) {
    ALL("All"),
    VISITS("Visits"),
    RX("Rx"),
    LABS("Labs");

    companion object {
        fun fromString(value: String): RecordType {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: ALL
        }
    }
}
