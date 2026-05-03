package com.payoff.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.payoff.ussd.UssdStateMachine

@Module
@InstallIn(SingletonComponent::class)
object UssdModule {
    // UssdStateMachine is provided implicitly via @Inject constructor
}
