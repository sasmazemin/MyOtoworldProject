
package com.eminsasmaz.otoworldd.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.eminsasmaz.otoworldd.R
import com.eminsasmaz.otoworldd.adapter.ServiceAdapter
import com.eminsasmaz.otoworldd.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var serviceAdapter:ServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList=ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.ford_advert,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.avis,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.mercedes_advert,ScaleTypes.FIT))

        val imageSlider=binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList,ScaleTypes.FIT)
        imageSlider.setItemClickListener(object :ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition =imageList[position]
                val itemMessage="Selected Image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }
        })
        binding.serviceRecyclerView.layoutManager=LinearLayoutManager(this.context)
        val serviceName= listOf("Carpark","Inspection","Tire","Tow")
        val serviceImages= listOf(R.drawable.parking_car_svgrepo_com_1,R.drawable.inspection,R.drawable.changing_car_tire_svgrepo_com_1,R.drawable.car_breakdown_tow_svgrepo_com_1)
        serviceAdapter= ServiceAdapter(serviceName,serviceImages)
        binding.serviceRecyclerView.adapter=serviceAdapter

        binding.serviceRecyclerView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMapsActivity()
            Navigation.findNavController(it).navigate(action)
        }

    }

    companion object {

    }
}
/*
  binding.ServiceRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.ServiceRecyclerView.adapter=adapter
 */
