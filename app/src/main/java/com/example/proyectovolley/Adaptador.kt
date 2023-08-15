package com.example.proyectovolley

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.proyectovolley.modelo.Producto
import com.squareup.picasso.Picasso


class Adaptador: BaseAdapter {
    // Atributos
    lateinit var contexto: Context
    var layout: Int=0
    lateinit var listaProductos:List<Producto>

    /*
    Constructor que inicializa el objeto
     */
    constructor(contexto: Context, layout: Int, listaProductos: List<Producto>){
        this.contexto=contexto
        this.layout=layout
        this.listaProductos=listaProductos
    }
    /*
     Se obtiene el tamaño de la lista del adaptador
     */
    override fun getCount(): Int {
        return listaProductos.size
    }
    /*
     Obtiene el item  del elemento de acuerdo a posición
     */
    override fun getItem(posicion: Int): Any {
        return listaProductos[posicion]
    }

    override fun getItemId(posicion: Int): Long {
        return posicion.toLong()
    }
    /*
     Retorna la vista con los elementos
     */
    override fun getView(posicion: Int, vista: View?, parent: ViewGroup?): View {
        var v:View
        var inflater: LayoutInflater= LayoutInflater.from(contexto)
        v = inflater.inflate(R.layout.layout_producto,null)
        val txtNombre: TextView= v.findViewById(R.id.txtNombreProducto)
        txtNombre.text = listaProductos[posicion].nombre
        val txtCodigo:TextView=v.findViewById(R.id.txtCodigoProducto)
        txtCodigo.text = listaProductos[posicion].codigo.toString()
        val txtPrecio:TextView=v.findViewById(R.id.txtPrecioProducto)
        txtPrecio.text = listaProductos[posicion].precio.toString()
        val imgFoto: ImageView=v.findViewById(R.id.imgFoto)
        Picasso.get()
            .load(listaProductos[posicion].urlImagen)
            .resize(50,50)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(imgFoto)
        return v;
    }
}