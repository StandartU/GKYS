package com.example.walkiepaws

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class LivingRoomFragment : Fragment() {
    private lateinit var characterImage: ImageView // Выносим в поле класса для повторного использования

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_living_game_screen, container, false)

        characterImage = view.findViewById(R.id.imageCharacter) // Убедитесь, что в вашем layout есть ImageView с этим ID
        updateCharacterImage()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateCharacterImage()
    }

    private fun updateCharacterImage() {
        if (view != null) { // Проверяем, что View существует
            characterImage.setImageResource(DataManager.characters[DataManager.currentCharacterIndex])
        }
    }
}