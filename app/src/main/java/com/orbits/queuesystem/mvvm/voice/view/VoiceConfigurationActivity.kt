package com.orbits.queuesystem.mvvm.voice.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityVoiceConfigurationBinding
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.PrefUtils.getUserDataResponse
import com.orbits.queuesystem.helper.PrefUtils.setUserDataResponse
import com.orbits.queuesystem.helper.interfaces.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.models.UserResponseModel

class VoiceConfigurationActivity : BaseActivity() {
    private lateinit var binding: ActivityVoiceConfigurationBinding
    var gender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_voice_configuration)

        initializeToolbar()
        initializeFields()
        onClickListeners()

    }


    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.voice_configuration),
            isBackArrow = true,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    if (type == Constants.TOOLBAR_ICON_ONE) {

                    }
                }
            }
        )
    }

    private fun initializeFields(){
        binding.edtEnglishMessage.setText(getUserDataResponse()?.msg_en)
        binding.edtArabicMessage.setText(getUserDataResponse()?.msg_ar)

        if (getUserDataResponse()?.voice_gender == Constants.MALE){
            binding.switchMale.isChecked = true
            gender = Constants.MALE
        }else {
            binding.switchFemale.isChecked  = true
            gender = Constants.FEMALE
        }

        when (getUserDataResponse()?.voice_selected) {
            Constants.ENGLISH -> {
                showSelected(binding.ivEnglish)
                showSelectedEditText(listOf(binding.edtEnglishMessage))
            }
            Constants.ARABIC -> {
                showSelected(binding.ivArabic)
                showSelectedEditText(listOf(binding.edtArabicMessage))
            }
            Constants.ENGLISH_ARABIC -> {
                showSelected(binding.ivEnglishArabic)
                showSelectedEditText(listOf(binding.edtEnglishMessage,binding.edtArabicMessage))
            }
            Constants.ARABIC_ENGLISH -> {
                showSelected(binding.ivArabicEnglish)
                showSelectedEditText(listOf(binding.edtEnglishMessage,binding.edtArabicMessage))
            }
        }


    }

    private fun onClickListeners(){
        binding.conEnglish.setOnClickListener {
            showSelected(binding.ivEnglish)
            showSelectedEditText(listOf(binding.edtEnglishMessage))
        }
        binding.conArabic.setOnClickListener {
            showSelected(binding.ivArabic)
            showSelectedEditText(listOf(binding.edtArabicMessage))
        }

        binding.conEnglishArabic.setOnClickListener {
            showSelected(binding.ivEnglishArabic)
            showSelectedEditText(listOf(binding.edtEnglishMessage,binding.edtArabicMessage))
        }

        binding.conArabicEnglish.setOnClickListener {
            showSelected(binding.ivArabicEnglish)
            showSelectedEditText(listOf(binding.edtEnglishMessage,binding.edtArabicMessage))
        }
        binding.switchMale.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                gender = Constants.MALE
                binding.switchFemale.isChecked = false
            }
        }
        binding.switchFemale.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                gender = Constants.FEMALE
                binding.switchMale.isChecked = false
            }
        }

        binding.btnConfirm.setOnClickListener {
            println("here is english msg ${binding.edtEnglishMessage.text}")
            println("here is ar msg ${binding.edtArabicMessage.text}")
            println("here is gender $gender")
            setUserDataResponse(
                UserResponseModel(
                    voice_selected =
                    when {
                        binding.ivEnglish.isVisible -> Constants.ENGLISH
                        binding.ivArabic.isVisible -> Constants.ARABIC
                        binding.ivEnglishArabic.isVisible -> Constants.ENGLISH_ARABIC
                        binding.ivArabicEnglish.isVisible -> Constants.ARABIC_ENGLISH
                        else -> Constants.ENGLISH
                    },
                    msg_en = binding.edtEnglishMessage.text.toString(),
                    msg_ar = binding.edtArabicMessage.text.toString(),
                    voice_gender = gender
                )
            )
            finish()
        }
    }

    private fun showSelected(imageViewToShow: ImageView) {
        val imageViews = listOf(
            binding.ivEnglish,
            binding.ivArabic,
            binding.ivEnglishArabic,
            binding.ivArabicEnglish
        )

        imageViews.forEach { it.visibility = ImageView.GONE }

        imageViewToShow.visibility = ImageView.VISIBLE
    }

    private fun showSelectedEditText(editTextsToShow: List<EditText>) {
        // List of all EditTexts you want to control
        val allEditTexts = listOf(
            binding.edtEnglishMessage,
            binding.edtArabicMessage,

        )


        allEditTexts.forEach { it.isVisible = false }


        editTextsToShow.forEach { it.isVisible = true }
    }
}