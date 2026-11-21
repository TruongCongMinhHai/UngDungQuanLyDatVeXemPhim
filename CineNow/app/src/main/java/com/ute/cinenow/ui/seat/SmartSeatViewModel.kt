package com.ute.cinenow.ui.seat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SmartSeatViewModel : ViewModel() {

    private val _preference = MutableLiveData(SeatPreference())
    val preference: LiveData<SeatPreference> get() = _preference

    private val _suggestion = MutableLiveData<SeatSuggestion?>()
    val suggestion: LiveData<SeatSuggestion?> get() = _suggestion

    private val _selectedSeats = MutableLiveData<List<Seat>>(emptyList())
    val selectedSeats: LiveData<List<Seat>> get() = _selectedSeats

    var initialSmartSuggestionSeats: List<Seat> = emptyList()
        private set

    fun setPreference(pref: SeatPreference) {
        _preference.value = pref
    }

    fun applySmartSuggestion(suggestionResult: SeatSuggestion) {
        _suggestion.value = suggestionResult
        initialSmartSuggestionSeats = suggestionResult.seats
        _selectedSeats.value = suggestionResult.seats
    }

    fun toggleSeatSelection(seat: Seat, maxAllowed: Int): Boolean {
        val current = _selectedSeats.value.orEmpty().toMutableList()
        val exists = current.any { it.seatId == seat.seatId }

        if (exists) {
            current.removeAll { it.seatId == seat.seatId }
            _selectedSeats.value = current
            return true
        } else {
            if (current.size >= maxAllowed) {
                return false
            }
            current.add(seat)
            _selectedSeats.value = current.sortedBy { it.seatId }
            return true
        }
    }

    fun resetToInitialSuggestion() {
        _selectedSeats.value = initialSmartSuggestionSeats
    }
}