package com.saxomoose.frontend.ui.home.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.saxomoose.frontend.data.TransactionDao
import com.saxomoose.frontend.entities.TransactionItemEntity
import com.saxomoose.frontend.models.Item
import com.saxomoose.frontend.models.TransactionItem
import kotlinx.coroutines.launch

class TransactionViewModelFactory(
    private val transactionDao: TransactionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return TransactionViewModel(transactionDao) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}

class TransactionViewModel(
    private val transactionDao: TransactionDao
) : ViewModel() {
    // Data source is memory.
    private var _transactionItems = mutableListOf<TransactionItem>()
    val transactionItems: List<TransactionItem> = _transactionItems

    fun addItem(item: Item) {
        val transactionItem = _transactionItems.firstOrNull { it.id == item.id }
        if (transactionItem == null) {
            val newTransactionItem = TransactionItem(item.id, item.name, 1)
            _transactionItems.add(newTransactionItem)
        } else {
            transactionItem.quantity++
        }
    }

    fun removeItem(transactionItem: TransactionItem) {
        val currentTransactionItem = _transactionItems.firstOrNull { it.id == transactionItem.id }
        if (currentTransactionItem != null) {
            if (currentTransactionItem.quantity > 1) {
                currentTransactionItem.quantity--
            } else {
                _transactionItems.remove(currentTransactionItem)
            }
        }
    }

    fun saveTransaction(transactionItemEntities: List<TransactionItemEntity>) {
        viewModelScope.launch {
            transactionDao.insertTransactionWithItems(transactionItemEntities)
        }
        _transactionItems.clear()
    }
}