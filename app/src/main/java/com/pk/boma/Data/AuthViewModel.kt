package com.pk.boma.Data

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pk.boma.Models.User
import com.pk.boma.navigation.ROUTE_ADD_PRODUCT
import com.pk.boma.navigation.ROUTE_LOGIN
import com.pk.boma.navigation.ROUTE_REGISTER


class AuthViewModel(var navController: NavHostController, var context: Context){


    lateinit var mAuth: FirebaseAuth
    val progress: ProgressDialog

    init {
        mAuth= FirebaseAuth.getInstance()
        progress= ProgressDialog(context)
        progress.setTitle("Loading")
        progress.setMessage("PLease Wait.....")
    }
    fun signup(email:String,pass:String,confpass:String){
        progress.show()

        if (email.isBlank() || pass.isBlank() ||confpass.isBlank()){
            progress.dismiss()
            Toast.makeText(context,"Email and password cannot be blank", Toast.LENGTH_LONG).show()
            return
        }else if (pass != confpass){
            Toast.makeText(context,"Passwords do not match", Toast.LENGTH_LONG).show()
            return
        }else{
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                return@addOnCompleteListener (if (it.isSuccessful) {
                    val userdata = User(email, pass, mAuth.currentUser!!.uid)
                    val regRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users/" + mAuth.currentUser!!.uid)
                    regRef.setValue(userdata).addOnCompleteListener {

                        if (it.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Registered Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(ROUTE_ADD_PRODUCT)

                        } else {
                            Toast.makeText(
                                context,
                                "${it.exception!!.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(ROUTE_REGISTER)
                        }
                    }
                } else {
                    navController.navigate(ROUTE_REGISTER)
                }) as Unit

            }
        }

    }
    fun login(email:String,pass:String){
        progress.show()

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
            progress.dismiss()
            if (it.isSuccessful){
                Toast.makeText(context,"Successfully logged in", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_ADD_PRODUCT)
            }else{
                Toast.makeText(context,"${it.exception!!.message}",Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN)
            }
        }
    }
    fun logout(){
        mAuth.signOut()
        navController.navigate(ROUTE_LOGIN)
    }

    fun isloggedin():Boolean{
        return mAuth.currentUser !=null
    }


}


