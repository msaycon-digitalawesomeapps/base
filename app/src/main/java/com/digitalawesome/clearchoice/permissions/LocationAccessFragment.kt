package com.digitalawesome.clearchoice.permissions

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalawesome.clearchoice.databinding.FragmentLocationAccessBinding


class LocationAccessFragment : Fragment() {

    interface Listener {
        fun enableButtonClicked()
    }
    companion object {
        fun newInstance() = LocationAccessFragment()
    }

    private lateinit var binding: FragmentLocationAccessBinding
    private lateinit var viewModel: LocationAccessViewModel
    private var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationAccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewEvents()
    }

    private fun setupViewEvents() {
        binding.btEnableLocationService.setOnClickListener {
            listener?.enableButtonClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }
}