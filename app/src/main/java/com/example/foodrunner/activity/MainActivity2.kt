package com.example.foodrunner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.drawerActivity
import com.example.foodrunner.fragment.*
import com.example.foodrunner.model.Restaurants

import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity2 : AppCompatActivity() {

    lateinit var coordinatelayout: CoordinatorLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var activity: drawerActivity
    lateinit var toolbar: Toolbar
    lateinit var navigationview: NavigationView
    lateinit var frame: FrameLayout
    var previousMenuitem:MenuItem?=null
    lateinit var sharedPreferences:SharedPreferences
 val restaurant= arrayListOf<Restaurants>()
    lateinit var username: TextView
    lateinit var userphonenumber: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        openFragment(HomeFragment(),"All Restaurants")
        sharedPreferences=getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawerlayout)

        coordinatelayout = findViewById(R.id.CoordinateLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationview = findViewById(R.id.NavigationView)

        navigationview.setCheckedItem(R.id.Home)
        val headerView = navigationview.getHeaderView(0)
       username= headerView.findViewById(R.id.UserName)
        userphonenumber= headerView.findViewById(R.id.UserPhoneNumber)

        val name = sharedPreferences.getString("Name","DEFAULT_NAME")
        val mobile = "+91-" + sharedPreferences.getString("MobNo","DEFAULT_MOB")

        username.text=name
        userphonenumber.text=mobile

        navigationview.setNavigationItemSelectedListener {

            if(previousMenuitem!=null){
                previousMenuitem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuitem=it
            when (it.itemId) {
                R.id.Home -> {
                    openFragment(HomeFragment(),"All Restaurants")

                    drawerLayout.closeDrawers()
                }
                R.id.MyProfile -> {
                    openFragment(MyProfileFragment(),"My Profile")
                    drawerLayout.closeDrawers()
                }
                R.id.FavRestaurants -> {
                    openFragment(FavRestaurantsFragment(),"Favourite Restaurants")
                    drawerLayout.closeDrawers()
                }
                R.id.OrderHistory -> {
                    openFragment(OrderHistoryFragment(),"Order History")
                    drawerLayout.closeDrawers()
                }

                R.id.LogOut->{
                    val dialog = AlertDialog.Builder(this@MainActivity2)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to exit?")

                    dialog.setPositiveButton("YES") { text, listener ->
                        sharedPreferences.edit().clear().apply()
                        val intent=Intent(this@MainActivity2,MainActivity::class.java)
                        startActivity(intent)
                    }
                    dialog.setNegativeButton("NO") { text, listener ->
                        //close dialog and open home
                        openFragment(HomeFragment(),"All Restaurants")
                        drawerLayout.closeDrawers()
                    }
                    dialog.create()
                    dialog.show()
                }

            }


            return@setNavigationItemSelectedListener true
        }
        frame = findViewById(R.id.frame)
        setUpToolBar()
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity2, drawerLayout, R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
   override fun onOptionsItemSelected(item:MenuItem):Boolean{
       val id=item.itemId
       if(id==android.R.id.home){
           drawerLayout.openDrawer(GravityCompat.START)

       }

           return super.onOptionsItemSelected(item)
    }
    fun openFragment(fragment: Fragment, title:String){

        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
            .commit()
        supportActionBar?.title=title

    }

    override fun onBackPressed(){
        val frag=supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is HomeFragment -> {
                openFragment(HomeFragment(), "All Restaurants")
                navigationview.setCheckedItem(R.id.Home)
            }

            else->{
                finishAffinity()
            }
        }


    }
    fun on(search:EditText){
        for(i in 0 until restaurant.size){
            val resname=restaurant[i].restaurantName


        }
    }


}