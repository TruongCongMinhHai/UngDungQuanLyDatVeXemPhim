package com.ute.cinenow.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
     * Sự kiện click vào các button và danh mục trên trang
     */
    private fun setupClickListeners() {
        // Nút Chỉnh sửa hồ sơ
        binding.btnEditProfile.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show()
            }
        }

        // Mục 1: Sở thích xem phim
        binding.itemCineMatch.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Sở thích xem phim (CineMatch)", Toast.LENGTH_SHORT).show()
            }
        }

        // Mục 2: Nhóm CineTogether
        binding.itemCineTogether.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Nhóm CineTogether", Toast.LENGTH_SHORT).show()
            }
        }

        // Mục 3: Lịch sử đặt vé
        binding.itemTicketHistory.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Lịch sử đặt vé", Toast.LENGTH_SHORT).show()
            }
        }

        // Mục 4: Đổi mật khẩu
        binding.itemChangePassword.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Đổi mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút Đăng xuất
        binding.btnLogout.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "Đăng xuất tài khoản", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hiệu ứng ấn nhả mượt mà khi click
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