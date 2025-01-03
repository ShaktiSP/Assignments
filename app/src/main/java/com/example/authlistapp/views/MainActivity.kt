package com.example.authlistapp.views

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.authlistapp.adapter.PostAdapter
import com.example.authlistapp.databinding.ActivityMainBinding
import com.example.authlistapp.model.PostListResponseItem
import com.example.authlistapp.network.PostRepository
import com.example.authlistapp.utils.Status
import com.example.authlistapp.viewModel.AuthViewModelFactory
import com.example.authlistapp.viewModel.PostViewModel
import com.example.myfirebaseapplication.utils.Preference

class MainActivity : AppCompatActivity() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: ActivityMainBinding
    private var list = ArrayList<PostListResponseItem>()

    private val postViewModel by viewModels<PostViewModel> {
        AuthViewModelFactory(Application(), PostRepository())
    }

    private var currentPage = 1
    private val limit = 10
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observers()
        postViewModel.bookingApi(currentPage, limit)

        binding.btnWork.setOnClickListener {
            startActivity(Intent(this, WorkManagerActivity::class.java))
        }

        binding.btnLog.setOnClickListener {
            Preference.getInstance(this).putData(Preference.Keys.isUserLogin, false)
            startActivity(Intent(this,LogInActivity::class.java))
            finishAffinity()
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(this, list)
        binding.recyclerView.adapter = postAdapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItemPosition + 1 >= totalItemCount) {
                    // Load next page
                    isLoading = true
                    currentPage++
                    postViewModel.bookingApi(currentPage, limit)
                }
            }
        })
    }

    private fun observers() {
        postViewModel.postResponse.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { data ->
                        list.addAll(data)
                        postAdapter.notifyDataSetChanged()

                        if (list.isNullOrEmpty()){
                            binding.progress.visibility = View.VISIBLE
                        } else {
                            binding.progress.visibility = View.GONE
                        }
                    }
                    isLoading = false
                    binding.progressBar.visibility = View.GONE
                }

                Status.LOADING -> {
                    if (currentPage == 1) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }

                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    isLoading = false
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}
