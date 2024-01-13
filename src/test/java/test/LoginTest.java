package test;


import static constants.Endpoints.LOGIN;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.Tag;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import model.Logar;
import services.BaseTest;
import services.LoginService;

@Tag("login")
@Epic("Testes do Edpoint /login")
public class LoginTest extends BaseTest {
	private static Logar logar = new Logar();
	
	@Test
	@Description("Deve fazer login")
	public void fazerLogin() {
		logar = LoginService.loginAccount();
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(notNullValue()));
		assertThat(response.path("message").toString(), is("Login realizado com sucesso"));
		assertThat(response.asString(), containsString("authorization"));
		assertThat(response.path("authorization").toString(), matchesRegex("^Bearer [A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$"));
	    assertThat(response.path("authorization"), is(instanceOf(String.class)));
	    assertThat(response.path("message"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/200.json"));
	}
	
	@Test
	@Description("Não deve fazer login")
	public void naoFazerLogin() {
		logar.setEmail("fasfasd@qa.com");
		logar.setPassword("gasdgsdgsdfg");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(notNullValue()));
		assertThat(response.path("message").toString(), is("Email e/ou senha inválidos"));
		assertThat(response.path("message"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/401.json"));
	}
	
	@Test
	@Description("Não deve fazer login com senha incorreta") //********
	public void loginComSenhaIncorreta( ) {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("12345");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(notNullValue()));
		assertThat(response.path("message").toString(), is("Email e/ou senha inválidos"));
		assertThat(response.path("message"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/401.json"));
	}
	
	@Test
	@Description("Não deve fazer login com email incorreta")
	public void loginComEmailIncorreto( ) {
		logar.setEmail("harry123@qa.com");
		logar.setPassword("defaultpass");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(notNullValue()));
		assertThat(response.path("message").toString(), is("Email e/ou senha inválidos"));
		assertThat(response.path("message"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/401.json"));
	}
	
	@Test
	@Description("Não deve fazer login com senha maiuscula")
	public void loginComSenhaMaiuscula( ) {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("DEFAULTPASS");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(notNullValue()));
		assertThat(response.path("message").toString(), is("Email e/ou senha inválidos"));
		assertThat(response.path("message"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/401.json"));
	}
	@Test
	@Description("Não deve fazer login com email maiusculo")
	public void loginComEmailMaiusculo( ) {
		logar.setEmail("DEFAULTUSER@qa.com");
		logar.setPassword("defaultpass");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(notNullValue()));
		assertThat(response.path("message").toString(), is("Email e/ou senha inválidos"));
		assertThat(response.path("message"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/401.json"));
	}
	
	@Test
	@Description("Não deve fazer login com dados incorretos")
	public void loginComDadosIncorretos( ) {
		logar.setEmail("Robertao@qa.com");
		logar.setPassword("formula1");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(notNullValue()));
		assertThat(response.path("message").toString(), is("Email e/ou senha inválidos"));
		assertThat(response.path("message"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/401.json"));
	}
	
	@Test
	@Description("Não deve fazer login com email e senha vazios")
	public void loginComEmaileSenhaVazios( ) {
		logar.setEmail("");
		logar.setPassword("");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("email"));
		assertThat(response.asString(), containsString("password"));
		assertThat(response.path("email"), is(not(nullValue())));
		assertThat(response.path("password"), is(not(nullValue())));
		assertThat(response.path("email").toString(), is("email não pode ficar em branco"));
		assertThat(response.path("email"), is(instanceOf(String.class)));
		assertThat(response.path("password"), is(instanceOf(String.class)));
		assertThat(response.path("password").toString(), is("password não pode ficar em branco"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/400.json"));
	}
	
	@Test
	@Description("Não deve fazer login com email e senha em branco")
	public void loginComEmaileSenhaEmBranco( ) {
		logar.setEmail(" ");
		logar.setPassword(" ");
		
		Response response = rest.post(LOGIN, logar);
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("email"));
		assertThat(response.path("email"), is(not(nullValue())));
		assertThat(response.path("email").toString(), is("email deve ser um email válido"));
		assertThat(response.path("email"), is(instanceOf(String.class)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login/post/401_2.json"));
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
