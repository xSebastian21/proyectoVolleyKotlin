package com.example.proyectovolley

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.proyectovolley.modelo.Producto
import org.json.JSONException


class MainActivityListarProductos: AppCompatActivity() {
    private lateinit var listaProductos:MutableList<Producto>
    private lateinit var listViewProductos: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_listar_productos)
        listaProductos = mutableListOf()
        listViewProductos = findViewById(R.id.listaProductos)
        obtenerProductos()
    }

    // Funcion que realiza una peticion a la api para obtener todos los productos
    private fun obtenerProductos(){
        val url = "https://santiagov.pythonanywhere.com/producto"
        val queue = Volley.newRequestQueue(this)
        val jsonCategorias = JsonArrayRequest(Request.Method.GET,url,null,
            {   response ->
                try {
                    for (i in 0   until  response.length() ){
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val codigo = jsonObject.getInt("proCodigo")
                        val nombre = jsonObject.getString("proNombre")
                        val precio = jsonObject.getInt("proPrecio")
                        val categoria = jsonObject.getInt("proCategoria")
                        val urlImagen = jsonObject.getString("proFoto")
                        val producto = Producto(id,codigo,nombre,precio,categoria,urlImagen)
                        listaProductos.add(producto)
                    }
                    /*
                     Se crea un objeto de tipo adaptador donde se pasa como parametro el layout creado y la lista
                     de productos
                     */
                    val adaptador = Adaptador(this,
                        R.layout.layout_producto,listaProductos)
                    listViewProductos.adapter = adaptador
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

        },{ error ->
                Toast.makeText(this,error.message, Toast.LENGTH_LONG).show()
            })
        queue.add(jsonCategorias)
    }
}