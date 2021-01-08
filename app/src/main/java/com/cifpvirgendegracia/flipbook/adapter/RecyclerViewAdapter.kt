package com.cifpvirgendegracia.flipbook.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.model.Libro
import com.cifpvirgendegracia.flipbook.ui.buscar.BuscarFragment
import com.cifpvirgendegracia.flipbook.ui.detalle.DetalleLibroFragment
import com.cifpvirgendegracia.flipbook.util.Utilidades


class RecyclerViewAdapter(private val mContext: Context, mData: List<Libro>, private val view: BuscarFragment) :
        RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    private var mData: List<Libro> = mData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View
        val mInflater = LayoutInflater.from(mContext)
        view = mInflater.inflate(R.layout.cardview_item, parent, false)
        return MyViewHolder(view)
    }

    public fun setData(datos: ArrayList<Libro>) {
        this.mData = datos
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_book_title.text = mData[position].titulo
        holder.img_book_thumbnail.setImageBitmap(Utilidades.StringToBitMap(mData[position].foto))
        holder.cardView.setOnClickListener {
            //Ver detalles del libro.

            val activity = view.context as AppCompatActivity?
            val myFragment: Fragment = DetalleLibroFragment(mData[position])
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.parent_view, myFragment).addToBackStack(null).commit()

        }

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