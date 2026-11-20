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

        bindTicketData()
        setupClickListeners()
    }

    /**
     * Lấy dữ liệu vé dựa vào ticket_key và binding động lên giao diện
     */
    private fun bindTicketData() {
        val ticketKey = arguments?.getString("ticket_key") ?: "dune"
        val ticket = TicketDataRepository.getTicket(ticketKey)

        // Thông tin rạp & phim
        binding.tvCinemaName.text = ticket.cinemaName
        binding.tvFormatBadge.text = ticket.formatTag
        binding.tvAgeRating.text = ticket.ageRating
        binding.tvMovieTitle.text = ticket.movieTitle
        binding.tvMovieSubtitle.text = ticket.movieSubtitle
        binding.imgDetailPoster.setImageResource(ticket.posterResId)

        // Lịch chiếu
        binding.tvShowTime.text = ticket.showTime
        binding.tvShowDate.text = ticket.showDate
        binding.tvShowDayOfWeek.text = ticket.showDayOfWeek
        binding.tvRoomName.text = ticket.roomName
        binding.tvRoomFloor.text = ticket.roomFloor

        // Thông tin ghế
        binding.tvSeatsCountHeader.text = ticket.seatCountHeader
        binding.tvSeatsDetail.text = ticket.seatsDetail
        binding.tvSeatsBadge.text = ticket.seatsBadge

        // Mã đặt chỗ
        binding.tvBookingCode.text = ticket.bookingCode

        // Bảng giá chi tiết
        binding.tvTicketPriceLabel.text = ticket.ticketPriceLabel
        binding.tvTicketPriceValue.text = ticket.ticketPriceValue

        // Combo bắp nước (ẩn nếu không có)
        if (ticket.comboLabel != null && ticket.comboValue != null) {
            binding.layoutComboRow.visibility = View.VISIBLE
            binding.tvComboLabel.text = ticket.comboLabel
            binding.tvComboValue.text = ticket.comboValue
        } else {
            binding.layoutComboRow.visibility = View.GONE
        }

        // Voucher ưu đãi (ẩn nếu không có)
        if (ticket.discountLabel != null && ticket.discountValue != null) {
            binding.layoutDiscountRow.visibility = View.VISIBLE
            binding.tvDiscountLabel.text = ticket.discountLabel
            binding.tvDiscountValue.text = ticket.discountValue
        } else {
            binding.layoutDiscountRow.visibility = View.GONE
        }

        binding.tvTotalPrice.text = ticket.totalPrice
        binding.tvPaymentMethod.text = ticket.paymentMethod

        // Thẻ nhóm xem cùng CineTogether (chỉ hiện khi vé thuộc nhóm)
        binding.cardGroupInfo.visibility = if (ticket.isGroupTicket) View.VISIBLE else View.GONE
        if (ticket.isGroupTicket && ticket.groupName != null) {
            binding.tvGroupName.text = ticket.groupName
        }
    }

    private fun setupClickListeners() {
        // Nút Back trên CineHeader
        binding.header.onBackClick = {
            if (!findNavController().popBackStack()) {
                findNavController().navigate(R.id.ticketsFragment)
            }
        }

        // Sao chép Mã đặt chỗ (Booking ID) vào Clipboard
        binding.btnCopyBookingCode.setOnClickListener {
            it.animatePress {
                val bookingCode = binding.tvBookingCode.text.toString().trim()
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("BookingCode", bookingCode)
                clipboard.setPrimaryClip(clip)
                showCineMessage("Đã sao chép mã đặt chỗ: $bookingCode")
            }
        }

        // Bấm vào Nhóm CineTogether -> Chuyển sang màn CineTogether (#19)
        binding.cardGroupInfo.setOnClickListener {
            it.animatePress {
                findNavController().navigate(R.id.cineTogetherFragment)
            }
        }

        // Nút Lưu vé vào thư viện ảnh
        binding.btnSaveTicketImage.setOnClickListener {
            it.animatePress {
                showCineMessage("Đã lưu vé điện tử vào thư viện ảnh")
            }
        }

        // Bấm vào mã QR lớn hoặc Barcode
        val qrBarcodeAction: (View) -> Unit = { view ->
            view.animatePress {
                showCineMessage("Mã vé đã sẵn sàng để quét tại quầy soát vé")
            }
        }
        binding.imgLargeQrCode.setOnClickListener(qrBarcodeAction)
        binding.imgBarcode.setOnClickListener(qrBarcodeAction)
    }

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