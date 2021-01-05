package com.cifpvirgendegracia.flipbook.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.model.Libro
import com.cifpvirgendegracia.flipbook.util.Utilidades


class RecyclerViewAdapter(private val mContext: Context, mData: List<Libro>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    private val mData: List<Libro> = mData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View
        val mInflater = LayoutInflater.from(mContext)
        view = mInflater.inflate(R.layout.cardview_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_book_title.text = mData[position].titulo
        holder.img_book_thumbnail.setImageBitmap(Utilidades.StringToBitMap(mData[position].foto))

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_book_title: TextView = itemView.findViewById<View>(R.id.book_title_id) as TextView
        var img_book_thumbnail: ImageView = itemView.findViewById<View>(R.id.book_img_id) as ImageView
        var cardView: CardView = itemView.findViewById<View>(R.id.cardview_id) as CardView

    }

}