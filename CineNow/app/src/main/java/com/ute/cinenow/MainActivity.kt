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

    private var isTabScreen = false
    private var isKeyboardVisible = false

    // Chỉ hiện bottom nav ở màn tab và khi không có bàn phím (tránh nó nhảy lên trên bàn phím)
    private fun updateBottomNavVisibility() {
        binding.bottomNav.visibility =
            if (isTabScreen && !isKeyboardVisible) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Đẩy nội dung ra khỏi thanh trạng thái, thanh điều hướng hệ thống và bàn phím
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
                        or WindowInsetsCompat.Type.ime()
            )
            view.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom
            )

            // Bàn phím hiện hoặc ẩn thì cập nhật lại việc hiện bottom nav
            val keyboardNow = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (keyboardNow != isKeyboardVisible) {
                isKeyboardVisible = keyboardNow
                updateBottomNavVisibility()
            }
            insets
        }

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)

        // Ẩn bottom nav ở các màn hình như đăng nhập, chọn ghế, chi tiết vé...
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isTabScreen = destination.id in bottomNavScreens
            updateBottomNavVisibility()
        }
    }
}