package com.example.buynow.presentation.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.buynow.R
import com.example.buynow.data.local.room.item.ItemEntity
import com.example.buynow.data.local.room.item.ItemViewModel
import com.example.buynow.data.model.Item
import com.example.buynow.presentation.activity.VisualSearchActivity
import com.example.buynow.presentation.adapter.ProductAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

//    NewProducts.json

    private lateinit var itemViewModel: ItemViewModel

    lateinit var saleRecView: RecyclerView
    lateinit var saleProduct: ArrayList<ItemEntity>

    lateinit var saleProductAdapter: ProductAdapter

    lateinit var animationView: LottieAnimationView

    lateinit var saleLayout: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        saleProduct = arrayListOf()

        saleRecView = view.findViewById(R.id.saleRecView)
        saleLayout = view.findViewById(R.id.saleLayout)
        animationView = view.findViewById(R.id.animationView)

        val visualSearchBtn_homePage: ImageView = view.findViewById(R.id.visualSearchBtn_homePage)

        hideLayout()

        getItems()

        saleRecView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        saleRecView.setHasFixedSize(true)
        saleProductAdapter = ProductAdapter(saleProduct, requireContext())
        saleRecView.adapter = saleProductAdapter

        visualSearchBtn_homePage.setOnClickListener {
            startActivity(Intent(context, VisualSearchActivity::class.java))
        }

        showLayout()

        return view
    }

    private fun getItems() {
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)

        itemViewModel.allItems.observe(viewLifecycleOwner, Observer { List ->
            List?.let {
                saleProductAdapter.updateList(it)
            }

            if (List.size == 0) {
                animationView.playAnimation()
                animationView.loop(true)


            } else {
                animationView.pauseAnimation()
            }


        })
    }


    private fun hideLayout() {
        animationView.playAnimation()
        animationView.loop(true)
        saleLayout.visibility = View.GONE
        animationView.visibility = View.VISIBLE
    }

    private fun showLayout() {
        animationView.pauseAnimation()
        animationView.visibility = View.GONE
        saleLayout.visibility = View.VISIBLE
    }

//    private fun setCoverData() {
//
//        val jsonFileString = context?.let {
//
//            StringUtils.getJsonData(it, "CoverProducts.json")
//        }
//        val gson = Gson()
//
//        val listCoverType = object : TypeToken<List<Product>>() {}.type
//
//        var coverD: List<Product> = gson.fromJson(jsonFileString, listCoverType)
//
//        coverD.forEachIndexed { idx, person ->
//
//            coverProduct.add(person)
//            saleProduct.add(person)
//
//        }
//    }
//
//    private fun setNewProductData() {
//
//        val jsonFileString = context?.let {
//
//            StringUtils.getJsonData(it, "NewProducts.json")
//        }
//        val gson = Gson()
//
//        val listCoverType = object : TypeToken<List<Product>>() {}.type
//
//        var coverD: List<Product> = gson.fromJson(jsonFileString, listCoverType)
//
//        coverD.forEachIndexed { idx, person ->
//
//
//            newProduct.add(person)
//
//
//        }
//
//
//    }

}


