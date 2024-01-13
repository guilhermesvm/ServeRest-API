package antigo.S4;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;


import org.junit.Test;
import model.Logar;



public class Login_S4 extends Base_S4{
	private static Logar logar = new Logar();
	
	@Test
	public void fazerLogin() {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("defaultpass");
		
		String Token =
			given()
				.body(logar)
			.when()
				.post("/login")
			.then()
				.body("message", equalTo("Login realizado com sucesso"))
				.log().all()
				.statusCode(200)
				.body("message", is("Login realizado com sucesso"))
				.body("authorization", containsString("Bearer"))
				.body("authorization", matchesRegex("^Bearer [A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$"))
			.extract()
				.path("authorization")
				;
			logar.setAuthorization(Token);
	}
	
	@Test
	public void naoFazerLogin() {
		logar.setEmail("fasfasd@qa.com");
		logar.setPassword("gasdgsdgsdfg");
		
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComSenhaIncorreta( ) {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("12345");
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComEmailIncorreto( ) {
		logar.setEmail("harry123@qa.com");
		logar.setPassword("defaultpass");
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComSenhaMaiuscula( ) {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("DEFAULTPASS");
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	@Test
	public void loginComEmailMaiusculo( ) {
		logar.setEmail("DEFAULTUSER@qa.com");
		logar.setPassword("defaultpass");
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComDadosIncorretos( ) { //MELHORIA FICTÍCIA
		logar.setEmail("Robertao@qa.com");
		logar.setPassword("formula1");
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComEmaileSenhaVazios( ) {
		logar.setEmail("");
		logar.setPassword("");
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("email", is("email não pode ficar em branco"))
			.body("password", is("password não pode ficar em branco"))
			;
	}
	
	@Test
	public void loginComEmaileSenhaEmBranco( ) {
		logar.setEmail(" ");
		logar.setPassword(" ");
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("email", is("email deve ser um email válido"))
			//.body("password", is("password não pode ficar em branco"))
			;
	}
	
}

/*@Test
public void servidorForaDoAr() {  //MELHORIA FICTÍCIA - /status
	given()
	.when()
		.get(/status")
	.then()
		.log().all()
		.statusCode(503)
		.body("message", is("Servidor em manutenção. Aguarde alguns instantes."))
		;
}*/
