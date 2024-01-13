package antigo.S3;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;
import org.junit.Test;
import io.restassured.http.ContentType;

public class Login_S3 {
	String url_login = "http://localhost:3000/login";
	
	@Test
	public void fazerLogin() {
	String usuario = "{\r\n"
			+ "  \"email\": \"guima@qa.com.br\",\r\n"
			+ "  \"password\": \"raptors1\"\r\n"
			+ "}";

		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_login)
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Login realizado com sucesso"))
			.body("authorization", containsString("Bearer"))
			.body("authorization", matchesRegex("^Bearer [A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$"))
            .extract()
            .response()
            .asString();
			;
	}
	
	@Test
	public void loginComSenhaIncorreta( ) {
	String usuario = "{\r\n"
			+ "  \"email\": \"guima@qa.com.br\",\r\n"
			+ "  \"password\": \"senhaerrada\"\r\n"
			+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_login)
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComEmailIncorreto( ) {
	String usuario = "{\r\n"
			+ "  \"email\": \"emailerrado@qa.com.br\",\r\n"
			+ "  \"password\": \"raptors1\"\r\n"
			+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_login)
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComSenhaMaiuscula( ) {
	String usuario = "{\r\n"
			+ "  \"email\": \"guima@qa.com.br\",\r\n"
			+ "  \"password\": \"RAPTORS1\"\r\n"
			+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_login)
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	@Test
	public void loginComEmailMaiusculo( ) {
	String usuario = "{\r\n"
			+ "  \"email\": \"GUIMA@qa.com.br\",\r\n"
			+ "  \"password\": \"raptors1\"\r\n"
			+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_login)
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComDadosIncorretos( ) { //MELHORIA FICTÍCIA
	String usuario = "{\r\n"
			+ "  \"email\": \"errado@qa.com.br\",\r\n"
			+ "  \"password\": \"senhaerrada\"\r\n"
			+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_login)
		.then()
			.log().all()
			.statusCode(401)
			.body(is(not(nullValue())))
			.body("message", is("Email e/ou senha inválidos"))
			;
	}
	
	@Test
	public void loginComDadoEmBranco( ) {
	String usuario = "{\r\n"
			+ "  \"email\": \"\",\r\n"
			+ "  \"password\": \"\"\r\n"
			+ "}";
		given()
			.body(usuario)
			.contentType(ContentType.JSON)
		.when()
			.post(url_login)
		.then()
			.log().all()
			.statusCode(400)
			.body(is(not(nullValue())))
			.body("email", is("email não pode ficar em branco"))
			.body("password", is("password não pode ficar em branco"))
			;
	}
}

/*@Test
public void servidorForaDoAr() {  //MELHORIA FICTÍCIA - /status
	given()
	.when()
		.get("http://localhost:3000/status")
	.then()
		.log().all()
		.statusCode(503)
		.body("message", is("Servidor em manutenção. Aguarde alguns instantes."))
		;
}*/
