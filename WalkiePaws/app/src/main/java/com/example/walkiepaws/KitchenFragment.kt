package com.example.walkiepaws

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment

class KitchenFragment : Fragment() {

    private lateinit var characterImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_kitchen_game_screen, container, false)
        characterImage = view.findViewById(R.id.imageCharacter)
        resetCharacterState()
        return view
    }

    override fun onResume() {
        super.onResume()
        if (isAdded && !isHidden) {
            showCharacterSmoothly()
        }
    }

    override fun onPause() {
        hideCharacterImmediately()
        super.onPause()
    }

    private fun resetCharacterState() {
        if (::characterImage.isInitialized) {
            characterImage.visibility = View.INVISIBLE
            characterImage.alpha = 0f
            characterImage.translationX = 0f
            characterImage.translationY = 0f
        }
    }

    fun hideCharacterImmediately() {
        view?.post {
            if (::characterImage.isInitialized) {
                characterImage.animate().cancel()
                characterImage.visibility = View.INVISIBLE
                characterImage.alpha = 0f
            }
        }
    }

    fun showCharacterSmoothly() {
        view?.post {
            if (::characterImage.isInitialized && isVisible) {
                updateCharacterImage()
                characterImage.apply {
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setDuration(400)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()
                }
            }
        }
    }

    private fun updateCharacterImage() {
        if (::characterImage.isInitialized &&
            DataManager.currentCharacterIndex in DataManager.characters.indices) {
            characterImage.setImageResource(DataManager.characters[DataManager.currentCharacterIndex])
        }
    }

    companion object {
        fun newInstance() = KitchenFragment()
    }
}