package com.kyberswap.android.presentation.helper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kyberswap.android.R
import com.kyberswap.android.domain.model.Alert
import com.kyberswap.android.domain.model.Contact
import com.kyberswap.android.domain.model.LocalLimitOrder
import com.kyberswap.android.domain.model.MarketItem
import com.kyberswap.android.domain.model.Order
import com.kyberswap.android.domain.model.SocialInfo
import com.kyberswap.android.domain.model.Token
import com.kyberswap.android.domain.model.Transaction
import com.kyberswap.android.domain.model.UserInfo
import com.kyberswap.android.domain.model.Wallet
import com.kyberswap.android.domain.model.Word
import com.kyberswap.android.presentation.landing.LandingActivity
import com.kyberswap.android.presentation.main.MainActivity
import com.kyberswap.android.presentation.main.alert.AlertMethodFragment
import com.kyberswap.android.presentation.main.alert.LeaderBoardFragment
import com.kyberswap.android.presentation.main.alert.ManageAlertFragment
import com.kyberswap.android.presentation.main.alert.PriceAlertFragment
import com.kyberswap.android.presentation.main.alert.PriceAlertTokenSearchFragment
import com.kyberswap.android.presentation.main.balance.address.BalanceAddressFragment
import com.kyberswap.android.presentation.main.balance.chart.ChartFragment
import com.kyberswap.android.presentation.main.balance.send.SendConfirmActivity
import com.kyberswap.android.presentation.main.balance.send.SendFragment
import com.kyberswap.android.presentation.main.kybercode.KyberCodeFragment
import com.kyberswap.android.presentation.main.limitorder.CancelOrderFragment
import com.kyberswap.android.presentation.main.limitorder.ConvertFragment
import com.kyberswap.android.presentation.main.limitorder.FilterLimitOrderFragment
import com.kyberswap.android.presentation.main.limitorder.LimitOrderFragment
import com.kyberswap.android.presentation.main.limitorder.LimitOrderTokenSearchFragment
import com.kyberswap.android.presentation.main.limitorder.ManageOrderFragment
import com.kyberswap.android.presentation.main.limitorder.MarketFragment
import com.kyberswap.android.presentation.main.limitorder.OrderConfirmFragment
import com.kyberswap.android.presentation.main.limitorder.OrderConfirmV2Fragment
import com.kyberswap.android.presentation.main.notification.NotificationFragment
import com.kyberswap.android.presentation.main.notification.NotificationSettingFragment
import com.kyberswap.android.presentation.main.profile.ProfileDetailFragment
import com.kyberswap.android.presentation.main.profile.ProfileFragment
import com.kyberswap.android.presentation.main.profile.SignUpConfirmFragment
import com.kyberswap.android.presentation.main.profile.SignUpFragment
import com.kyberswap.android.presentation.main.profile.TermConditionActivity
import com.kyberswap.android.presentation.main.profile.kyc.KycInfoSearchFragment
import com.kyberswap.android.presentation.main.profile.kyc.KycInfoType
import com.kyberswap.android.presentation.main.profile.kyc.PassportFragment
import com.kyberswap.android.presentation.main.profile.kyc.PersonalInfoFragment
import com.kyberswap.android.presentation.main.profile.kyc.SubmitFragment
import com.kyberswap.android.presentation.main.profile.kyc.VerificationFragment
import com.kyberswap.android.presentation.main.setting.AddContactFragment
import com.kyberswap.android.presentation.main.setting.ContactFragment
import com.kyberswap.android.presentation.main.setting.wallet.BackupWalletInfoFragment
import com.kyberswap.android.presentation.main.setting.wallet.EditWalletFragment
import com.kyberswap.android.presentation.main.setting.wallet.ManageWalletFragment
import com.kyberswap.android.presentation.main.swap.PromoPaymentConfirmActivity
import com.kyberswap.android.presentation.main.swap.PromoSwapConfirmActivity
import com.kyberswap.android.presentation.main.swap.SwapConfirmActivity
import com.kyberswap.android.presentation.main.swap.TokenSearchFragment
import com.kyberswap.android.presentation.main.transaction.CustomizeGasFragment
import com.kyberswap.android.presentation.main.transaction.TransactionDetailReceiveFragment
import com.kyberswap.android.presentation.main.transaction.TransactionDetailSendFragment
import com.kyberswap.android.presentation.main.transaction.TransactionDetailSwapFragment
import com.kyberswap.android.presentation.main.transaction.TransactionFilterFragment
import com.kyberswap.android.presentation.main.transaction.TransactionFragment
import com.kyberswap.android.presentation.main.walletconnect.WalletConnectFragment
import com.kyberswap.android.presentation.wallet.BackupWalletActivity
import com.kyberswap.android.presentation.wallet.ImportWalletActivity
import com.kyberswap.android.presentation.wallet.VerifyBackupWordActivity
import javax.inject.Inject

