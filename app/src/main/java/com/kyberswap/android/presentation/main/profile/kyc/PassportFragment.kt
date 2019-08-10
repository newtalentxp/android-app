package com.kyberswap.android.presentation.main.profile.kyc


import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.textChanges
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.R
import com.kyberswap.android.databinding.FragmentPassportBinding
import com.kyberswap.android.domain.SchedulerProvider
import com.kyberswap.android.domain.model.KycInfo
import com.kyberswap.android.presentation.base.BaseFragment
import com.kyberswap.android.presentation.helper.DialogHelper
import com.kyberswap.android.presentation.helper.Navigator
import com.kyberswap.android.presentation.main.MainActivity
import com.kyberswap.android.presentation.main.profile.UserInfoState
import com.kyberswap.android.util.di.ViewModelFactory
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_passport.*
import pl.aprilapps.easyphotopicker.*
import java.util.*
import javax.inject.Inject


class PassportFragment : BaseFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentPassportBinding

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var dialogHelper: DialogHelper

    @Inject
    lateinit var appExecutors: AppExecutors


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var easyImage: EasyImage

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private var currentSelectedView: View? = null

    private var frontImageString: String? = null
    private var backImageString: String? = null
    private var selfieImageString: String? = null

    private var currentSelectedDate: EditText? = null


    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PassportViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPassportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.getUserInfo()

        viewModel.getUserInfoCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is UserInfoState.Success -> {
                        if (binding.info != state.userInfo?.kycInfo) {
                            binding.info = state.userInfo?.kycInfo
                            val info = binding.info
                            info?.let {
                                binding.rbId.isChecked = info.isIdentityCard
                                binding.rbPassport.isChecked = info.isPassport
                                binding.cbIssueDate.isChecked = info.issueDateNonApplicable
                                binding.cbExpiryDate.isChecked = info.expiryDateNonApplicable

                                info.photoIdentityFrontSide.removePrefix(BASE64_PREFIX)


                                if (info.photoIdentityFrontSide.isNotEmpty()) {
                                    this.frontImageString =
                                        info.photoIdentityFrontSide.removePrefix(BASE64_PREFIX)
                        
                                displayImage(this.frontImageString, binding.imgPassportFrontSide)

                                info.photoIdentityBackSide.removePrefix(BASE64_PREFIX)

                                if (info.photoIdentityBackSide.isNotEmpty()) {
                                    this.backImageString =
                                        info.photoIdentityBackSide.removePrefix(BASE64_PREFIX)
                        

                                displayImage(this.backImageString, binding.imgPassportBackSide)

                                info.photoSelfie.removePrefix(BASE64_PREFIX)

                                if (info.photoSelfie.isNotEmpty()) {
                                    this.selfieImageString =
                                        info.photoSelfie.removePrefix(BASE64_PREFIX)
                        

                                displayImage(this.selfieImageString, binding.imgPassportHolding)
                    
                
            
                    is UserInfoState.ShowError -> {
                        showError(state.message ?: getString(R.string.something_wrong))
            
        
    
)


        val rxPermissions = RxPermissions(this)

        easyImage = EasyImage.Builder(this.context!!)
            .setChooserTitle(getString(R.string.upload_document))
            .setCopyImagesToPublicGalleryFolder(true)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .setFolderName("kyc")
            .build()

        binding.imgBack.setOnClickListener {
            onBackPress()


        binding.tvNext.setOnClickListener {
            navigator.navigateToSubmitKYC(currentFragment)


        binding.tvPassportFrontSide.setOnClickListener {
            dialogHelper.showBottomSheetPassportDialog(appExecutors)


        binding.tvPassportBackSide.setOnClickListener {
            dialogHelper.showBottomSheetPassportDialog(appExecutors)


        binding.tvPassportHolding.setOnClickListener {
            dialogHelper.showBottomSheetHoldPassportDialog(appExecutors)



        binding.edtIssueDate.setOnClickListener {
            currentSelectedDate = it as EditText
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.maxDate = now
            dpd.show(fragmentManager, "Datepickerdialog")


        binding.edtExpiryDate.setOnClickListener {
            currentSelectedDate = it as EditText
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.minDate = now
            dpd.show(fragmentManager, "Datepickerdialog")


        listOf(
            binding.tvBrowsePassportFontSide,
            binding.tvBrowsePassportBackSide,
            binding.tvBrowsePassportHolding
        )
            .forEach {
                it.setOnClickListener {
                    currentSelectedView = it
                    dialogHelper.showImagePickerBottomSheetDialog(
                        {
                            rxPermissions.request(Manifest.permission.CAMERA)
                                .subscribe { granted ->
                                    if (granted) {
                                        easyImage.openCameraForImage(this)
                             else {
                                        showError(getString(R.string.permission_required))
                            
                        
                ,
                        {
                            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .subscribe { granted ->
                                    if (granted) {
                                        easyImage.openGallery(this)
                             else {
                                        showError(getString(R.string.permission_required))
                            
                        
                
                    )
        
    

        viewModel.compositeDisposable.add(
            binding.cbIssueDate.checkedChanges()
                .observeOn(schedulerProvider.ui())
                .subscribe {
                    binding.edtIssueDate.isEnabled = !it
                    if (!it) {
                        binding.edtIssueDate.requestFocus()
             else {
                        binding.edtIssueDate.setText("")
            

        )

        viewModel.compositeDisposable.add(binding.edtDocumentNumber.textChanges()
            .skipInitialValue()
            .observeOn(schedulerProvider.ui())
            .subscribe {
                binding.ilDocumentId.error = null
    )

        viewModel.compositeDisposable.add(binding.edtIssueDate.textChanges()
            .skipInitialValue()
            .observeOn(schedulerProvider.ui())
            .subscribe {
                binding.ilIssueDate.error = null
    )

        viewModel.compositeDisposable.add(binding.edtExpiryDate.textChanges()
            .skipInitialValue()
            .observeOn(schedulerProvider.ui())
            .subscribe {
                binding.ilExpiryDate.error = null
    )

        viewModel.compositeDisposable.add(
            binding.cbExpiryDate.checkedChanges()
                .observeOn(schedulerProvider.ui())
                .subscribe {
                    binding.edtExpiryDate.isEnabled = !it
                    if (!it) {
                        binding.edtExpiryDate.requestFocus()
             else {
                        binding.edtExpiryDate.setText("")
            

        )

        viewModel.compositeDisposable.add(binding.rgIdentity.checkedChanges()
            .skipInitialValue()
            .observeOn(schedulerProvider.ui())
            .subscribe {
                binding.lnBackSide.visibility =
                    if (it == R.id.rbPassport) View.GONE else View.VISIBLE
                if (it == R.id.rbId) {
                    context?.let {
                        binding.lnHoldingDocument.setBackgroundColor(
                            ContextCompat.getColor(it, R.color.identity_info_gray)
                        )
            

         else if (it == R.id.rbPassport) {
                    binding.lnHoldingDocument.setBackgroundColor(
                        Color.TRANSPARENT
                    )
        
    )
        binding.tvNext.setOnClickListener {
            val info = binding.info
            info?.let {
                val photoIdentityFrontSide =
                    if (frontImageString.isNullOrEmpty()) it.photoIdentityFrontSide else
                        BASE64_PREFIX + frontImageString

                val photoIdentityBackSide =
                    if (backImageString.isNullOrEmpty()) it.photoIdentityBackSide else
                        BASE64_PREFIX + backImageString

                val photoSelfie =
                    if (selfieImageString.isNullOrEmpty()) it.photoSelfie else BASE64_PREFIX + selfieImageString

                when {

                    !binding.rbId.isChecked && !binding.rbPassport.isChecked -> {
                        showAlertWithoutIcon(
                            title = getString(R.string.invalid_document_type),
                            message = getString(R.string.kyc_document_type_required)
                        )
            

                    edtDocumentNumber.text.toString().isBlank() -> {
                        val error = getString(R.string.kyc_document_number_required)
                        showAlertWithoutIcon(
                            title = getString(R.string.invalid_document_number),
                            message = error
                        )
                        binding.ilDocumentId.error = error

            

                    edtIssueDate.text.toString().isBlank() -> {
                        val error = getString(
                            R.string.invalid_issue_date
                        )
                        showAlertWithoutIcon(
                            title = getString(R.string.invalid_input),
                            message = error
                        )
                        binding.ilIssueDate.error = error
            

                    edtExpiryDate.text.toString().isBlank() -> {
                        val error = getString(
                            R.string.invalid_expired_date
                        )
                        showAlertWithoutIcon(
                            title = getString(R.string.invalid_input),
                            message = error
                        )
                        binding.ilExpiryDate.error = error
            

                    photoIdentityFrontSide.isEmpty() -> {
                        showAlertWithoutIcon(
                            title = getString(R.string.photo_not_found),
                            message = getString(R.string.kyc_photo_id_front_required)
                        )
            

                    photoIdentityBackSide.isEmpty() && binding.rbId.isChecked -> {
                        showAlertWithoutIcon(
                            title = getString(R.string.photo_not_found),
                            message = getString(R.string.kyc_photo_id_front_required)
                        )
            

                    photoSelfie.isEmpty() -> {
                        showAlertWithoutIcon(
                            title = getString(R.string.photo_not_found),
                            message = getString(R.string.kyc_photo_id_front_required)
                        )
            

                    else -> {
                        viewModel.save(
                            it.copy(
                                documentId = binding.edtDocumentNumber.text.toString(),
                                documentType = if (binding.rbId.isChecked) KycInfo.TYPE_NATIONAL_ID else if (binding.rbPassport.isChecked) KycInfo.TYPE_PASSPORT else "",
                                documentIssueDate = binding.edtIssueDate.text.toString(),
                                documentExpiryDate = binding.edtExpiryDate.text.toString(),
                                photoIdentityFrontSide = photoIdentityFrontSide,
                                photoIdentityBackSide = photoIdentityBackSide,
                                photoSelfie = photoSelfie

                            )
                        )
            
        
    


        viewModel.savePersonalInfoCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                showProgress(state == SavePersonalInfoState.Loading)
                when (state) {
                    is SavePersonalInfoState.Success -> {
                        navigator.navigateToSubmitKYC(currentFragment)
            
                    is SavePersonalInfoState.ShowError -> {
                        showError(state.message ?: getString(R.string.something_wrong))
            
        
    
)


    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        currentSelectedDate?.setText(
            String.format(
                getString(R.string.date_format_yyyy_mm_dd),
                year,
                monthOfYear + 1,
                dayOfMonth
            )
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activity?.let {
            easyImage.handleActivityResult(requestCode, resultCode, data, it, object :
                DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    onPhotosReturned(imageFiles)
        

                override fun onImagePickerError(error: Throwable, source: MediaSource) {

                    error.printStackTrace()
        

                override fun onCanceled(source: MediaSource) {

        
    )

    }

    private fun onPhotosReturned(returnedPhotos: Array<MediaFile>) {

        returnedPhotos.first().file.absolutePath?.let {
            viewModel.resizeImage(it)
            viewModel.resizeImageCallback.observe(viewLifecycleOwner, Observer {
                it?.getContentIfNotHandled()?.let { state ->
                    showProgress(state == ResizeImageState.Loading)
                    when (state) {
                        is ResizeImageState.Success -> {
                            displayImage(state.stringImage)
                
                        is ResizeImageState.ShowError -> {
                            showError(state.message ?: getString(R.string.something_wrong))
                
            
        
    )


    }

    private fun displayImage(stringImage: String?, imageView: ImageView? = null) {
        if (stringImage.isNullOrEmpty()) return
        showLoadingImage(true, imageView)
        setStringImage(stringImage, imageView)
        viewModel.decode(stringImage, imageView)
        viewModel.decodeImageCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                showProgress(state == DecodeBase64State.Loading)
                when (state) {
                    is DecodeBase64State.Success -> {
                        glideDisplayImage(state.byteArray, state.imageView)
            
                    is DecodeBase64State.ShowError -> {
                        showLoadingImage(false, imageView)
            
        
    
)
    }

    private fun showLoadingImage(isShown: Boolean, imageView: ImageView?) {
        imageView?.let {
            when (it) {
                binding.imgPassportFrontSide -> binding.progressBarPassportFrontSide.visibility =
                    if (isShown) View.VISIBLE else View.GONE
                binding.imgPassportBackSide -> binding.progressBarPassportBackSide.visibility =
                    if (isShown) View.VISIBLE else View.GONE
                binding.imgPassportHolding -> binding.progressBarPassportHolding.visibility =
                    if (isShown) View.VISIBLE else View.GONE
    

    }

    private fun setStringImage(stringImage: String, imageView: ImageView?) {
        val image = getCurrentSelectedImage(imageView)
        image?.let {
            when (it) {
                binding.imgPassportFrontSide -> frontImageString = stringImage
                binding.imgPassportBackSide -> backImageString = stringImage
                binding.imgPassportHolding -> selfieImageString = stringImage
    

    }

    private fun getCurrentSelectedImage(imageView: ImageView?): ImageView? {
        return imageView ?: when (currentSelectedView) {
            binding.tvBrowsePassportBackSide -> binding.imgPassportBackSide
            binding.tvBrowsePassportFontSide -> binding.imgPassportFrontSide
            binding.tvBrowsePassportHolding -> binding.imgPassportHolding
            else -> null

    }


    private fun glideDisplayImage(byteArray: ByteArray, imageView: ImageView?) {
        val image = getCurrentSelectedImage(imageView)
        image?.let {
            Glide.with(it)
                .load(byteArray)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        showLoadingImage(false, imageView)
                        return false
            

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        showLoadingImage(false, imageView)
                        return false
            

        )
                .into(image)

    }

    fun onBackPress() {
        val fm = (activity as MainActivity).getCurrentFragment()?.childFragmentManager
        if (fm != null) {
            for (i in 0 until fm.backStackEntryCount) {
                fm.popBackStack()
    

        navigator.navigateToPersonalInfo(currentFragment)

    }

    override fun onDestroyView() {
        viewModel.onCleared()
        viewModel.compositeDisposable.clear()
        super.onDestroyView()
    }

    companion object {
        private const val BASE64_PREFIX = "data:image/jpeg;base64,"
        fun newInstance() =
            PassportFragment()
    }


}
