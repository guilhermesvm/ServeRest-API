package aulas;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static constants.Endpoints.LOGIN;

import org.junit.Before;
import org.junit.Test;

import helper.EnvConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;
import model.Logar;
import services.BaseTest;

//MultiAmbiente
//Código para teste: mvn test -Denv=local -Dtest="aulas/AulaTest.java"
public class AulaTest extends BaseTest {
	private static Logar logar = new Logar();
	
	private static RequestSpecification reqSpec;
	
	@Before
	public void setupp() {
			reqSpec = new RequestSpecBuilder()
					.setBaseUri(EnvConfig.getProperty("url", ""))
					.setAccept(ContentType.JSON)
					.build();
	}
	
	@Test
	public void fazerLogin() {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("defaultpass");
		
		String Token =
			given()
				.spec(reqSpec)
				.body(logar)
			.when()
				.post(LOGIN)
			.then()
				.body("message", equalTo("Login realizado com sucesso"))
				.statusCode(200)
				.body("message", is("Login realizado com sucesso"))
				.body("authorization", containsString("Bearer"))
				.body("authorization", matchesRegex("^Bearer [A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$"))
				.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/200.json"))
			.extract()
				.path("authorization")
				;
			logar.setAuthorization(Token);
	}
}