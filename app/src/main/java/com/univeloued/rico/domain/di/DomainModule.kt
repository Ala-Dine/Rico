package com.univeloued.rico.domain.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    // Use cases with @Inject constructor are automatically provided by Hilt.
    // You can add explicit providers here if you have interfaces or 
    // need custom initialization for your use cases.
}
