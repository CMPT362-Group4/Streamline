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
import ca.sfu.cmpt362.group4.streamline.data_models.TvShow
import com.bumptech.glide.Glide

class TvShowsViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

    private val textTitle: TextView = view.findViewById(R.id.text_title)
    private val textReleaseDate: TextView = view.findViewById(R.id.text_release_date)
    private val imagePoster: ImageView = view.findViewById(R.id.image_poster)
    private val ratingBar: RatingBar? = view.findViewById(R.id.rating_bar)


    fun bind(tvShow: TvShow){
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

class TvShowsAdapter( private var tvShows: List<TvShow>,
                      private val clickListener: (TvShow) -> Unit,
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

    fun updateTvShows(newTvShows: List<TvShow>) {
        tvShows = newTvShows
        notifyDataSetChanged()
    }
}


class SearchTvShowAdapter : RecyclerView.Adapter<SearchTvShowAdapter.TvShowViewHolder>() {

    private var tvShows: List<TvShow> = emptyList()
    private var onItemClickListener: ((TvShow) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tv_show_search, parent, false)
        return TvShowViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val tvShow = tvShows[position]
        holder.bind(tvShow)
    }

    override fun getItemCount(): Int = tvShows.size

    fun submitList(newTvShows: List<TvShow>) {
        tvShows = newTvShows
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (TvShow) -> Unit) {
        onItemClickListener = listener
    }

    inner class TvShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val searchResultImage: ImageView = itemView.findViewById(R.id.searchResultImage)
        private val searchResultName: TextView = itemView.findViewById(R.id.searchResultName)
        private val searchResultReleaseDate: TextView =
            itemView.findViewById(R.id.searchResultReleaseDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tvShow = tvShows[position]
                    onItemClickListener?.invoke(tvShow)
                }
            }
        }

        fun bind(tvShow: TvShow) {
            // Load the image using Glide
            Glide.with(searchResultImage.context)
                .load("https://image.tmdb.org/t/p/w500${tvShow.poster_path}")
                .into(searchResultImage)

            searchResultName.text = tvShow.name
            searchResultReleaseDate.text = tvShow.first_air_date
        }
    }
}