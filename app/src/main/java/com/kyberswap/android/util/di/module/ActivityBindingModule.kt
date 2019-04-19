package com.kyberswap.android.util.di.module

import com.kyberswap.android.presentation.landing.LandingActivity
import com.kyberswap.android.presentation.main.MainActivity
import com.kyberswap.android.presentation.splash.SplashActivity
import com.kyberswap.android.presentation.wallet.BackupWalletActivity
import com.kyberswap.android.presentation.wallet.VerifyBackupWordActivity
import com.kyberswap.android.util.di.scope.ActivityScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            MainActivityModule::class
        ]
    )
    fun contributeMainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            SplashActivityModule::class
        ]
    )
    fun contributeSplashActivity(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            LandingActivityModule::class
        ]
    )
    fun contributeLandingActivity(): LandingActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            BackupWalletActivityModule::class
        ]
    )
    fun contributeBackupWalletActivity(): BackupWalletActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            VerifyBackupWordActivityModule::class
        ]
    )
    fun contributeVerifyBackupWordActivity(): VerifyBackupWordActivity

}
