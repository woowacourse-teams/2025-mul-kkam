package com.mulkkam.ui.custom.chip

import android.content.Context
import androidx.core.graphics.toColorInt

class MulKkamChipGroupAdapter<T>(
    private val context: Context,
    private val labelProvider: (T) -> String,
    private val colorProvider: (T) -> String,
    private val isMultiSelect: Boolean = false,
    private val requireSelection: Boolean = false,
    private val onItemSelected: (List<T>) -> Unit,
) {
    private val selectedItems = mutableSetOf<T>()
    private val chips = mutableMapOf<T, MulKkamChipView>()
    private var items: List<T> = emptyList()

    fun submitList(newItems: List<T>) {
        items = newItems
        selectedItems.clear()
        chips.clear()
    }

    fun buildChips(): List<MulKkamChipView> =
        items.mapIndexed { index, item ->
            MulKkamChipView(context).apply {
                text = labelProvider(item)
                baseColor = colorProvider(item).toColorInt()

                initRequiredSelection(index, item)
                initClickListener(item)

                chips[item] = this
            }
        }

    private fun MulKkamChipView.initRequiredSelection(
        index: Int,
        item: T,
    ) {
        if (requireSelection && index == 0) {
            isSelectedChip = true
            selectedItems.add(item)
            onItemSelected(getSelectedItems())
        } else {
            isSelectedChip = false
        }
    }

    private fun MulKkamChipView.initClickListener(item: T) {
        setOnClickListener {
            handleSelection(this, item)
            onItemSelected(getSelectedItems())
        }
    }

    private fun handleSelection(
        chip: MulKkamChipView,
        item: T,
    ) {
        if (isMultiSelect) {
            handleMultiSelect(chip, item)
        } else {
            handleSingleSelect(item, chip)
        }
    }

    private fun handleMultiSelect(
        chip: MulKkamChipView,
        item: T,
    ) {
        val isCurrentlySelected = chip.isSelectedChip

        if (isCurrentlySelected && requireSelection && selectedItems.size == 1) {
            return
        }

        chip.isSelectedChip = !chip.isSelectedChip
        if (chip.isSelectedChip) {
            selectedItems.add(item)
        } else {
            selectedItems.remove(item)
        }
    }

    private fun handleSingleSelect(
        item: T,
        chip: MulKkamChipView,
    ) {
        if (selectedItems.contains(item) && requireSelection) {
            return
        }

        chips.forEach { (_, view) ->
            view.isSelectedChip = false
        }

        chip.isSelectedChip = true
        selectedItems.clear()
        selectedItems.add(item)
    }

    fun getSelectedItems(): List<T> = selectedItems.toList()
}