class Navigator @Inject constructor(private val activity: AppCompatActivity) {
    private val containerId: Int = R.id.container
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    fun navigateToLandingPage() {
        activity.startActivity(LandingActivity.newIntent(activity))
        activity.finishAffinity()
    }

    fun navigateToImportWalletPage(fromMain: Boolean = false) {
        activity.startActivity(ImportWalletActivity.newIntent(activity, fromMain))
    }

    fun navigateVerifyBackupWordPage(words: List<Word>, wallet: Wallet) {
        activity.startActivity(VerifyBackupWordActivity.newIntent(activity, words, wallet))
    }

    fun navigateToBackupWalletPage(
        words: List<Word>,
        wallet: Wallet,
        fromSetting: Boolean = false
    ) {
        activity.startActivity(BackupWalletActivity.newIntent(activity, words, wallet, fromSetting))
        if (!fromSetting) {
            activity.finishAffinity()
        }
    }

    fun navigateToHome(isPromoCode: Boolean = false) {
        activity.startActivity(
            MainActivity.newIntent(activity, isPromoCode = isPromoCode)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
    }

    @JvmOverloads
    fun replaceFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        customAnimations: Int = WITHOUT_ANIMATION,
        container: Int = this.containerId
    ) {
        val transaction = fragmentManager.beginTransaction()
        if (customAnimations == IN_RIGHT_OUT_LEFT) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        } else if (customAnimations == IN_LEFT_OUT_RIGHT) {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        transaction.replace(container, fragment, fragment.javaClass.simpleName)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commitAllowingStateLoss()
    }

    fun navigateToBalanceAddressScreen(
        currentFragment: Fragment?
    ) {

        navigateByChildFragmentManager(currentFragment, BalanceAddressFragment.newInstance())
    }

