package antigo.S4;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static io.restassured.RestAssured.*;

import java.util.Locale;

import org.junit.Test;

import com.github.javafaker.Faker;
import model.User;


public class Usuarios_S4 extends Base_S4{
	private static Faker faker = new Faker (Locale.ENGLISH);
	private static User user = new User();
	private static User alteracao = new User();
	
	@Test
	public void retornaListaUsuarios() { //GET
		given()
		.when()
			.get("/usuarios")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body(containsString("usuarios"))
			.body(containsString("quantidade"))
			.body("quantidade", is(greaterThan(0)))
			;
	}
	
	@Test
	public void retornarUsuarioPorId() { //GET ID
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword(faker.internet().password());
		user.setAdministrador(true);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.get("/usuarios/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body(containsString("nome"))
			.body(containsString("email"))
			.body(containsString("password"))
			.body(containsString("administrador"))
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
		;
	}

	@SuppressWarnings("deprecation")
	@Test
	public void retornarUsuarioPorIdDesserializado() {
		User created_user = new User();
		
		created_user =				
		given()
			.pathParam("id", "0uxuPY0cbmQhpEz1")
		.when()
			.get("/usuarios/{id}").as(User.class);
		
		assertThat(created_user.getNome(), equalTo("Default User"));
		assertThat(created_user.getEmail(), equalTo("defaultuser@qa.com"));
		assertThat(created_user.getPassword(), equalTo("defaultpass"));
		assertThat(created_user.getId(), equalTo("0uxuPY0cbmQhpEz1"));
	}


	@Test
	public void naoRetornarUsuarioPorId() {
		given()
			.pathParam("id", "0uxuPY452352345234")
		.when()
			.get("/usuarios/{id}")
		.then()
			.log().all()
			.time(lessThan(3000L))
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("message", is("Usuário não encontrado"))
			;
	}
	
	@Test
	public void cadastrarNovoUsuario() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
			;
		
	}
	
	@Test
	public void naoCadastrarUsuarioCamposEmBranco() { //POST
		user.setNome(" ");
		user.setEmail(" ");
		user.setPassword(" ");
		user.setAdministrador(false);
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("email", is("email deve ser um email válido"))
			;
	}
	
	@Test
	public void naoCadastrarUsuarioCamposVazios() { //POST
		user.setNome("");
		user.setEmail("");
		user.setPassword("");
		user.setAdministrador(false);
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("nome", is("nome não pode ficar em branco"))
			.body("email", is("email não pode ficar em branco"))
			.body("password", is("password não pode ficar em branco"))
			;
	}
	
