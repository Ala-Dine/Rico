package com.univeloued.rico.data.sync

import android.content.Context
import androidx.work.*
import com.univeloued.rico.domain.sync.SyncManager
import com.univeloued.rico.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncManager {

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleRecordsSync() {
        enqueueSync(Constants.WORK_SYNC_RECORDS)
    }

    override fun scheduleContactsSync() {
        enqueueSync(Constants.WORK_SYNC_CONTACTS)
    }

    override fun scheduleFamilySync() {
        enqueueSync(Constants.WORK_SYNC_FAMILY)
    }

    override fun scheduleRemindersSync() {
        enqueueSync(Constants.WORK_SYNC_REMINDERS)
    }

    override fun triggerManualSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueueUniqueWork(
            Constants.WORK_MANUAL_SYNC,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    private fun enqueueSync(uniqueName: String) {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        workManager.enqueueUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, syncRequest)
    }
}
