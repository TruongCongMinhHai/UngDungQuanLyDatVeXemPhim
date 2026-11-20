package com.ute.cinenow

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ute.cinenow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Các màn hình có hiện thanh điều hướng dưới (Đã bổ sung ticketHistoryFragment)
    private val bottomNavScreens = setOf(
        R.id.homeFragment,
        R.id.exploreFragment,
        R.id.cineTogetherFragment,
        R.id.ticketsFragment,
        R.id.ticketHistoryFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Đẩy nội dung ra khỏi thanh trạng thái và thanh điều hướng hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom
            )
            insets
        }

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)

        // Quản lý hiển thị Bottom Navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility =
                if (destination.id in bottomNavScreens) View.VISIBLE else View.GONE

            // Khi ở màn Lịch sử vé, giữ sáng icon "Vé của tôi" trên thanh menu dưới
            if (destination.id == R.id.ticketHistoryFragment) {
                binding.bottomNav.menu.findItem(R.id.ticketsFragment)?.isChecked = true
            }
        }
    }
}