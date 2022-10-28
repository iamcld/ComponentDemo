package com.example.viewmodel.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.viewmodel.R
import kotlinx.android.synthetic.main.fragment_right.*
import kotlinx.android.synthetic.main.fragment_right.tv_test

class RightFragment : Fragment() {

    // 此处的viewModel和fragmentactivity的viewModel实例一致，这样才能做到activity和fragment之间保持通信
    private val viewModel: BlankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_right, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_add_one.setOnClickListener(){
            viewModel.addOne()
        }

        viewModel.getLiveData().observe(viewLifecycleOwner) {
            tv_test.text = it.toString()
        }
    }


}