    fun navigateToChartScreen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        token: Token?,
        chartMarket: String,
        market: MarketItem? = null,
        orderType: Int = LocalLimitOrder.TYPE_BUY
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            ChartFragment.newInstance(wallet, token, chartMarket, market, orderType)
        )
    }

    fun navigateToTokenSearchFromSwapTokenScreen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        isSourceToken: Boolean = false
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            TokenSearchFragment.newInstance(wallet, false, isSourceToken)
        )
    }

    fun navigateToTokenSearchFromLimitOrder(
        currentFragment: Fragment?,
        wallet: Wallet?,
        isSourceToken: Boolean = false
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            LimitOrderTokenSearchFragment.newInstance(wallet, isSourceToken)
        )
    }

    fun navigateToLimitOrderMarket(
        currentFragment: Fragment?, type: Int, quoteSymbol: String?
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            MarketFragment.newInstance(type, quoteSymbol)
        )
    }

    fun navigateToLimitOrderV1(
        currentFragment: Fragment?
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            LimitOrderFragment.newInstance()
        )
    }

    fun navigateToTokenSearchFromSendTokenScreen(
        currentFragment: Fragment?,
        wallet: Wallet?
    ) {

        navigateByChildFragmentManager(
            currentFragment,
            TokenSearchFragment.newInstance(wallet, true, false)
        )
    }

    fun navigateToSwapConfirmationScreen(wallet: Wallet?) {
        when {
            wallet?.isPromo == true -> when {
                wallet.isPromoPayment -> activity.startActivity(
                    PromoPaymentConfirmActivity.newIntent(
                        activity,
                        wallet
                    )
                )
                else -> activity.startActivity(PromoSwapConfirmActivity.newIntent(activity, wallet))
            }
            else -> activity.startActivity(SwapConfirmActivity.newIntent(activity, wallet))
        }
    }

    fun navigateToSendConfirmationScreen(wallet: Wallet?, isContactExist: Boolean) {
        activity.startActivity(SendConfirmActivity.newIntent(activity, wallet, isContactExist))
    }

    fun navigateToWalletConnectScreen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        content: String
    ) {
//        activity.startActivity(WalletConnectActivity.newIntent(activity, wallet, content))
        navigateByChildFragmentManager(
            currentFragment,
            WalletConnectFragment.newInstance(wallet, content)
        )
    }

    fun navigateToSendScreen(
        currentFragment: Fragment?,
        wallet: Wallet?
    ) {
        navigateByChildFragmentManager(currentFragment, SendFragment.newInstance(wallet))
    }

    fun navigateToNotificationScreen(
        currentFragment: Fragment?
    ) {
        navigateByChildFragmentManager(currentFragment, NotificationFragment.newInstance())
    }

    fun navigateToNotificationSettingScreen(
        currentFragment: Fragment?, isPriceNotificationEnable: Boolean
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            NotificationSettingFragment.newInstance(isPriceNotificationEnable)
        )
    }

    fun navigateToAddContactScreen(
        currentFragment: Fragment?,
        wallet: Wallet? = null,
        address: String = "",
        contact: Contact? = null
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            AddContactFragment.newInstance(wallet, address, contact)
        )
    }

    fun navigateToContactScreen(
        currentFragment: Fragment?
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            ContactFragment.newInstance()
        )
    }

    fun navigateToTransactionScreen(
        currentFragment: Fragment?,
        wallet: Wallet?
    ) {

        navigateByChildFragmentManager(currentFragment, TransactionFragment.newInstance(wallet))
    }

    fun navigateToTransactionFilterScreen(
        currentFragment: Fragment?,
        wallet: Wallet?
    ) {

        navigateByChildFragmentManager(
            currentFragment,
            TransactionFilterFragment.newInstance(wallet)
        )
    }

    fun navigateToSwapTransactionScreen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        transaction: Transaction?
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            TransactionDetailSwapFragment.newInstance(wallet, transaction)
        )
    }

    fun navigateToSendTransactionScreen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        transaction: Transaction?
    ) {

        navigateByChildFragmentManager(
            currentFragment,
            TransactionDetailSendFragment.newInstance(wallet, transaction)
        )
    }

    fun navigateToReceivedTransactionScreen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        transaction: Transaction?
    ) {

        navigateByChildFragmentManager(
            currentFragment,
            TransactionDetailReceiveFragment.newInstance(wallet, transaction)
        )
    }

    private fun navigateByChildFragmentManager(
        currentFragment: Fragment?,
        newFragment: Fragment,
        addToBackStack: Boolean = true
    ) {
        currentFragment?.let {
            currentFragment.view?.id?.let { id ->
                replaceFragment(
                    currentFragment.childFragmentManager,
                    id,
                    newFragment,
                    addToBackStack
                )
            }

        }
    }

    private fun navigateByFragmentManager(
        currentFragment: Fragment?,
        newFragment: Fragment
    ) {
        currentFragment?.fragmentManager?.let { fragmentManager ->
            currentFragment.view?.id?.let { id ->
                replaceFragment(
                    fragmentManager,
                    id,
                    newFragment
                )
            }

        }
    }

    fun navigateToSignUpScreen(currentFragment: Fragment?) {
        navigateByChildFragmentManager(currentFragment, SignUpFragment.newInstance())
    }

    fun navigateToSignInScreen(currentFragment: Fragment?) {
        navigateByChildFragmentManager(currentFragment, ProfileFragment.newInstance(), false)
    }

    fun navigateToTermAndCondition() {
        activity.startActivity(TermConditionActivity.newIntent(activity))
    }

    fun navigateToSignUpConfirmScreen(
        currentFragment: Fragment?,
        socialInfo: SocialInfo?
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            SignUpConfirmFragment.newInstance(socialInfo)
        )
    }

    fun navigateToProfileDetail(
        currentFragment: Fragment?
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            ProfileDetailFragment.newInstance(),
            false
        )
    }

    private fun replaceFragment(
        fragmentManager: FragmentManager,
        container: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true
    ) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(container, fragment, fragment.javaClass.simpleName)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commitAllowingStateLoss()
    }

    fun navigateToManageOrder(currentFragment: Fragment?, wallet: Wallet?) {
        navigateByChildFragmentManager(
            currentFragment,
            ManageOrderFragment.newInstance(wallet)
        )
    }

    fun navigateToManageOrderV1(currentFragment: Fragment?, wallet: Wallet?) {
        navigateByFragmentManager(
            currentFragment,
            ManageOrderFragment.newInstance(wallet)
        )
    }

    fun navigateToLimitOrderFilterScreen(
        currentFragment: Fragment?,
        wallet: Wallet?
    ) {

        navigateByChildFragmentManager(
            currentFragment,
            FilterLimitOrderFragment.newInstance(wallet)
        )
    }

    fun navigateToOrderConfirmScreen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        limitOrder: LocalLimitOrder?
    ) {

        navigateByChildFragmentManager(
            currentFragment,
            OrderConfirmFragment.newInstance(wallet, limitOrder)
        )
    }

    fun navigateToOrderConfirmV2Screen(
        currentFragment: Fragment?,
        wallet: Wallet?,
        limitOrder: LocalLimitOrder?
    ) {

        navigateByChildFragmentManager(
            currentFragment,
            OrderConfirmV2Fragment.newInstance(wallet, limitOrder)
        )
    }

    fun navigateToConvertFragment(
        currentFragment: Fragment?,
        wallet: Wallet?,
        order: LocalLimitOrder?
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            ConvertFragment.newInstance(wallet, order)
        )
    }

    fun navigateToCancelOrderFragment(
        currentFragment: Fragment?,
        wallet: Wallet?,
        orders: ArrayList<Order>,
        currentOrder: LocalLimitOrder?,
        needConvertEth: Boolean
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            CancelOrderFragment.newInstance(wallet, orders, currentOrder, needConvertEth)
        )
    }

    fun navigateToTokenSelection(currentFragment: Fragment?, wallet: Wallet?, alert: Alert?) {
        navigateByChildFragmentManager(
            currentFragment,
            PriceAlertTokenSearchFragment.newInstance(wallet, alert)
        )
    }

    fun navigateToPriceAlertScreen(currentFragment: Fragment?, alert: Alert? = null) {
        navigateByChildFragmentManager(
            currentFragment,
            PriceAlertFragment.newInstance(alert)
        )
    }

    fun navigateToLeaderBoard(
        currentFragment: Fragment?,
        userInfo: UserInfo?,
        isCampaignResult: Boolean = false
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            LeaderBoardFragment.newInstance(userInfo, isCampaignResult)
        )
    }

    fun navigateToManageAlert(currentFragment: Fragment) {
        navigateByChildFragmentManager(
            currentFragment,
            ManageAlertFragment.newInstance()
        )
    }

    fun navigateToAlertMethod(currentFragment: Fragment) {
        navigateByChildFragmentManager(
            currentFragment,
            AlertMethodFragment.newInstance()
        )
    }

    fun navigateToKYC(currentFragment: Fragment, step: Int?) {
        navigateByChildFragmentManager(
            currentFragment,
            when (step) {
                UserInfo.KYC_STEP_ID_PASSPORT -> PassportFragment.newInstance()
                UserInfo.KYC_STEP_SUBMIT -> SubmitFragment.newInstance()
                else -> PersonalInfoFragment.newInstance()
            }
        )
    }

    fun navigateToPassport(currentFragment: Fragment) {
        navigateByChildFragmentManager(
            currentFragment,
            PassportFragment.newInstance()
        )
    }

    fun navigateToPersonalInfo(currentFragment: Fragment) {
        navigateByChildFragmentManager(
            currentFragment,
            PersonalInfoFragment.newInstance()
        )
    }

    fun navigateToSubmitKYC(currentFragment: Fragment) {
        navigateByChildFragmentManager(
            currentFragment,
            SubmitFragment.newInstance()
        )
    }

    fun navigateToVerification(currentFragment: Fragment) {
        navigateByChildFragmentManager(
            currentFragment,
            VerificationFragment.newInstance()
        )
    }

    fun navigateToSearch(
        currentFragment: Fragment,
        data: List<String>,
        infoType: KycInfoType
    ) {
        navigateByChildFragmentManager(
            currentFragment,
            KycInfoSearchFragment.newInstance(data, infoType)

        )
    }

    fun navigateToKyberCode(currentFragment: Fragment?) {
        navigateByChildFragmentManager(
            currentFragment,
            KyberCodeFragment.newInstance()

        )
    }

    fun navigateToKyberCodeFromLandingPage(container: Int) {
        replaceFragment(
            fragmentManager,
            container,
            KyberCodeFragment.newInstance(true)
        )
    }

    fun navigateToManageWalletFragment(currentFragment: Fragment) {
        navigateByChildFragmentManager(
            currentFragment,
            ManageWalletFragment.newInstance()
        )
    }

    fun navigateToEditWallet(currentFragment: Fragment, wallet: Wallet) {
        navigateByChildFragmentManager(
            currentFragment,
            EditWalletFragment.newInstance(wallet)
        )
    }

    fun navigateToBackupWalletInfo(currentFragment: Fragment?, value: String) {
        navigateByChildFragmentManager(
            currentFragment,
            BackupWalletInfoFragment.newInstance(value)
        )
    }

    fun navigateToCustomGas(currentFragment: Fragment, transaction: Transaction) {
        navigateByChildFragmentManager(
            currentFragment,
            CustomizeGasFragment.newInstance(transaction)

        )
    }

    companion object {
        const val IN_RIGHT_OUT_LEFT = 1
        const val IN_LEFT_OUT_RIGHT = -1
        const val WITHOUT_ANIMATION = 0
    }
}
