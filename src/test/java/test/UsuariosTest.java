package test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static constants.Endpoints.USUARIOS;

import java.util.Locale;
import org.junit.Test;
import com.github.javafaker.Faker;
import datafactory.DynamicFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.Tag;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import model.User;
import services.BaseTest;

@Tag("usuarios")
@Epic("Testes do Edpoint /usuarios")
public class UsuariosTest extends BaseTest{
	private static Faker faker = new Faker (Locale.ENGLISH);
	private static User user = new User();
	private static User alteracao = new User();
	
	@Test
	@Description("Deve retornar lista de usuários")
	public void retornaListaUsuarios() { 
		Response response = rest.get(USUARIOS);
		assertThat(response.statusCode(), is(200));
	    assertThat(response.asString(), containsString("quantidade"));
	    assertThat(response.path("quantidade"), is(not(nullValue())));
	    assertThat(response.asString(), containsString("usuarios"));
	    assertThat(response.path("usuarios"), is(not(nullValue())));
	    assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/get/200.json"));
	}
	
	@Test
	public void retornarUsuarioPorId() {
		user = DynamicFactory.generateRandomUser(true);
		Response criar = rest.post(USUARIOS, user);
		String id = criar.path("_id");
		user.setId(id);
		
		Response response = rest.getId(USUARIOS, id);
		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("nome"));
		assertThat(response.asString(), containsString("email"));
		assertThat(response.asString(), containsString("password"));
		assertThat(response.asString(), containsString("administrador"));
		assertThat(response.asString(), containsString("_id"));
		assertThat(response.path("nome"), is(not(nullValue())));
		assertThat(response.path("email"), is(not(nullValue())));
		assertThat(response.path("password"), is(not(nullValue())));
		assertThat(response.path("administrador"), is(not(nullValue())));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/get_{id}/200.json"));
		
