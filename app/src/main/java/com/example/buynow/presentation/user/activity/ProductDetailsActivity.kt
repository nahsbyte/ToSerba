package com.example.buynow.presentation.user.activity

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buynow.R
import com.example.buynow.data.local.room.cart.CartViewModel
import com.example.buynow.data.local.room.cart.ProductEntity
import com.example.buynow.data.local.room.item.ItemEntity
import com.example.buynow.data.local.room.item.ItemViewModel
import com.example.buynow.presentation.user.adapter.ProductAdapter
import com.example.buynow.utils.Extensions.toast
import com.google.firebase.auth.FirebaseAuth

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var itemViewModel: ItemViewModel
    private lateinit var addToCart_ProductDetailsPage: Button
    private lateinit var plusLayout: LinearLayout
    private lateinit var minusLayout: LinearLayout
    private lateinit var quantityEtBottom: EditText
    var productId: Int = -1
    private lateinit var cartViewModel: CartViewModel
    private val TAG = "TAG"
    lateinit var productImage_ProductDetailsPage: ImageView
    lateinit var backIv_ProfileFrag: ImageView
    lateinit var productName_ProductDetailsPage: TextView
    lateinit var productBrand_ProductDetailsPage: TextView
    lateinit var lblRating: TextView
    lateinit var productDes_ProductDetailsPage: TextView
    lateinit var RatingProductDetails: TextView
    lateinit var productRating_singleProduct: RatingBar
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    lateinit var RecomRecView_ProductDetailsPage: RecyclerView
    lateinit var newProductAdapter: ProductAdapter
    lateinit var newProduct: ArrayList<ItemEntity>

    lateinit var pName: String
    var qua: Int = 1
    var pPrice: Int = 0
    var subTotal: Int = 0
    var pPid: Int = 0
    var pUid: String = ""
    lateinit var pImage: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        productId = intent.getIntExtra("ProductID", -1)

        productImage_ProductDetailsPage = findViewById(R.id.productImage_ProductDetailsPage)
        productName_ProductDetailsPage = findViewById(R.id.productName_ProductDetailsPage)
        productBrand_ProductDetailsPage = findViewById(R.id.productBrand_ProductDetailsPage)
        lblRating = findViewById(R.id.lblRating)
        productDes_ProductDetailsPage = findViewById(R.id.productDes_ProductDetailsPage)
        plusLayout = findViewById(R.id.plusLayout)
        minusLayout = findViewById(R.id.minusLayout)
        quantityEtBottom = findViewById(R.id.quantityEtBottom)
        productRating_singleProduct = findViewById(R.id.productRating_singleProduct)
        RatingProductDetails = findViewById(R.id.RatingProductDetails)
        RecomRecView_ProductDetailsPage = findViewById(R.id.RecomRecView_ProductDetailsPage)
        backIv_ProfileFrag = findViewById(R.id.backIv_ProfileFrag)
        addToCart_ProductDetailsPage = findViewById(R.id.addToCart_ProductDetailsPage)

        newProduct = arrayListOf()

        RecomRecView_ProductDetailsPage.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        RecomRecView_ProductDetailsPage.setHasFixedSize(true)
        newProductAdapter = ProductAdapter(newProduct, this)
        RecomRecView_ProductDetailsPage.adapter = newProductAdapter

        backIv_ProfileFrag.setOnClickListener {
            onBackPressed()
        }

        plusLayout.setOnClickListener {
            qua++
            quantityEtBottom.setText(qua.toString())

            setProductData()
        }

        minusLayout.setOnClickListener {
            qua--
            if (qua < 1)
                qua = 1
            quantityEtBottom.setText(qua.toString())

            setProductData()
        }

        addToCart_ProductDetailsPage.setOnClickListener {
            addProductToBag()
        }

        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        setProductData()
        itemViewModel.getByItemID(productId.toString())
    }

    private fun addProductToBag() {

        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        cartViewModel.insert(
            ProductEntity(
                firebaseAuth.uid.toString(),
                pUid,
                pName,
                qua,
                pPrice,
                subTotal,
                pPid,
                pImage,
                true,
                false,
                "", ""
            )
        )
        toast("Tambah ke keranjang berhasil")
    }

    private fun setProductData() {
        // Observe changes in item LiveData
        itemViewModel.item.observe(this, Observer { itemSelected ->
            // Update UI with item data
            Glide.with(applicationContext)
                .load(itemSelected.image)
                .into(productImage_ProductDetailsPage)

            productName_ProductDetailsPage.text = itemSelected.name

            productBrand_ProductDetailsPage.text = itemSelected.brand
            productDes_ProductDetailsPage.text = itemSelected.desc
            productRating_singleProduct.rating = itemSelected.rating.toFloat()
            lblRating.text = String.format("%.1f", (itemSelected.rating))
            RatingProductDetails.text =
                itemSelected.rating.toString() + " Rating on this Product."

            pName = itemSelected.name
            subTotal = itemSelected.price * qua
            pPrice = itemSelected.price
            pUid = itemSelected.pUId
            pPid = itemSelected.pId
            pImage = itemSelected.image
            addToCart_ProductDetailsPage.text =
                "Tambahkan ke keranjang | " + "Rp " + subTotal
        })
    }

}


