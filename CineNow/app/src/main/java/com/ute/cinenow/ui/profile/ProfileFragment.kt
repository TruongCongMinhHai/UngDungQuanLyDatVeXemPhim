package com.ute.cinenow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    /**
     * Thiết lập điều hướng thật tới các màn hình trong hệ thống
     */
    private fun setupClickListeners() {
        // 1. Bấm vào Avatar -> Chuyển sang Chỉnh sửa hồ sơ (#27)
        binding.imgAvatar.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.editProfileFragment)
            }
        }

        // 2. Nút Chỉnh sửa hồ sơ -> Chuyển sang Chỉnh sửa hồ sơ (#27)
        binding.btnEditProfile.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.editProfileFragment)
            }
        }

        // 3. Mục 1: Sở thích xem phim -> Chuyển sang Sở thích CineMatch (#13)
        binding.itemCineMatch.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.cineMatchPreferencesFragment)
            }
        }

        // 4. Mục 2: Nhóm CineTogether -> Chuyển sang Đi cùng nhóm (#17)
        binding.itemCineTogether.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }

        // 5. Mục 3: Lịch sử đặt vé -> Chuyển sang Lịch sử vé (#25)
        binding.itemTicketHistory.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.ticketHistoryFragment)
            }
        }

        // 6. Mục 4: Đổi mật khẩu -> Chuyển sang Đổi mật khẩu (#28)
        binding.itemChangePassword.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.changePasswordFragment)
            }
        }

        // 7. Nút Đăng xuất -> Quay về màn Đăng nhập (#1) và xóa stack điều hướng
        binding.btnLogout.setOnClickListener {
            it.animatePress {
                findNavController().navigate(
                    R.id.loginFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.nav_graph) { inclusive = true }
                    }
                )
            }
        }
    }

    // Hiệu ứng ấn nhả nút mượt mà
    private fun View.animatePress(action: () -> Unit) {
        animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).withEndAction {
            animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).withEndAction {
                action()
            }.start()
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}