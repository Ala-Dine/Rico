package com.univeloued.rico.util

object Constants {
    const val DATABASE_NAME = "rico_database"
    const val SECURE_PREFS_NAME = "rico_secure_prefs"
    
    // Supabase Buckets
    const val BUCKET_MEDICAL_RECORDS = "medical-records"
    
    // File Subdirectories
    const val DIR_MEDICAL_RECORDS = "medical_records"
    const val DIR_PROFILE_PHOTOS = "profile_photos"
    const val DIR_FAMILY_PHOTOS = "family_photos"
    
    // Key Store Aliases
    const val MASTER_KEY_ALIAS = "rico_master_key"
    const val E2EE_KEY_ALIAS = "rico_e2ee_key"
    
    // Work Names
    const val WORK_SYNC_RECORDS = "sync_records"
    const val WORK_SYNC_CONTACTS = "sync_contacts"
    const val WORK_SYNC_FAMILY = "sync_family"
    const val WORK_SYNC_REMINDERS = "sync_reminders"
    const val WORK_MANUAL_SYNC = "manual_sync"
}
