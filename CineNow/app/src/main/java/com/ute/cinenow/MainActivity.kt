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

    // Các màn hình có hiện thanh điều hướng dưới
    private val bottomNavScreens = setOf(
        R.id.homeFragment,
        R.id.exploreFragment,
        R.id.cineTogetherFragment,
        R.id.ticketsFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Chỉ áp top inset lên root (đẩy nội dung xuống khỏi status bar)
        // Áp bottom inset riêng lên bottomNav để nó không bị che bởi system nav bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            // Chỉ áp left/top/right lên root – KHÔNG áp bottom
            view.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = 0   // ← Đổi từ bars.bottom thành 0
            )
            // Áp bottom inset trực tiếp lên BottomNavigationView
            binding.bottomNav.updatePadding(
                bottom = bars.bottom
            )
            insets
        }

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)

        // Ẩn bottom nav ở các màn hình như đăng nhập, chọn ghế, chi tiết vé...
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility =
                if (destination.id in bottomNavScreens) View.VISIBLE else View.GONE
        }
    }
}