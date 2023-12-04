package ca.sfu.cmpt362.group4.streamline.ui.tv_shows

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import com.bumptech.glide.Glide

class TvShowsViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

    private val textTitle: TextView = view.findViewById(R.id.text_title)
    private val textReleaseDate: TextView = view.findViewById(R.id.text_release_date)
    private val imagePoster: ImageView = view.findViewById(R.id.image_poster)
    private val ratingBar: RatingBar? = view.findViewById(R.id.rating_bar)


    fun bind(tvShow: TvShows){
        textTitle.text = tvShow.name
        val year = tvShow.first_air_date.split("-").firstOrNull() // Assumes format is "YYYY-MM-DD"
        textReleaseDate.text = year

        Log.d("Adapter", "${tvShow.poster_path}")
        Log.d("Adapter", "${tvShow.backdrop_path}")


        Glide.with(imagePoster.context)
            .load("https://image.tmdb.org/t/p/w500${tvShow.poster_path}")
            .into(imagePoster)

        ratingBar?.rating = tvShow.rating
    }
}

class TvShowsAdapter( private var tvShows: List<TvShows>,
                      private val clickListener: (TvShows) -> Unit,
                      private val layoutResId: Int
): RecyclerView.Adapter<TvShowsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(layoutResId, parent, false)
        return TvShowsViewHolder(view)
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