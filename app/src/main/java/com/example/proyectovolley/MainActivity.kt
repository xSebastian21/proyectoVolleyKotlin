package com.example.proyectovolley

import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager.Request
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectovolley.modelo.Categoria
import com.example.proyectovolley.modelo.Producto
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    lateinit var txtCodigo: EditText
    lateinit var txtNombre: EditText
    lateinit var txtPrecio: EditText
    lateinit var cbCategoria: Spinner
    lateinit var btnAgregar: Button
    lateinit var btnConsultar: Button
    lateinit var btnEliminar: Button
    lateinit var btnListar: Button
    lateinit var btnActualizar: Button
    lateinit var listaCategoria: MutableList<Categoria>
    lateinit var listaProductos:MutableList<Producto>
    private var  idCategoria: Int=0
    private var  idProducto: Int=0
    private val urlBase: String="https://JuanSebastian10.pythonanywhere.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtCodigo = findViewById(R.id.txtCodigo)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnListar = findViewById(R.id.btnListar)
        btnActualizar = findViewById(R.id.btnActualizar)
        cbCategoria = findViewById(R.id.cbCategoria)
        listaCategoria = mutableListOf<Categoria>()
        listaProductos = mutableListOf<Producto>()

        // Eventos de los botones
        btnAgregar.setOnClickListener { agregar() }
        btnConsultar.setOnClickListener { consultar() }
        btnEliminar.setOnClickListener {validarBorrar () }
        btnActualizar.setOnClickListener { actualizar() }
        btnListar.setOnClickListener{ listar() }

        /*
        *Se llama a la siguiente  funcion para consumir la api que nos
        * retorna las categorias y las guarda en la listaCategorias
        * */
        obtenerCategorias()
        /*
        Codigo que responde al evento de seleccionar un elemento
        del spinner  seleccionar una categoria
         */
        /* Creando un adaptador donde se van a capturar los datos de la listaCategorias -
        dicho adaptador se asocia al control visual de tipo spinner que es como un combobox */
        cbCategoria.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                idCategoria = listaCategoria[posicion].id
                Toast.makeText(this@MainActivity, "Seleccionado", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
    private fun obtenerCategorias() {
        val url = "https://JuanSebastian10.pythonanywhere.com/categoria"
        val queue = Volley.newRequestQueue(this)
        val jsonCategorias = JsonArrayRequest(com.android.volley.Request.Method.GET,url,null,
            Response.Listener<JSONArray>() { response ->
            try {
                val jsonArray = response
                for (i in 0  until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getInt("id")
                    val nombre = jsonObject.getString("catNombre")
                    val categoria = Categoria(id, nombre)
                    listaCategoria.add(categoria)
                }
                // Crear el adaptador y asociarle la lista de las categorias
                val adaptador = ArrayAdapter<Categoria>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listaCategoria
                )
                adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                cbCategoria.adapter = adaptador
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },  Response.ErrorListener{error ->
            Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            Log.e("Error: ",error.toString())
        })
    queue.add(jsonCategorias)
    }

    private fun agregar() {
        val url = "https://JuanSebastian10.pythonanywhere.com/producto"
        val queue = Volley.newRequestQueue(this)
        val progresBar = ProgressDialog.show(this, "Enviando Datos....", "Espere Por Favor")
        val resultadoPost = object : StringRequest(
            com.android.volley.Request.Method.POST,url,
            Response.Listener<String> { response ->
                progresBar.dismiss()
                Toast.makeText(this, "Producto agregado exitosamente",Toast.LENGTH_LONG).show()
                limpiar()
            }, Response.ErrorListener { error ->
                progresBar.dismiss()
                Toast.makeText(this,"Error ${error.message}",Toast.LENGTH_LONG).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String,String>()
                //val foto = bitmapToString(bitmap)
                parametros.put("proCodigo",txtCodigo.text.toString())
                parametros.put("proNombre",txtNombre.text.toString())
                parametros.put("proPrecio",txtPrecio.text.toString())
                parametros.put("proCategoria",idCategoria.toString())
                //parametros.put("proFoto",foto)
                return parametros

            }
        }
        queue.add(resultadoPost)
    }
    /*
    Función que realiza peticion a la API Para consultar un producto
     */

    private fun consultar() {
        val id = txtCodigo.text.toString()
        val url = "https://JuanSebastian10.pythonanywhere.com/producto/$id"
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            com.android.volley.Request.Method.GET, url, null,
            Response.Listener { response ->
                txtCodigo.setText(response.getString("proCodigo"))
                txtNombre.setText(response.getString("proNombre"))
                txtPrecio.setText(response.getString("proPrecio"))
                idProducto = response.getInt("id").toInt()
                idCategoria = response.getInt("proCategoria")
                var pos= 0
                for (categoria in listaCategoria){
                    if (categoria.id == idCategoria){
                        cbCategoria.setSelection(pos)
                        break
                    }
                    pos++
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this,error.toString(), Toast.LENGTH_LONG).show()
                Log.e("Error: ",error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun actualizar() {

        val url = "https://JuanSebastian10.pythonanywhere.com/producto/$idProducto"
        val queue = Volley.newRequestQueue(this)
        val resultadoPost = object : StringRequest(
            com.android.volley.Request.Method.PUT,url,
            Response.Listener<String> { response ->
                Toast.makeText(this, "Producto actualizado exitosamente",Toast.LENGTH_LONG).show()
                limpiar()
            }, Response.ErrorListener { error ->
                Toast.makeText(this,"Error ${error.message}",Toast.LENGTH_LONG).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String,String>()
                //val foto = bitmapToString(bitmap)
                parametros.put("proCodigo",txtCodigo.text.toString())
                parametros.put("proNombre",txtNombre.text.toString())
                parametros.put("proPrecio",txtPrecio.text.toString())
                parametros.put("proCategoria",idCategoria.toString())
                //parametros.put("proFoto",foto)
                return parametros

            }
        }
        queue.add(resultadoPost)
    }

    /*
    Función que valida si quiere eliminar un producto,
    si es afirmativo llama a la funcion que lo elimina
     */

    private fun validarBorrar() {
        val alertDialog: AlertDialog? = this?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        borrar() //Llamado función borrar
                        dialog.dismiss()
                    })
                setNegativeButton( "Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            }
            builder?.setMessage("Está seguro de eliminar el producto?")
            builder?.setTitle("Eliminar Producto")
            // Create the AlertDialog
            builder.create()
        }
        alertDialog?.show()
    }
    /*
    Petición para eliminar un producto de acuerdo a su ID
     */
    private fun borrar() {
        val url = "https://JuanSebastian10.pythonanywhere.com/producto/$idProducto"
        val queue = Volley.newRequestQueue(this)
        val resultadoPost = object : StringRequest(
            Method.DELETE,url,
            Response.Listener { response ->
                Toast.makeText(this, "Producto Eliminado",
                    Toast.LENGTH_LONG).show()
                limpiar()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Error al eliminar el producto $error",
                Toast.LENGTH_LONG).show()
            }
        ){
        }
        queue.add(resultadoPost)
    }

    /*
    Crea un objeto intent para abrir la interfaz para listar los productos
     */
    private fun listar (){
        val intent = Intent(this,MainActivityListarProductos::class.java)
        startActivity(intent)
    }



    fun limpiar() {
        txtCodigo.text.clear()
        txtNombre.text.clear()
        txtPrecio.text.clear()
        cbCategoria.setSelection(0)
    }
}