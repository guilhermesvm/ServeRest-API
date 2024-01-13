package test;

import static constants.Endpoints.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.github.javafaker.Faker;
import datafactory.DynamicFactory;
import io.restassured.response.Response;
import model.Logar;
import model.Product;
import model.User;
import services.BaseTest;


public class FluxoDeCompraTest extends BaseTest{
	private static User user = new User();
	private static User alteracao = new User();
	private static Logar logar = new Logar();
	private static Faker faker = new Faker (Locale.ENGLISH);
	private static Product produto = new Product();
	
	
	@Before
	public void criaUser() {
		//Cria usuário
		user.setNome(faker.name().fullName());
		user.setEmail("fulano@qa.com");
		user.setPassword("fulano123");
		user.setAdministrador(true);
		Response criacao = rest.post(USUARIOS, user);
		String id  = criacao.path("_id");
		user.setId(id);
	}
	
	@After
	public void ExcluirDados( ) {
		rest.deleteToken(PRODUTOS, produto.getId(), logar.getAuthorization());
		rest.delete(USUARIOS, user.getId());
	}
	
	@Test
	public void FluxoDeVendedor() {
		//Loga
		logar.setEmail("fulano@qa.com");
		logar.setPassword("fulano123");
		Response response = rest.post(LOGIN, logar);
		String token = response.path("authorization");
		logar.setAuthorization(token);
		
		//Altera seu nome no site
		alteracao.setNome("Senhorio Fulano");
		alteracao.setEmail("fulano@qa.com");
		alteracao.setPassword("fulano123");
		alteracao.setAdministrador(true);
		rest.put(USUARIOS, alteracao, user.getId());
		
		//Cria seu produto para vender
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, token);
		String idProd = product.path("_id");
		produto.setId(idProd);
	}
	
	@Test
	public void FluxoDeComprasFinalizado() {
		//Loga
		logar.setEmail("fulano@qa.com");
		logar.setPassword("fulano123");
		Response login = rest.post(LOGIN, logar);
		String token = login.path("authorization");
		logar.setAuthorization(token);
		
		//Altera seu nome no site
		alteracao.setNome("Senhorio Fulano");
		alteracao.setEmail("fulano@qa.com");
		alteracao.setPassword("fulano123");
		alteracao.setAdministrador(true);
		rest.put(USUARIOS, alteracao, user.getId());
		
		//Produto previamente criado
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String idProd = product.path("_id");
		produto.setId(idProd);
		
		//Cria carrinho com produto
		String carrinho = DynamicFactory.generateCart(idProd, 80);
		rest.postToken(CARRINHOS, carrinho, token);
		
		//Finaliza a compra, delete produto e deleta usuário
		rest.concluirCompraToken(token);

	}
	
	@Test
	public void FluxoDeComprasCancelado() {
		//Loga
		logar.setEmail("fulano@qa.com");
		logar.setPassword("fulano123");
		Response login = rest.post(LOGIN, logar);
		String token = login.path("authorization");
		logar.setAuthorization(token);
		
		//Altera seu nome no site
		alteracao.setNome("Senhorio Fulano");
		alteracao.setEmail("fulano@qa.com");
		alteracao.setPassword("fulano123");
		alteracao.setAdministrador(true);
		rest.put(USUARIOS, alteracao, user.getId());
		
		//Produto previamente criado
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		assertThat(product.statusCode(), is(201));
		assertThat(product.asString(), containsString("message"));
		assertThat(product.path("message"), is(not(nullValue())));
		assertThat(product.path("message").toString(), is("Cadastro realizado com sucesso"));
		String idProd = product.path("_id");
		produto.setId(idProd);
		
		//Cria carrinho com produto
		String carrinho = DynamicFactory.generateCart(idProd, 80);
		
		rest.postToken(CARRINHOS, carrinho, token);
		
		//Cancela a compra, deleta produto e deleta usuário
		rest.cancelarCompraToken(token);
	}

}
