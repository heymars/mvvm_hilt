package com.s.mvvmdemoapp.di

import android.content.Context
import com.s.mvvmdemoapp.data.DataRepository
import com.s.mvvmdemoapp.data.DateRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRepositoryImpl(@ApplicationContext context: Context) : DataRepository = DateRepositoryImpl(context)
}