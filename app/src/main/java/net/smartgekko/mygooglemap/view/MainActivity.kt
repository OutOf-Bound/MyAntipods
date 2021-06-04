package net.smartgekko.mygooglemap.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.smartgekko.mygooglemap.R
import net.smartgekko.mygooglemap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.rootLayout, GoogleMapsFragment())
                .commitAllowingStateLoss()
        }
    }
}


