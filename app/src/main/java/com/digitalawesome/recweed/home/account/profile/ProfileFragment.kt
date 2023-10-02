package com.digitalawesome.recweed.home.account.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalawesome.dispensary.components.models.FieldType
import com.digitalawesome.dispensary.components.models.InputField
import com.digitalawesome.dispensary.components.views.fragments.UpdateFieldBottomSheetDialog
import com.digitalawesome.recweed.UserViewModel
import com.digitalawesome.recweed.databinding.FragmentProfileBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val userViewModel: UserViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupViewEvents()
        setupDataEvents()

        initialServiceCalls()
    }

    private fun setupViews() {

    }

    private fun setupViewEvents() {
        binding.vEmail.root.setOnClickListener {
            UpdateFieldBottomSheetDialog
                .newInstance("Update Email", listOf(InputField("Email", FieldType.Regular)))
                .show(childFragmentManager, "UpdateEmail")
        }
        binding.vName.root.setOnClickListener {
            UpdateFieldBottomSheetDialog
                .newInstance("Update Name", listOf(
                    InputField("First Name", FieldType.Regular),
                    InputField("Last Name", FieldType.Regular)
                ))
                .show(childFragmentManager, "UpdateName")
        }
        binding.vPhone.root.setOnClickListener {
            UpdateFieldBottomSheetDialog
                .newInstance("Update Phone Number", listOf(InputField("Phone Number", FieldType.Regular)))
                .show(childFragmentManager, "UpdatePhoneNumber")
        }
        binding.vState.root.setOnClickListener {
            UpdateFieldBottomSheetDialog
                .newInstance("Update State", listOf(InputField("State", FieldType.Regular)))
                .show(childFragmentManager, "UpdateState")
        }
        binding.vResetPassword.root.setOnClickListener {
            UpdateFieldBottomSheetDialog
                .newInstance("Reset Password", listOf(
                    InputField("Current Password", FieldType.Password),
                    InputField("New Password", FieldType.Password),
                    InputField("Confirm Password", FieldType.Password),
                ))
                .show(childFragmentManager, "UpdateState")
        }

    }

    private fun setupDataEvents() {
        userViewModel.userData.observe(viewLifecycleOwner) {
            val user = it ?: return@observe
            binding.user = user.attributes
        }
    }

    private fun initialServiceCalls() {


    }

}