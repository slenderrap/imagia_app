package com.project.imagia.ui.account

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.project.imagia.databinding.FragmentAccountBinding

class AccountFragment:Fragment(){

    private var _binding : FragmentAccountBinding ? = null
    private val binding get() = _binding !!
    private val viewModel: AccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState:Bundle?
    ):View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
               val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("nom", "Carregant") ?: "Carregant"
        val password = sharedPreferences.getString("contrasenya", "Carregant")?.let { "*".repeat(it.length) } ?: "Carregant"
        val mail = sharedPreferences.getString("mail", "Carregant") ?: "Carregant"
        val nickname = sharedPreferences.getString("nickname", "Carregant") ?: "Carregant"
        val telephone = sharedPreferences.getString("telefon", "Carregant") ?: "Carregant"

        binding.userTextView.setText(username)
        binding.passwordTextView.setText(password)
        binding.mailTextView.setText(mail)
        binding.nicknameTextView.setText(nickname)
        binding.telephoneTextView.setText(telephone)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
