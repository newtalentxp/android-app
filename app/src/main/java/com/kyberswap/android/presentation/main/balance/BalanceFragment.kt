package com.kyberswap.android.presentation.main.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.jakewharton.rxbinding3.widget.textChanges
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.databinding.FragmentBalanceBinding
import com.kyberswap.android.domain.SchedulerProvider
import com.kyberswap.android.presentation.base.BaseFragment
import com.kyberswap.android.presentation.common.PendingTransactionNotification
import com.kyberswap.android.presentation.helper.Navigator
import com.kyberswap.android.presentation.main.MainActivity
import com.kyberswap.android.presentation.main.balance.kyberlist.KyberListViewModel
import com.kyberswap.android.presentation.splash.GetWalletState
import com.kyberswap.android.util.di.ViewModelFactory
import com.kyberswap.android.util.ext.showDrawer
import com.kyberswap.android.util.ext.showKeyboard
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class BalanceFragment : BaseFragment(), PendingTransactionNotification {

    private lateinit var binding: FragmentBalanceBinding

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    var currentSelectedView: View? = null

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(KyberListViewModel::class.java)
    }

    private val options by lazy {
        listOf(binding.tvKyberList, binding.tvOther)
    }

    private val balanceAddress by lazy { listOf(binding.tvAddress, binding.tvQr) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getSelectedWallet()
        viewModel.getSelectedWalletCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is GetWalletState.Success -> {
                        binding.tvUnit.text = state.wallet.unit
                        binding.tvBalance.text = state.wallet.balance
                        if (binding.wallet != state.wallet) {
                            binding.wallet = state.wallet
                
            
                    is GetWalletState.ShowError -> {

            
        
    
)


        val adapter = BalancePagerAdapter(
            childFragmentManager
        )
        binding.vpBalance.adapter = adapter
        binding.vpBalance.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
    

            override fun onPageSelected(position: Int) {
    
)


        binding.tvKyberList.isSelected = true
        currentSelectedView = binding.tvKyberList

        options.forEachIndexed { index, view ->
            view.setOnClickListener {
                setSelectedOption(it)
                binding.vpBalance.currentItem = index
    


        balanceAddress.forEach { view ->
            view.setOnClickListener {
                (activity as MainActivity).getCurrentFragment()
                navigator.navigateToBalanceAddressScreen(
                    (activity as MainActivity).getCurrentFragment()
                )
    



        binding.imgMenu.setOnClickListener {
            showDrawer(true)


        listOf(binding.edtSearch, binding.imgSearch).forEach {
            it.setOnClickListener {
                binding.edtSearch.requestFocus()
                it.showKeyboard()
    



        viewModel.compositeDisposable.add(
            binding.edtSearch.textChanges().skipInitialValue().debounce(
                250,
                TimeUnit.MILLISECONDS
            )
                .map {
                    return@map it.trim().toString().toLowerCase()
        .observeOn(schedulerProvider.ui())
                .subscribe {
                    viewModel.updateSearchKeyword(it)
        )

        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.edtSearch.clearFocus()
    
            false

    }

    override fun showNotification(showNotification: Boolean) {
        binding.vNotification.visibility = if (showNotification) View.VISIBLE else View.GONE
    }

    private fun setSelectedOption(view: View) {
        currentSelectedView?.isSelected = false
        view.isSelected = true
        currentSelectedView = view
    }

    companion object {
        fun newInstance() = BalanceFragment()
    }
}
