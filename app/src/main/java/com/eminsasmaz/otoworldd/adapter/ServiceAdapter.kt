package com.eminsasmaz.otoworldd.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eminsasmaz.otoworldd.HomeScreenActivity
import com.eminsasmaz.otoworldd.InspectionMapsActivity
import com.eminsasmaz.otoworldd.MapsActivity
import com.eminsasmaz.otoworldd.TireMapsActivity
import com.eminsasmaz.otoworldd.TowMapsActivity
import com.eminsasmaz.otoworldd.databinding.ServicesItemBinding

class ServiceAdapter(private val items:List<String>,private val image:List<Int>):RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(val binding:ServicesItemBinding):RecyclerView.ViewHolder(binding.root){
        private val imagesView=binding.imageView24

        fun bind(item: String, images: Int) {
            binding.serviceName.text=item
            imagesView.setImageResource(images)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding=ServicesItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ServiceViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val item=items[position]
        val images=image[position]
        holder.bind(item,images)

        holder.itemView.setOnClickListener{
           // val intent =Intent(holder.itemView.context,CarparkMapsActivity::class.java)
            //holder.itemView.context.startActivity(intent)
            val context=holder.itemView.context
            val intent=when(position){
                0->Intent(context,MapsActivity::class.java)
                1-> Intent(context,InspectionMapsActivity::class.java)
                2-> Intent(context,TireMapsActivity::class.java)
                3-> Intent(context,TowMapsActivity::class.java)
                else -> Intent(context,HomeScreenActivity::class.java)
            }
            context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return items.size
    }


}