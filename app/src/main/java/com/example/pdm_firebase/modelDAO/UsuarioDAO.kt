package com.example.pdm_firebase.modelDAO

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioDAO {

    private val db = FirebaseFirestore.getInstance()

    fun login(nome: String, senha: String, onResult: (Boolean) -> Unit) {
        try {
            db.collection("usuarios")
                .whereEqualTo("nome", nome)
                .whereEqualTo("senha", senha)
                .get()
                .addOnSuccessListener { documents ->
                    onResult(!documents.isEmpty)
                }
                .addOnFailureListener { exception ->
                    Log.e("UsuarioDAO", "Erro ao fazer login: ", exception)
                    onResult(false)
                }
        } catch (e: Exception) {
            Log.e("UsuarioDAO", "Exceção ao fazer login: ", e)
            onResult(false)
        }
    }

    fun buscarUsuario(nome: String, senha: String, callback: (Usuario?) -> Unit) {
        try {
            db.collection("usuarios")
                .whereEqualTo("nome", nome)
                .whereEqualTo("senha", senha)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        callback(null)
                    } else {
                        val document = documents.first()
                        val usuario = Usuario(
                            document.getString("nome")!!,
                            document.getString("senha")!!
                        )
                        callback(usuario)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("UsuarioDAO", "Erro ao buscar usuário: ", exception)
                    callback(null)
                }
        } catch (e: Exception) {
            Log.e("UsuarioDAO", "Exceção ao buscar usuário: ", e)
            callback(null)
        }
    }

    fun cadastrarUsuario(nome: String, senha: String, callback: (Boolean) -> Unit) {
        try {
            val usuario = hashMapOf(
                "nome" to nome,
                "senha" to senha
            )

            db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("UsuarioDAO", "Erro ao cadastrar usuário: ", exception)
                    callback(false)
                }
        } catch (e: Exception) {
            Log.e("UsuarioDAO", "Exceção ao cadastrar usuário: ", e)
            callback(false)
        }
    }

    fun carregarUsuarios(onResult: (List<Usuario>) -> Unit) {
        try {
            db.collection("usuarios")
                .get()
                .addOnSuccessListener { result ->
                    val usuarios = result.map { document ->
                        Usuario(
                            nome = document.getString("nome") ?: "",
                            senha = document.getString("senha") ?: ""
                        )
                    }
                    onResult(usuarios)
                }
                .addOnFailureListener { exception ->
                    Log.e("UsuarioDAO", "Erro ao carregar usuários: ", exception)
                    onResult(emptyList())
                }
        } catch (e: Exception) {
            Log.e("UsuarioDAO", "Exceção ao carregar usuários: ", e)
            onResult(emptyList())
        }
    }
}
