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
        characterImage.visibility = View.INVISIBLE
        characterImage.alpha = 0f

        updateCharacterImage()

        // Запуск анимации после отрисовки
        view.post { showCharacterWithAnimation() }

        return view
    }

    override fun onPause() {
        // Мгновенно скрываем персонажа при начале перехода
        if (::characterImage.isInitialized) {
            characterImage.visibility = View.INVISIBLE
            characterImage.alpha = 0f
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        updateCharacterImage()
        // Показываем с анимацией при возвращении на фрагмент
        view?.post { showCharacterWithAnimation() }
    }

    private fun updateCharacterImage() {
        if (view != null && ::characterImage.isInitialized &&
            DataManager.currentCharacterIndex in DataManager.characters.indices) {
            characterImage.setImageResource(DataManager.characters[DataManager.currentCharacterIndex])
        }
    }

    private fun showCharacterWithAnimation() {
        if (view != null && ::characterImage.isInitialized && characterImage.visibility != View.VISIBLE) {
            characterImage.apply {
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(5) // Укороченная анимация
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
        }
    }

    companion object {
        fun newInstance() = KitchenFragment()
    }
}