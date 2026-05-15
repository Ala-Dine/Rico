package com.univeloued.rico.domain.sync

interface SyncManager {
    fun scheduleRecordsSync()
    fun scheduleContactsSync()
    fun scheduleFamilySync()
    fun scheduleRemindersSync()
    fun triggerManualSync()
}
