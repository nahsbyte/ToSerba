package com.example.buynow.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.buynow.R
import com.example.buynow.data.local.room.item.ItemEntity
import com.example.buynow.data.local.room.item.ItemViewModel
import com.example.buynow.presentation.user.activity.HomeActivity
import com.example.buynow.utils.Extensions.toast
import com.example.buynow.utils.FirebaseUtils.firebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInBtn: Button
    lateinit var emailEt: EditText
    lateinit var passEt: EditText

    lateinit var loadingDialog: LoadingDialog

    lateinit var emailError: TextView
    lateinit var passwordError: TextView
    private val itemCollectionRef = Firebase.firestore.collection("Items")
    private val categoryCollectionRef = Firebase.firestore.collection("Categories")
    private lateinit var itemViewModel: ItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUpTv = findViewById<TextView>(R.id.signUpTv)
        signInBtn = findViewById(R.id.loginBtn)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.PassEt)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)

        textAutoCheck()
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)

        loadingDialog = LoadingDialog(this)

        signUpTv.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signInBtn.setOnClickListener {
            checkInput()
        }


    }

    private fun textAutoCheck() {


        emailEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                } else if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                    emailError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )
                    emailError.visibility = View.GONE
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                } else if (passEt.text.length > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )

                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

                passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                passwordError.visibility = View.GONE
                if (count > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_check
                        ), null
                    )

                }
            }
        })


    }

    private fun checkInput() {

        if (emailEt.text.isEmpty()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Email Can't be Empty"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Enter Valid Email"
            return
        }

        if (passEt.text.isEmpty()) {
            passwordError.visibility = View.VISIBLE
            passwordError.text = "Password Can't be Empty"
            return
        }

        if (passEt.text.isNotEmpty() && emailEt.text.isNotEmpty()) {
            emailError.visibility = View.GONE
            passwordError.visibility = View.GONE
            signInUser()
        }
    }


    private fun signInUser() {
        loadingDialog.startLoadingDialog()
        signInEmail = emailEt.text.toString().trim()
        signInPassword = passEt.text.toString().trim()
        firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    itemCollectionRef.orderBy("productId", Query.Direction.DESCENDING).get()
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                for (document in task.result!!) {
                                    // Convert each document to your data class and add to ArrayList
//                                    val person = document.toObject(ItemEntity::class.java)
                                    var itemEntity = ItemEntity(
                                        document.data["productId"].toString().toInt(),
                                        document.data["productUserId"].toString(),
                                        document.data["productName"].toString(),
                                        document.data["productPrice"].toString()
                                            .replace(Regex("\\D"), "").toInt(),
                                        document.data["productImage"].toString(),
                                        document.data["productDes"].toString(),
                                        document.data["productRating"].toString()
                                            .toDouble(),
                                        document.data["productDisCount"].toString(),
                                        Integer.parseInt(document.data["productStock"].toString()),
                                        false,
                                        document.data["productBrand"].toString(),
                                        document.data["productCategory"].toString(),
                                        document.data["productNote"].toString()
                                    )

                                    Log.d("SQL Query: ", itemEntity.toString())

                                    itemViewModel.insertItem(itemEntity)


                                    var docData: HashMap<String, String> = hashMapOf()
                                    docData["productCategory"] = document.data["productCategory"].toString().uppercase()

                                    categoryCollectionRef
                                        .document(document.data["productCategory"].toString().uppercase())
                                        .set(docData)
                                }

                                loadingDialog.dismissDialog()
                                startActivity(Intent(this, HomeActivity::class.java))
                                toast("signed in successfully")
                                finish()
                            } else {
                                // Handle errors here
                                toast("sign in failed")
                                loadingDialog.dismissDialog()
                            }
                        })
                } else {
                    toast("sign in failed")
                    loadingDialog.dismissDialog()
                }
            }
    }


}