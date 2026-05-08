package com.univeloued.rico.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // App-wide dependencies like Context-based providers will go here.
}
