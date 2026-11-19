package com.ute.cinenow.ui.tickets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ute.cinenow.R
import com.ute.cinenow.databinding.FragmentTicketDetailBinding
import com.ute.cinenow.utils.showCineMessage

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
        // 1. Nút Back trên CineHeader: lùi về hoặc sang ticketsFragment nếu backstack rỗng
        binding.header.onBackClick = {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.ticketsFragment)
            }
        }

        // 2. Sao chép Mã đặt chỗ (Booking ID) vào Clipboard hệ thống
        binding.btnCopyBookingCode.setOnClickListener {
            it.animatePress {
                val bookingCode = binding.tvBookingCode.text.toString().trim()
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("BookingCode", bookingCode)
                clipboard.setPrimaryClip(clip)
                showCineMessage("Đã sao chép mã đặt chỗ: $bookingCode")
            }
        }

        // 3. Bấm vào Nhóm CineTogether -> Chuyển sang màn Đi cùng nhóm (#17)
        binding.cardGroupInfo.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }

        // 4. Nút Lưu vé vào thư viện ảnh
        binding.btnSaveTicketImage.setOnClickListener {
            it.animatePress {
                showCineMessage("Đã lưu vé điện tử vào thư viện ảnh")
            }
        }

        // 5. Bấm vào mã QR lớn hoặc Barcode -> Nhắc nhở người dùng
        val qrBarcodeAction: (View) -> Unit = { view ->
            view.animatePress {
                showCineMessage("Mã vé đã sẵn sàng để quét tại quầy soát vé")
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