package com.generation.blogpessoal;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsuarioControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static String token;

	@Test
	@Order(1)
	void deveCadastrarUsuarioComSucesso() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setNome("Administrador");
		usuario.setUsuario("admin@email.com.br");
		usuario.setSenha("admin123");
		usuario.setFoto("https://i.imgur.com/Tk9f10K.png");

		mockMvc.perform(post("/usuarios/cadastrar").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuario))).andExpect(status().isCreated());
	}

	@Test
	@Order(2)
	void deveAutenticarUsuarioERetornarToken() throws Exception {
		UsuarioLogin loginRequest = new UsuarioLogin();
		loginRequest.setUsuario("admin@email.com.br");
		loginRequest.setSenha("admin123");

		MvcResult result = mockMvc
				.perform(post("/usuarios/logar").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		UsuarioLogin resposta = objectMapper.readValue(response, UsuarioLogin.class);
		token = resposta.getToken().replace("Bearer ", "");
		Assertions.assertNotNull(token);
		System.out.println(token);
	}

	@Test
	@Order(3)
	void deveListarUsuariosComTokenValido() throws Exception {
		System.out.println(token);
		mockMvc.perform(get("/usuarios/all").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	@Order(4)
	void deveAtualizarUsuarioComToken() throws Exception {
		Usuario usuarioAtualizado = new Usuario();
		usuarioAtualizado.setId(1L);
		usuarioAtualizado.setNome("Usuário Atualizado");
		usuarioAtualizado.setUsuario("teste@email.com");
		usuarioAtualizado.setSenha("novaSenha123");

		mockMvc.perform(put("/usuarios/atualizar").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioAtualizado)))
				.andExpect(status().isOk());
	}
}
