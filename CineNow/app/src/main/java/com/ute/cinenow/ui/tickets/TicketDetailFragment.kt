package com.ute.cinenow.ui.tickets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentTicketDetailBinding

class TicketDetailFragment : Fragment() {

    private var _binding: FragmentTicketDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    /**
     * Cấu hình toàn bộ sự kiện click trên màn Chi tiết vé
     */
    private fun setupClickListeners() {
        // 1. Nút Back quay lại màn trước
        binding.btnBack.setOnClickListener {
            it.animatePress {
                findNavController().popBackStack()
            }
        }

        // 2. Avatar góc phải -> Chuyển sang màn Cá nhân (#26)
        binding.imgTopAvatar.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.profileFragment)
            }
        }

        // 3. Sao chép Mã đặt chỗ (Booking ID) vào Clipboard hệ thống
        binding.btnCopyBookingCode.setOnClickListener {
            it.animatePress {
                val bookingCode = binding.tvBookingCode.text.toString().trim()
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("BookingCode", bookingCode)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Đã sao chép mã đặt chỗ: $bookingCode", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Bấm vào Nhóm CineTogether -> Chuyển sang màn Đi cùng nhóm (#17)
        binding.cardGroupInfo.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }

        // 5. Nút Lưu vé vào thư viện ảnh
        binding.btnSaveTicketImage.setOnClickListener {
            it.animatePress {
                Toast.makeText(requireContext(), "✅ Đã lưu vé vào thư viện ảnh thành công!", Toast.LENGTH_SHORT).show()
            }
        }

        // 6. Bấm vào mã QR lớn hoặc Barcode -> Nhắc nhở người dùng
        val qrBarcodeAction: (View) -> Unit = { view ->
            view.animatePress {
                Toast.makeText(requireContext(), "Mã vé đã sẵn sàng để quét tại quầy soát vé", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imgLargeQrCode.setOnClickListener(qrBarcodeAction)
        binding.imgBarcode.setOnClickListener(qrBarcodeAction)
    }

    // Hiệu ứng ấn nhả nút co nhún mượt mà
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