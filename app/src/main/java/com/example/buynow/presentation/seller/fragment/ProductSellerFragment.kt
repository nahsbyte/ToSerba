package com.example.buynow.presentation.seller.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buynow.R
import com.example.buynow.data.local.room.item.ItemEntity
import com.example.buynow.data.local.room.item.ItemViewModel
import com.example.buynow.presentation.seller.activity.AddItemActivity
import com.example.buynow.presentation.seller.adapter.ProductSellerAdapter
import com.example.buynow.presentation.user.activity.EditProfileActivity

class ProductSellerFragment : Fragment() {

    private lateinit var tambahItem: Button
    lateinit var adminProductRecView: RecyclerView
    lateinit var productAdminAdapter: ProductSellerAdapter
    lateinit var etSearch: EditText
    lateinit var ibFilter: ImageButton
    lateinit var saleProduct: ArrayList<ItemEntity>
    lateinit var itemViewModel: ItemViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_seller, container, false)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        adminProductRecView = view.findViewById(R.id.adminProductRecView)
        etSearch = view.findViewById(R.id.etSearch)
        ibFilter = view.findViewById(R.id.ibFilter)
        tambahItem = view.findViewById(R.id.tambahItem)
        saleProduct = arrayListOf()
        getItems()

        adminProductRecView.layoutManager = LinearLayoutManager(requireContext())
        adminProductRecView.setHasFixedSize(true)
        productAdminAdapter = ProductSellerAdapter(saleProduct, requireContext())
        adminProductRecView.adapter = productAdminAdapter

        ibFilter.setOnClickListener {
            productAdminAdapter.filter.filter(etSearch.text.toString())
        }

        tambahItem.setOnClickListener{
            val intent = Intent(requireContext(), AddItemActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun getItems() {
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)

        itemViewModel.allItems.observe(viewLifecycleOwner, Observer { List ->
            List?.let {
                var itemSize = it.size
                productAdminAdapter.updateList(it)

            }
        })
    }
}