package ca.sfu.cmpt362.group4.streamline.ui.tv_shows

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ExpandableListView.OnChildClickListener
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import com.bumptech.glide.Glide
import ca.sfu.cmpt362.group4.streamline.databinding.ItemTvShowBinding

class TvShowsViewHolder(private val binding: ItemTvShowBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(tvShow: TvShows){
        binding.textTitle.text = tvShow.name
        val year = tvShow.first_air_date.split("-").firstOrNull() // Assumes format is "YYYY-MM-DD"
        binding.textReleaseDate.text = year

        Log.d("Adapter", "${tvShow.poster_path}")
        Log.d("Adapter", "${tvShow.backdrop_path}")


        Glide.with(binding.imagePoster.context)
            .load("https://image.tmdb.org/t/p/w500${tvShow.poster_path}")
            .into(binding.imagePoster)
    }
}

class TvShowsAdapter( private var tvShows: List<TvShows>,
                      private val clickListener: (TvShows) -> Unit
): RecyclerView.Adapter<TvShowsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTvShowBinding.inflate(layoutInflater, parent, false)
        return TvShowsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TvShowsViewHolder, position: Int) {
        val tvShow = tvShows[position]
        holder.bind(tvShow)
        holder.itemView.setOnClickListener { clickListener(tvShow) }
    }

    override fun getItemCount() = tvShows.size

    fun updateTvShows(newTvShows: List<TvShows>) {
        tvShows = newTvShows
        notifyDataSetChanged()
    }
}