package com.project.imagia.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import okhttp3.OkHttpClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.imagia.databinding.FragmentHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private lateinit var adapter: HistoryAdapter
    private val binding get() = _binding!!
    private var token: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historyViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)


        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        token = sharedPreferences.getLong("token", 0)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter(emptyList())
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = adapter
        loadHistoryFromServer()
    }

    private fun loadHistoryFromServer() {

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://imagia5.ieti.site/api/usuaris/historial")
                    .addHeader("Authorization","Bearer ${token}")
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.i("OUTPUT",responseBody.toString())
                if (response.isSuccessful){
                    val historyList = parseHistoryJson(JSONObject(responseBody).get("data").toString())
                    withContext(Dispatchers.Main) {
                        adapter.updateData(historyList)
                    }
                }else{
                    Log.e("ERROR HISTORY","Error en la peticion ${response.code}: ${response.message}")
                }

            }catch (e: Exception){
                Log.e("ERROR HISTORY","Error al general el historial\n", e)
            }
        }
    }

    private fun parseHistoryJson(jsonString: String): List<HistoryItem> {
        val list = mutableListOf<HistoryItem>()
        Log.e("JSON",jsonString)
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val answer = jsonObject.getString("answer")
            val promptDate = jsonObject.getString("prompt_date")
            list.add(HistoryItem(answer, promptDate))
        }
        return list
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}