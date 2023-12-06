package com.example.dh_a3_prog3210

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

    /**
     * Called when the activity is first created. This is where you should
     * do all of your normal static set up: create views, bind data to lists,
     * etc. This method also provides you with a [Bundle] containing the
     * activity's previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down, then this [Bundle] contains the data it
     *     most recently supplied in [onSaveInstanceState]. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        // find the toolbar in the activity layout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // create and set up the ActionBarDrawerToggle for the drawerlayout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WelcomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_welcome)
        }
    }

    /**
     * Called when a menu item in the NavigationView is selected. Performs
     * the corresponding action based on the selected item.
     *
     * @param item The selected menu item.
     * @return `true` to consume the event and handle it, `false` to allow
     *     normal menu processing to proceed.
     */
    override fun onNavigationItemSelected (item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_welcome -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WelcomeFragment()).commit()
            R.id.nav_game -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GameFragment()).commit()
            R.id.nav_high_scores -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HighScoresFragment()).commit()
            R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
