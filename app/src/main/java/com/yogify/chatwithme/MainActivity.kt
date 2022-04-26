package com.yogify.chatwithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.yogify.chatwithme.Adapter.UserAdapter
import com.yogify.chatwithme.Authantication.LoginActivity
import com.yogify.chatwithme.ModelClass.User
import com.yogify.chatwithme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var userlist: ArrayList<User>
    lateinit var adapter: UserAdapter
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        auth=Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference()
        userlist = ArrayList()
        adapter = UserAdapter(applicationContext, userlist)
        binding.recyclearview.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclearview.adapter=adapter

        databaseReference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()
                for (postsnapshot in snapshot.children) {
                    var user = postsnapshot.getValue(User::class.java)
                    if(!auth.currentUser!!.uid.equals(user!!.uid))
                    {
                        userlist.add(user!!)
                    }
                }
                adapter.notifyDataSetChanged()
                binding.progessbar.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progessbar.visibility=View.GONE

            }

        })
    }

    fun Signout() {
        Firebase.auth.signOut()
        finishAfterTransition()
        startActivity(Intent(application, LoginActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var menuitemid = item.itemId
        if (menuitemid.equals(R.id.logout)) {
            Signout()
        }
        return true
    }
}