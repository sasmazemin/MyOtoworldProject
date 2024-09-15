
package com.eminsasmaz.otoworldd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.databinding.FragmentHomeBinding
import com.eminsasmaz.otoworldd.databinding.FragmentMyCarsBinding


class MyCarsFragment : Fragment() {
    private lateinit var binding:FragmentMyCarsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMyCarsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}
