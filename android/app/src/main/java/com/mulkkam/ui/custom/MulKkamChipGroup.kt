package com.mulkkam.ui.custom

import android.content.Context
import android.util.AttributeSet

class MulKkamChipGroup<T>
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : FlowLayout(context, attrs) {
        private var adapter: MulKkamChipGroupAdapter<T>? = null

        val selectedItems: List<T>
            get() = adapter?.getSelectedItems().orEmpty()

        fun setAdapter(adapter: MulKkamChipGroupAdapter<T>) {
            this.adapter = adapter
        }

        fun setItems(items: List<T>) {
            val currentAdapter =
                adapter
                    ?: throw IllegalStateException("Adapter is not set. Please call setAdapter() before setting items.")

            currentAdapter.submitList(items)
            removeAllViews()
            currentAdapter.buildChips().forEach { chip ->
                addView(chip)
            }
        }
    }
