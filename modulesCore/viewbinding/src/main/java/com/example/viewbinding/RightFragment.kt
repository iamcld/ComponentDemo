package com.example.viewbinding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.viewbinding.databinding.FragmentRightBinding


class RightFragment : Fragment() {

    // 此处的viewModel和fragmentactivity的viewModel实例一致，这样才能做到activity和fragment之间保持通信
    private val viewModel: BlankViewModel by activityViewModels()
    private lateinit var viewBingding: FragmentRightBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        viewBingding = FragmentRightBinding.inflate(inflater, container, false)
        return viewBingding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBingding.btnAddOne.setOnClickListener() {
            viewModel.addOne()
        }

        viewModel.getLiveData().observe(viewLifecycleOwner) {
            viewBingding.tvTest.text = it.toString()
        }
    }


}