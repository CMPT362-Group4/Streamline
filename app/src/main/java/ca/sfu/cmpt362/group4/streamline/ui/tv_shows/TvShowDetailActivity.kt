package ca.sfu.cmpt362.group4.streamline.ui.tv_shows

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityTvShowDetailBinding
import ca.sfu.cmpt362.group4.streamline.repositories.TvShowsRepository
import ca.sfu.cmpt362.group4.streamline.room.DAOs.TvShowsDao
import ca.sfu.cmpt362.group4.streamline.room.databases.TvShowsDatabase
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModelFactory
import com.bumptech.glide.Glide

class TvShowDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTvShowDetailBinding
    private lateinit var tvShows: TvShows

    private lateinit var tvShowsViewModel: TvShowsViewModel
    private lateinit var tvShowsDao: TvShowsDao
    private lateinit var tvShowsRepository: TvShowsRepository

    private lateinit var ratingBar: RatingBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvShowDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable the back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.back_arrow)

        supportActionBar?.title = ""
        formatLayout()

        tvShowsDao = TvShowsDatabase.getInstance(this).tvShowsDao
        tvShowsRepository = TvShowsRepository(tvShowsDao)
        tvShowsViewModel = ViewModelProvider(this, TvShowsViewModelFactory(tvShowsRepository))[TvShowsViewModel::class.java]


        tvShows = intent.getParcelableExtra<TvShows>("tvShows")!!

        tvShows?.let {
            binding.textTitle.text = it.name
            binding.textOverview.text = it.overview
            Log.d("Details", it.poster_path)
            Log.d("Details", it.backdrop_path)
            Log.d("Details", it.overview)

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.poster_path}")
                .into(binding.imagePoster)

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.backdrop_path}")
                .into(binding.imageBackdrop)
        }
        handleAddButton()
        handleRatingBar()
    }

    fun formatLayout(){
        val previousTitle = intent.getStringExtra("previousPageTitle")

        // Coming from HomeTvShowsFragment
        if (previousTitle == "HomeTvShows") {
            Log.d("HomeTvShowsDetailsActivity", "Coming from Home")
            binding.buttonAdd.visibility = View.GONE
            binding.buttonDelete.visibility = View.VISIBLE

            //layout rating bar relative to delete button
            val params = binding.ratingBar.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.button_delete)
            binding.ratingBar.layoutParams = params

            handleDeleteButton()
        }
        // Not coming from HomeFragment
        else {
            Log.d("TvShowsDetailsActivity", "Not coming from Home")
            binding.buttonAdd.visibility = View.VISIBLE
            binding.buttonDelete.visibility = View.GONE

            handleAddButton()
        }
    }

    fun handleRatingBar(){
        ratingBar = binding.ratingBar

        ratingBar.rating = tvShows.rating

        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                tvShows?.let {
                    tvShowsViewModel.updateTvShowsRating(it.databaseId, rating)
                }
            }
        }

    }

    fun handleAddButton(){
        val buttonAdd = binding.buttonAdd
        buttonAdd.setOnClickListener {
            tvShows?.let {
                // Using api service id which is unique to tvShows
                Log.d("TvShowsDetailActivity", "${tvShows.id}")
                if (!tvShowsViewModel.isTvShowsInList(tvShows.id)) {
                    tvShows.rating = ratingBar.rating
                    tvShowsViewModel.insertTvShows(tvShows)
                    Toast.makeText(this, "TvShows added to your list", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "TvShows is already in your list", Toast.LENGTH_SHORT).show()
                }

                finish()
            }
        }
    }

    fun handleDeleteButton(){
        val buttonDelete = binding.buttonDelete

        binding.buttonDelete.setOnClickListener {
            tvShows?.let {
                // Delete the tvShows from database
                tvShowsViewModel.deleteTvShows(it)

                // Display a message
                Toast.makeText(this, "TvShows deleted from your list", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                finish() // Close the current activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}