	@Test
	public void naoCadastrarUsuarioJaExistente() { 
		user.setNome(faker.name().fullName());
		user.setEmail("defaultuser@qa.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Este email já está sendo usado"))
			;
	}
	
	@Test
	public void naoCadastrarUsuarioComGmailBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("marcos@gmail.com");
		user.setPassword("oi");
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
	}
	
	@Test
	public void naoCadastrarUsuarioComHotmailBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("marcos@hotmail.com");
		user.setPassword("ola01234");
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaMenorQueCincoBUG() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword("oi");
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaMaiorQueDezBUG() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword("woqadoaskdasior92345923954fjsdigjdfighdftgho");
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaComEspacoEmBrancoBUG() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword(" ");
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaVazia() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword("");
		user.setAdministrador(false);
		
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("password", is("password não pode ficar em branco"));
		
		
	}
	
	
	@Test
	public void cadastrarUsuarioSemArroba() { 
		user.setNome(faker.name().fullName());
		user.setEmail("marcosgmail.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("email", is("email deve ser um email válido"))
			;
	}
	
	@Test
	public void alterarInfoUsuario() { //PUT ID
		user.setNome(faker.name().fullName());
		user.setEmail("batata@gmail.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(true);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("batata@gmail.com");
		alteracao.setPassword(faker.internet().password());
		alteracao.setAdministrador(true);
		
		given()
			.body(alteracao)
			.pathParam("id", id)
		.when()
			.put("/usuarios/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro alterado com sucesso"))
			;
		given()
		.pathParam("id", id)
	.when()
		.delete("/usuarios/{id}")
	.then()
		.statusCode(200)
		.time(lessThan(3000L))
		.body(is(not(nullValue())))
		.body("message", is("Registro excluído com sucesso"));
		;
	}
	
	@Test
	public void alterarInfoUsuarioInexistenteLogoCriarNovoUsuario() { //PUT ID
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("batata@gmail.com");
		alteracao.setPassword(faker.internet().password());
		alteracao.setAdministrador(true);
		
		String id =
		given()
			.body(alteracao)
			.pathParam("id", "adfisdj23i5ji2345k")
		.when()
			.put("/usuarios/{id}")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		given()
	.pathParam("id", id)
	.when()
		.delete("/usuarios/{id}")
		.then()
		.statusCode(200)
		.time(lessThan(3000L))
		.body(is(not(nullValue())))
		.body("message", is("Registro excluído com sucesso"));
		;
	
	}
	
	@Test
	public void alterarSenhaValidaParaInvalidaBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("joao@qa.com");
		user.setPassword("qa12345");
		user.setAdministrador(true);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("joao@qa.com");
		alteracao.setPassword("♫");
		alteracao.setAdministrador(true);
		
		given()
			.body(alteracao)
			.pathParam("id", id)
		.when()
			.put("/usuarios/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro alterado com sucesso"))
			;
		given()
		.pathParam("id", id)
	.when()
		.delete("/usuarios/{id}")
	.then()
		.statusCode(200)
		.time(lessThan(3000L))
		.body(is(not(nullValue())))
		.body("message", is("Registro excluído com sucesso"));
		;
	}
	
	@Test
	public void alterarEmailValidoParaGmailBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("usergmail@qa.com");
		user.setPassword("qa12345");
		user.setAdministrador(true);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("marciodias@gmail.com");
		alteracao.setPassword("1");
		alteracao.setAdministrador(true);
		
		given()
			.body(alteracao)
			.pathParam("id", id)
		.when()
			.put("/usuarios/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro alterado com sucesso"))
			;
		given()
		.pathParam("id", id)
	.when()
		.delete("/usuarios/{id}")
	.then()
		.statusCode(200)
		.time(lessThan(3000L))
		.body(is(not(nullValue())))
		.body("message", is("Registro excluído com sucesso"));
		;
	}
	
	@Test
	public void alterarEmailValidoParaHotmailBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("marciodias@qa.com");
		user.setPassword("qa12345");
		user.setAdministrador(true);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("marciodias@hotmail.com");
		alteracao.setPassword("152364578679634534645667");
		alteracao.setAdministrador(true);
		
		given()
			.body(alteracao)
			.pathParam("id", id)
		.when()
			.put("/usuarios/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro alterado com sucesso"))
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
			;
	}
	
	
	@Test
	public void naoAlterarEmailParaUmEmailJaUtilizado() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("marciodias@qa.com");
		user.setPassword("qa12345");
		user.setAdministrador(true);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("defaultuser@qa.com");
		alteracao.setPassword("defaultpass");
		alteracao.setAdministrador(true);
		
		given()
			.body(alteracao)
			.pathParam("id", id)
		.when()
			.put("/usuarios/{id}")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Este email já está sendo usado"))
			;
	}
	@Test
	public void excluirUsuario( ) {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
			;
		given()
			.pathParam("id", id)
		.when()
			.delete("/usuarios/{id}")
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"));
			;
	}
	
	@Test
	public void excluirUsuarioNaoExistente() {
		given()
		.when()
			.delete("/usuarios/{id}", "564565645sdfd")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Nenhum registro excluído"))
			;
	}
	
	@Test
	public void naoExcluirUsuarioComProdutosNoCarrinho() {
		given()
		.when()
			.delete("/usuarios/{id}", "0uxuPY0cbmQhpEz1")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Não é permitido excluir usuário com carrinho cadastrado"))
			.body(containsString("idCarrinho"))
			.body(containsString("message"))
			;
	}
	@Test
	public void fluxoFelizCriacaoDeUsuario() {
		user.setNome("Julio Cesar");
		user.setEmail("juliocesar@qa.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		
		String id =
		given()
			.body(user)
		.when()
			.post("/usuarios")                       	// CRIAR USUÁRIO
		.then()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
		.extract()
			.path("_id")
		;
		given()
		.when()
			.get("/usuarios/{id}", id)    //BUSCAR USUARIO CRIADO
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body(containsString("nome"))
			.body(containsString("email"))
			.body(containsString("password"))
			.body(containsString("administrador"))
			;
		User alteracao = new User();
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail(faker.internet().emailAddress());
		alteracao.setPassword(faker.internet().password());
		alteracao.setAdministrador(false);
		given()
			.body(alteracao)
			.contentType("application/json")
		.when()
			.put("/usuarios/{id}", id)  // ALTERA O USUÁRIO
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body("message", is("Registro alterado com sucesso"))
			;
		given()
		.when()
			.get("/usuarios/{id}", id)    //BUSCAR USUARIO ALTERADO
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body(containsString("nome"))
			.body(containsString("email"))
			.body(containsString("password"))
			.body(containsString("administrador"))
			;
		given()
		.when()
			.delete("/usuarios/{id}", id)   //DELETAR USUÁRIO
		.then()
			.statusCode(200)
			.time(lessThan(3000L))
			.body("message", is("Registro excluído com sucesso"))
			;
		
		given()
		.when()
			.get("/usuarios/{id}", id) 	//VERIFICA SE O USUÁRIO FOI DELETADO
		.then()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Usuário não encontrado"))
			;
	}
}