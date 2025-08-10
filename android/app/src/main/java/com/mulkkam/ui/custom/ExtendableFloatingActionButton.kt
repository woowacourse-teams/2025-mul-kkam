package com.mulkkam.ui.custom

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.children
import androidx.core.view.isVisible
import com.mulkkam.R
import com.mulkkam.databinding.LayoutExpandableFloatingActionButtonBinding
import com.mulkkam.ui.util.extensions.setSingleClickListener

class ExtendableFloatingActionButton
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : FrameLayout(context, attrs) {
        private val binding = LayoutExpandableFloatingActionButtonBinding.inflate(LayoutInflater.from(context), this, true)
        private var isOpen = false
        private val animationDuration = 200L

        private val outsideTouchView by lazy {
            View(context).apply {
                layoutParams =
                    LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT,
                    )
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    closeMenu()
                }
            }
        }

        init {
            binding.fabDrink.setOnClickListener { toggleMenu() }
        }

        fun <T> setMenuItems(
            items: List<ExtendableFloatingMenuItem<T>>,
            onItemClick: (ExtendableFloatingMenuItem<T>) -> Unit,
        ) {
            binding.llMenu.removeAllViews()

            items.forEach { item ->
                val menuView =
                    ExtendableFloatingMenu(context).apply {
                        setButtonLabel(item.buttonLabel)
                        setIcon(item.icon)
                        setIconLabel(item.iconLabel ?: "")
                        setSingleClickListener {
                            onItemClick(item)
                            closeMenu()
                        }
                        alpha = 0f
                        translationY = 0f
                        isVisible = false
                    }
                binding.llMenu.addView(menuView, 0)
            }
        }

        private fun toggleMenu() {
            if (isOpen) closeMenu() else openMenu()
        }

        private fun openMenu() {
            if (outsideTouchView.parent == null) {
                addView(outsideTouchView, 0) // 맨 아래에 추가
            }

            val animators = mutableListOf<Animator>()
            binding.fabDrink.setImageDrawable(getDrawable(context, R.drawable.ic_home_close))
            binding.llMenu.childrenReversed().forEachIndexed { index, view ->
                view.isVisible = true
                val translation = ObjectAnimator.ofFloat(view, "translationY", 0f, -((index + 1) * 12f.dpToPx()))
                val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
                animators.add(translation)
                animators.add(alpha)
            }
            AnimatorSet().apply {
                playTogether(animators)
                duration = animationDuration
                start()
            }
            isOpen = true
        }

        fun closeMenu() {
            val animators = mutableListOf<Animator>()
            binding.fabDrink.setImageDrawable(getDrawable(context, R.drawable.ic_home_drink))
            binding.llMenu.childrenReversed().forEach { view ->
                val translation = ObjectAnimator.ofFloat(view, "translationY", view.translationY, 0f)
                val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
                animators.add(translation)
                animators.add(alpha)
            }
            AnimatorSet().apply {
                playTogether(animators)
                duration = animationDuration
                start()
            }
            postDelayed({
                binding.llMenu.childrenReversed().forEach { it.isVisible = false }
                removeView(outsideTouchView)
            }, animationDuration)
            isOpen = false
        }

        private fun ViewGroup.childrenReversed(): List<View> = this.children.toList().asReversed()

        private fun Float.dpToPx(): Float = this * resources.displayMetrics.density
    }
