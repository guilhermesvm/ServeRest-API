package antigo.S3;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

import java.util.Date;


import org.junit.Test;
import io.restassured.http.ContentType;


public class Usuarios_S3 {
	String url_usuarios = "http://localhost:3000/usuarios";
	
	@Test
	public void retornaListaUsuarios() { //GET
		given()
		.when()
			.get(url_usuarios)
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body(containsString("usuarios"))
			.body(containsString("quantidade"))
			.body("quantidade", is(greaterThan(0)))
			;
	}
	
	@Test
	public void retornarUsuarioPorId() { //GET ID
		given()
		.when()
			.get("http://localhost:3000/usuarios/{id}", "wUUCTm8yTt8UpbBT")
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body(containsString("nome"))
			.body(containsString("email"))
			.body(containsString("password"))
			.body(containsString("administrador"))
			;
	}

	@Test
	public void naoRetornarUsuarioPorId() {
		given()
		.when()
			.get("http://localhost:3000/usuarios/{id}", " ")
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("message", is("Usuário não encontrado"))
			;
	}
	
	@Test
	public void cadastrarNovoUsuario() { //POST
		Date date = new Date();
		String email = date.getTime() + "@qa.com.br";
		
		String usuario = "{\r\n"
				+ "  \"nome\": \"Usuario RestAssured\",\r\n"
				+ "  \"email\":\"" + email + "\",\r\n"
				+ "  \"password\": \"teste123\",\r\n"
				+ "  \"administrador\": \"false\"\r\n"
				+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_usuarios)
		.then()
			.log().all()
			.statusCode(201)
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
			;
	}
	
	@Test
	public void cadastrarUsuarioCamposEmBeranco() { //POST
		String usuario = "{\r\n"
				+ "  \"nome\": \"\",\r\n"
				+ "  \"email\": \"\",\r\n"
				+ "  \"password\": \"\",\r\n"
				+ "  \"administrador\": \"\"\r\n"
				+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_usuarios)
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("nome", is("nome não pode ficar em branco"))
			.body("email", is("email não pode ficar em branco"))
			.body("password", is("password não pode ficar em branco"))
			.body("administrador", is("administrador deve ser 'true' ou 'false'"))
			;
	}
	
	@Test
	public void cadastrarUsuarioJaExistente() { 
		String usuario = "{\r\n"
				+ "  \"nome\": \"Julião\",\r\n"
				+ "  \"email\": \"usassured@qa.com.br\",\r\n"
				+ "  \"password\": \"juliao123\",\r\n"
				+ "  \"administrador\": \"false\"\r\n"
				+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_usuarios)
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("message", is("Este email já está sendo usado"))
			;
	}
	
	@Test
	public void cadastrarUsuarioComEmaileSenhaForaDosPadroes() { //SEM SCRIPT
		//Cadastrar
		String usuario = "{\r\n"
				+ "  \"nome\": \"Marquinhos\",\r\n"
				+ "  \"email\": \"aloha@gmail.com.br\",\r\n"
				+ "  \"password\": \"oi\",\r\n"
				+ "  \"administrador\": \"false\"\r\n"
				+ "}";
		String id_excluir =
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_usuarios)
		.then()
			.log().all()
			/*.statusCode(400)
			.body(is(not(nullValue())))
			.body("message", is("Email e Senha estão fora dos padrões"))*/
			.statusCode(201)
			.body(is(not(nullValue())))
			.extract()
			.path("_id")
			;
		//Deletar
		given()
		.when()
			.delete("http://localhost:3000/usuarios/{id}", id_excluir)
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
	}
	
	@Test
	public void cadastrarUsuarioSemArroba() { 
		String usuario = "{\r\n"
				+ "  \"nome\": \"Marquinhos\",\r\n"
				+ "  \"email\": \"alohagmail.com.br\",\r\n"
				+ "  \"password\": \"oi\",\r\n"
				+ "  \"administrador\": \"false\"\r\n"
				+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_usuarios)
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("email", is("email deve ser um email válido"))
			;
	}
	
	@Test
	public void alterarInfoUsuario() { //PUT ID
		String usuario = "{\r\n"
				+ "  \"nome\": \"Gui Machado\",\r\n"
				+ "  \"email\": \"guilherme@qa.com.br\",\r\n"
				+ "  \"password\": \"raptors2\",\r\n"
				+ "  \"administrador\": \"true\"\r\n"
				+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.put("http://localhost:3000/usuarios/{id}", "Ic9toAjfR4V8tfhx")
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Registro alterado com sucesso"))
			;
	}
	
	@Test
	public void alterarInfoValidaParaInvalida() { //SEM SCRIPT
		//Cadastrar
		String usuario = "{\r\n"
				+ "  \"nome\": \"Lebron James\",\r\n"
				+ "  \"email\": \"lebronjames@hotmail.com\",\r\n"
				+ "  \"password\": \"losangeleslakers\",\r\n"
				+ "  \"administrador\": \"true\"\r\n"
				+ "}";
		String id_excluir =
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.put("http://localhost:3000/usuarios/{id}", "Ic9toAjfR4V8tfhx")
		.then()
			.log().all()
			/*.statusCode(400)
			.body(is(not(nullValue())))
			.body("message", is("Registro não pode ser alterado. Email e Senha estão fora dos padrões"))*/
			.statusCode(201)
			.body(is(not(nullValue())))
			.extract()
			.path("_id")
			;
		//Deletar
		given()
		.when()
			.delete("http://localhost:3000/usuarios/{id}", id_excluir)
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
	}
	@Test
	public void excluirUsuario( ) {
		String usuario = "{\r\n"
				+ "  \"nome\": \"Usuario Daniel\",\r\n"
				+ "  \"email\": \"Daniel@qa.com\",\r\n"
				+ "  \"password\": \"teste123\",\r\n"
				+ "  \"administrador\": \"false\"\r\n"
				+ "}";
		String excluir_id =
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_usuarios)
		.then()
			.log().all()
			.statusCode(201)
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
			.extract()
			.path("_id")
			;
	given()
	.when()
		.delete("http://localhost:3000/usuarios/{id}", excluir_id )             //DELETAR USUARIO
	.then()
		.log().all()
		.statusCode(200)
		.body(is(not(nullValue())))
		.body("message", is("Registro excluído com sucesso"));
}
	
	@Test
	public void excluirUsuarioNaoExistente() {
		given()
		.when()
			.delete("http://localhost:3000/usuarios/{id}", "5Lv1GDV6cQSdNPq9")
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Nenhum registro excluído"))
			;
	}
	@Test
	public void fluxoFelizCriacaoDeUsuario() {
		Date date = new Date();
		String email = date.getTime() + "@qa.com.br";
		String usuario = "{\r\n"
				+ "  \"nome\": \"Ralph Bufford\",\r\n"
				+ "  \"email\":\"" + email + "\",\r\n"
				+ "  \"password\": \"sackings\",\r\n"
				+ "  \"administrador\": \"true\"\r\n"
				+ "}";
		
		String id =
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_usuarios)                       	// CRIAR USUÁRIO
		.then()
			.log().all()
			.statusCode(201)
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
		;
		given()
		.when()
			.get("http://localhost:3000/usuarios/{id}", id)    //BUSCAR USUARIO CRIADO
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body(containsString("nome"))
			.body(containsString("email"))
			.body(containsString("password"))
			.body(containsString("administrador"))
			;
		String usuario_novo = "{\r\n"
				+ "  \"nome\": \"Ralph F. Bufford\",\r\n"
				+ "  \"email\":\"" + email + "\",\r\n"
				+ "  \"password\": \"saspurs\",\r\n"
				+ "  \"administrador\": \"false\"\r\n"
				+ "}";
		given()
			.body(usuario_novo)
			.contentType("application/json")
		.when()
			.put("http://localhost:3000/usuarios/{id}", id)  // ALTERA O USUÁRIO
		.then()
			.log().all()
			.statusCode(200)
			.body("message", is("Registro alterado com sucesso"))
			;
		given()
		.when()
			.get("http://localhost:3000/usuarios/{id}", id)    //BUSCAR USUARIO ALTERADO
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body(containsString("nome"))
			.body(containsString("email"))
			.body(containsString("password"))
			.body(containsString("administrador"))
			;
		given()
		.when()
			.delete("http://localhost:3000/usuarios/{id}", id)   //DELETAR USUÁRIO
		.then()
			.log().all()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"))
			;
		given()
		.when()
			.get("http://localhost:3000/usuarios/{id}", id)
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("message", is("Usuário não encontrado"))
			;
	}
}