		rest.delete(USUARIOS, id);
	}
	
	
	@Test
	public void naoRetornarUsuarioPorId() {
		Response response = rest.getId(USUARIOS, "sdafasfsdf");
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message"), is("Usuário não encontrado"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/get_{id}/400.json"));	
	}
	
	@Test
	public void cadastrarNovoUsuario() {
		user = DynamicFactory.generateRandomUser(true);
		Response response = rest.post(USUARIOS, user);
		assertThat(response.statusCode(), is(201));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message").toString(), is("Cadastro realizado com sucesso"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/post/201.json"));
		
		String id = response.path("_id");
		user.setId(id);
		
		rest.delete(USUARIOS, id);
	}
	
	@Test
	public void naoCadastrarUsuarioCamposEmBranco() { //POST
		user.setNome(" ");
		user.setEmail(" ");
		user.setPassword(" ");
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		assertThat(response.statusCode(), is(400));
		assertThat(response.path("email").toString(), is("email deve ser um email válido"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/post/400_2.json"));
	}
	
	@Test
	public void naoCadastrarUsuarioCamposVazios() { //POST
		user.setNome("");
		user.setEmail("");
		user.setPassword("");
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		assertThat(response.statusCode(), is(400));
		assertThat(response.path("nome").toString(), is("nome não pode ficar em branco"));
		assertThat(response.path("email").toString(), is("email não pode ficar em branco"));
		assertThat(response.path("password").toString(), is("password não pode ficar em branco"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/post/400_3.json"));
		
		String id = response.path("_id");
		user.setId(id);
		
	}
	
	@Test
	public void naoCadastrarUsuarioJaExistente() { 
		user.setNome(faker.name().fullName());
		user.setEmail("defaultuser@qa.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		assertThat(response.statusCode(), is(400));
		assertThat(response.path("message").toString(), is("Este email já está sendo usado"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/post/400.json"));
		
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaVazia() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword("");
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		assertThat(response.statusCode(), is(400));
		assertThat(response.path("password").toString(), is("password não pode ficar em branco"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/post/400_4.json"));
		
		String id = response.path("_id");
		user.setId(id);
	
	}
	
	
	@Test
	public void cadastrarUsuarioSemArroba() { 
		user.setNome(faker.name().fullName());
		user.setEmail("marcosgmail.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		assertThat(response.statusCode(), is(400));
		assertThat(response.path("email").toString(), is("email deve ser um email válido"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/post/400_2.json"));
	}
	
	@Test
	public void naoCadastrarUsuarioComGmailBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("marcos@gmail.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		String id  = response.path("_id");
		user.setId(id);
		
		rest.delete(USUARIOS, id);

	}
	
	@Test
	public void naoCadastrarUsuarioComHotmailBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("marcos@hotmail.com");
		user.setPassword(faker.internet().password());
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		String id  = response.path("_id");
		user.setId(id);
		
		rest.delete(USUARIOS, id);;
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaMenorQueCincoBUG() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword("123");
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		String id  = response.path("_id");
		user.setId(id);
		
		rest.delete(USUARIOS, id);
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaMaiorQueDezBUG() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword("123412345235345634645756784566346346745756346456");
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		String id  = response.path("_id");
		user.setId(id);
		
		rest.delete(USUARIOS, id);
	}
	
	@Test
	public void naoCadastrarUsuarioComSenhaComEspacoEmBrancoBUG() {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword(" ");
		user.setAdministrador(false);
		
		Response response = rest.post(USUARIOS, user);
		String id  = response.path("_id");
		user.setId(id);
		
		rest.delete(USUARIOS, id);
	}
	@Test
	public void alterarInfoUsuario() { //PUT ID
		user = DynamicFactory.generateRandomUser(false);
		Response response = rest.post(USUARIOS, user);
		String id = response.path("_id");
		user.setId(id);
		
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("batata@gmail.com");
		alteracao.setPassword(faker.internet().password());
		alteracao.setAdministrador(true);
		
		Response alterar = rest.put(USUARIOS, alteracao, id);
		assertThat(alterar.statusCode(), is(200));
		assertThat(alterar.asString(), containsString("message"));
		assertThat(alterar.path("message"), is(not(nullValue())));
		assertThat(alterar.path("message"), is("Registro alterado com sucesso"));
		assertThat(alterar.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/put/200.json"));
		
		rest.delete(USUARIOS, id);
	}
	
	@Test
	public void alterarInfoUsuarioInexistenteLogoCriarNovoUsuario() { //PUT ID
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("batata@gmail.com");
		alteracao.setPassword(faker.internet().password());
		alteracao.setAdministrador(true);
		Response alterar = rest.put(USUARIOS, alteracao, "342342asfasd");
		assertThat(alterar.statusCode(), is(201));
		assertThat(alterar.asString(), containsString("message"));
		assertThat(alterar.path("message"), is(not(nullValue())));
		assertThat(alterar.path("message"), is("Cadastro realizado com sucesso"));
		assertThat(alterar.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/put/201.json"));
		
		String id = alterar.path("_id");
		user.setId(id);
		
		rest.delete(USUARIOS, id);
	}
	
	@Test
	public void alterarSenhaValidaParaInvalidaBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("joao@qa.com");
		user.setPassword("abc12345");
		user.setAdministrador(false);
		Response response = rest.post(USUARIOS, user);
		String id = response.path("_id");
		user.setId(id);
		
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("joao@qa.com");
		alteracao.setPassword("♫");
		alteracao.setAdministrador(true);
		rest.put(USUARIOS, alteracao, id);
		rest.delete(USUARIOS, id);
	}
	
	@Test
	public void alterarEmailValidoParaGmailBUG() { //SEM SCRIPT
		user.setNome(faker.name().fullName());
		user.setEmail("usergmail@qa.com");
		user.setPassword("qa12345");
		user.setAdministrador(true);
		Response response = rest.post(USUARIOS, user);
		String id = response.path("_id");
		user.setId(id);
		
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("usergmail@gmail.com");
		alteracao.setPassword("1");
		alteracao.setAdministrador(true);
		rest.put(USUARIOS, alteracao, id);
		
		rest.delete(USUARIOS, id);
	}
	
	@Test
	public void alterarEmailValidoParaHotmailBUG() {
		user.setNome(faker.name().fullName());
		user.setEmail("userhotmail@qa.com");
		user.setPassword("qa12345");
		user.setAdministrador(true);
		
		Response response = rest.post(USUARIOS, user);
		String id = response.path("_id");
		user.setId(id);
		
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("userhotmail@hotmal.com");
		alteracao.setPassword("15234523");
		alteracao.setAdministrador(true);
		rest.put(USUARIOS, alteracao, id);
		
		
		rest.delete(USUARIOS, id);
	}
	
	
	@Test
	public void naoAlterarEmailParaUmEmailJaUtilizado() { 
		user.setNome(faker.name().fullName());
		user.setEmail("mesmoemail@qa.com");
		user.setPassword("qa12345");
		user.setAdministrador(true);
		
		Response response = rest.post(USUARIOS, user);
		String id = response.path("_id");
		user.setId(id);
		
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("defaultuser@qa.com");
		alteracao.setPassword(faker.internet().password());
		alteracao.setAdministrador(true);
		
		Response alterar = rest.put(USUARIOS, alteracao, id);
		assertThat(alterar.statusCode(), is(400));
		assertThat(alterar.asString(), containsString("message"));
		assertThat(alterar.path("message"), is(notNullValue()));
		assertThat(alterar.path("message"), is("Este email já está sendo usado"));
		assertThat(alterar.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/put/400.json"));
	}
	@Test
	public void excluirUsuario( ) {		
		user = DynamicFactory.generateRandomUser(true);
		
		Response response = rest.post(USUARIOS, user);
	
		String id = response.path("_id");
		user.setId(id);
		
		Response excluir = rest.delete(USUARIOS, id);
		assertThat(excluir.statusCode(), is(200));
		assertThat(excluir.asString(), containsString("message"));
		assertThat(excluir.path("message"), is(not(nullValue())));
		assertThat(excluir.path("message").toString(), is("Registro excluído com sucesso"));
		assertThat(excluir.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/delete/200.json"));
	}
	
	@Test
	public void excluirUsuarioNaoExistente() {
		Response excluir = rest.delete(USUARIOS, "qwraraer34");
		assertThat(excluir.statusCode(), is(200));
		assertThat(excluir.path("message"), is(not(nullValue())));
		assertThat(excluir.path("message").toString(), is("Nenhum registro excluído"));
		assertThat(excluir.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/delete/200.json"));

	}
	
	/*@Test
	public void naoExcluirUsuarioComProdutosNoCarrinho() {
		Response excluir = rest.delete(USUARIOS, "l2JobvXVr0CHpFab");
		assertThat(excluir.statusCode(), is(400));
		assertThat(excluir.asString(), containsString("message"));
		assertThat(excluir.asString(), containsString("idCarrinho"));
		assertThat(excluir.path("message"), is(not(nullValue())));
		assertThat(excluir.path("idCarrinho"), is(not(nullValue())));
		assertThat(excluir.path("message").toString(), is("Não é permitido excluir usuário com carrinho cadastrado"));
		assertThat(excluir.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/usuarios/delete/400.json"));
	}*/
	@Test
	public void fluxoFelizCriacaoDeUsuario() {
		user = DynamicFactory.generateRandomUser(true);
		Response response = rest.post(USUARIOS, user);
		String id =response.path("_id");
		user.setId(id);
		
		rest.getId(USUARIOS, id);
		
		alteracao.setNome(faker.name().fullName());
		alteracao.setEmail("batata@gmail.com");
		alteracao.setPassword(faker.internet().password());
		alteracao.setAdministrador(true);
		Response alterar = rest.put(USUARIOS, alteracao, id);
		assertThat(alterar.statusCode(), is(200));
		assertThat(alterar.asString(), containsString("message"));
		assertThat(alterar.path("message"), is(not(nullValue())));
		assertThat(alterar.path("message"), is("Registro alterado com sucesso"));
		
		rest.getId(USUARIOS, id);
		
		rest.delete(USUARIOS, id);
		;
	}
}