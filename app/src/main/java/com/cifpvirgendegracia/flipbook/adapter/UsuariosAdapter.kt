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
import com.cifpvirgendegracia.flipbook.model.Usuario
import com.cifpvirgendegracia.flipbook.ui.detalle.DetalleLibroFragment
import com.cifpvirgendegracia.flipbook.util.Utilidades


/**
 * Usuarios adapter
 *
 * @property mContext
 * @property view
 * @constructor
 *
 * @param mData
 */
class UsuariosAdapter(
    private val mContext: Context,
    mData: List<Usuario>,
    private val view: Fragment,
) :
    RecyclerView.Adapter<UsuariosAdapter.MyViewHolder>() {
    private var mData: List<Usuario> = mData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View
        val mInflater = LayoutInflater.from(mContext)
        view = mInflater.inflate(R.layout.users_item, parent, false)
        return MyViewHolder(view)
    }

    /**
     * Set data
     *
     * @param datos
     */
    fun setData(datos: ArrayList<Usuario>) {
        this.mData = datos
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tv_book_title.text = mData[position].nombre



    }

    override fun getItemCount(): Int {
        return mData.size
    }

    /**
     * My view holder
     *
     * @constructor
     *
     * @param itemView
     */
    class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tv_book_title: TextView = itemView.findViewById<View>(R.id.usuarioNombreBuscar) as TextView


    }

}