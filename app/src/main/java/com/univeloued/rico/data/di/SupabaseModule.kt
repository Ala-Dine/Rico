package com.univeloued.rico.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://pxotrineglvdqljfykhr.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB4b3RyaW5lZ2x2ZHFsamZ5a2hyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg1MTM2MzIsImV4cCI6MjA5NDA4OTYzMn0.LFS5i57F9V1di62uxODzIUSSz5_l_Xk36yZO7O9wRoM"
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth) {
                scheme = "rico"
                host = "reset-password"
            }
        }
    }
}