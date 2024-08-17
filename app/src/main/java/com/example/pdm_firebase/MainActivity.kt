package com.example.pdm_firebase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.pdm_firebase.modelDAO.Usuario
import com.example.pdm_firebase.modelDAO.UsuarioDAO
import com.example.pdm_firebase.ui.theme.PDMFirebaseTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val usuarioDAO = UsuarioDAO()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            PDMFirebaseTheme {
                var isCadastroScreen by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text(if (isCadastroScreen) "Cadastro" else "PDM Firebase") })
                    }
                ) { innerPadding ->
                    if (isCadastroScreen) {
                        CadastroContent(
                            modifier = Modifier.padding(innerPadding),
                            onCadastrar = { nome, senha ->
                                cadastrarUsuario(nome, senha) {
                                    isCadastroScreen = false
                                }
                            },
                            onCancelar = { isCadastroScreen = false }
                        )
                    } else {
                        MainContent(
                            modifier = Modifier.padding(innerPadding),
                            onLogin = { nome, senha ->
                                login(nome, senha)
                            },
                            onCarregarUsuarios = {
                                carregarUsuarios()
                            },
                            onNavigateToCadastro = { isCadastroScreen = true }
                        )
                    }
                }
            }
        }
    }

    private fun login(nome: String, senha: String) {
        lifecycleScope.launch {
            usuarioDAO.login(nome, senha) { success ->
                if (success) {
                    Toast.makeText(this@MainActivity, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Nome ou senha incorretos!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cadastrarUsuario(nome: String, senha: String, onSuccess: () -> Unit) {
        lifecycleScope.launch {
            usuarioDAO.cadastrarUsuario(nome, senha) { success ->
                if (success) {
                    Toast.makeText(this@MainActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(this@MainActivity, "Falha ao cadastrar usuário!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun carregarUsuarios() {
        lifecycleScope.launch {
            usuarioDAO.carregarUsuarios { usuarios ->
                setContent {
                    PDMFirebaseTheme {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                TopAppBar(title = { Text("Lista de Usuários") })
                            }
                        ) { innerPadding ->
                            UserListScreen(usuarios = usuarios, modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    onLogin: (String, String) -> Unit,
    onCarregarUsuarios: () -> Unit,
    onNavigateToCadastro: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLogin(nome, senha) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCarregarUsuarios,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Carregar Usuários")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToCadastro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar Novo Usuário")
        }
    }
}

@Composable
fun CadastroContent(
    modifier: Modifier = Modifier,
    onCadastrar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onCadastrar(nome, senha) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
fun UserListScreen(usuarios: List<Usuario>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(usuarios) { usuario ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = "User Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = usuario.nome)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    PDMFirebaseTheme {
        MainContent(
            onLogin = { _, _ -> },
            onCarregarUsuarios = {},
            onNavigateToCadastro = {}
        )
    }
}
