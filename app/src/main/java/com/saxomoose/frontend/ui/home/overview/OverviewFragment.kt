package com.saxomoose.frontend.ui.home.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.saxomoose.frontend.FrontEndApplication
import com.saxomoose.frontend.R
import com.saxomoose.frontend.databinding.FragmentOverviewBinding
import com.saxomoose.frontend.models.TransactionWrapper

// Displays the transactions.
class OverviewFragment : Fragment() {
    private lateinit var binding: FragmentOverviewBinding
    private val viewModel: OverviewViewModel by viewModels {
        OverviewViewModelFactory((activity?.application as FrontEndApplication).database.transactionDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TransactionAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerView.context,
            LinearLayoutManager(requireContext()).orientation
        )
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            if (!transactions.isNullOrEmpty()) {
                val data = mutableListOf<TransactionWrapper>()
                for (transaction in transactions) {
                    data.add(TransactionWrapper.TransactionRow(transaction.transactionEntity))
                    for (item in transaction.itemEntities) {
                        data.add(TransactionWrapper.TransactionItemRow(item))
                    }
                }
                adapter.submitList(data)
                binding.emptyDatasetTextview.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.emptyDatasetTextview.text =
                    String.format(getString(R.string.empty_dataset), "transactions")
                binding.recyclerView.visibility = View.GONE
                binding.emptyDatasetTextview.visibility = View.VISIBLE
            }
        }
    }
}