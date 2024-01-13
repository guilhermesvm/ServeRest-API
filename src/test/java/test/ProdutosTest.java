package test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static constants.Endpoints.PRODUTOS;
import static constants.Endpoints.CARRINHOS;
import static constants.Endpoints.LOGIN;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import com.github.javafaker.Faker;
import datafactory.DynamicFactory;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import model.Logar;
import model.Product;
import services.BaseTest;
import services.LoginService;

public class ProdutosTest extends BaseTest {
	private static Faker faker = new Faker (Locale.ENGLISH);
	private static Product produto = new Product();
	private static Logar logar = new Logar();

	@Before
	public void fazerLogin() {
		logar = LoginService.loginAccount();
		
		Response response = rest.post(LOGIN, logar);
		String token = response.path("authorization");
		 logar.setAuthorization(token);
	}
	

	@Test
	public void retornaListaProdutos() {
		Response response = rest.get(PRODUTOS);

		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("quantidade"));
		assertThat(response.asString(), containsString("produtos"));
		assertThat(response.path("quantidade"), is(not(nullValue())));
		assertThat(response.path("produtos"), is(not(nullValue())));
		assertThat(response.path("quantidade"), is(greaterThan(0)));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/get/200.json"));
	}
	
	@Test
	public void retornaListaProdutosPorID() {	;
		Response response = rest.getId(PRODUTOS, "BeeJh5lz3k6kSIzA");
		assertThat(response.asString(), containsString("nome"));
		assertThat(response.asString(), containsString("preco"));
		assertThat(response.asString(), containsString("descricao"));
		assertThat(response.asString(), containsString("quantidade"));
		assertThat(response.path("nome"), is(not(nullValue())));
		assertThat(response.path("preco"), is(not(nullValue())));
		assertThat(response.path("descricao"), is(not(nullValue())));
		assertThat(response.path("quantidade"), is(not(nullValue())));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/get_{id}/200.json"));
	}
	
	@Test
	public void naoRetornaListaProdutosPorID() {
		Response response = rest.getId(PRODUTOS, "BeeJawerqwe4r1234h5lz3k6kSIzA");
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message"), is("Produto não encontrado"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/get_{id}/400.json"));
	}

	@Test
	public void cadastrarProduto() {
		produto = DynamicFactory.generateRandomProduct();
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		assertThat(response.statusCode(), is(201));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Cadastro realizado com sucesso"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/post/201.json"));
		
		String id = response.path("_id");
		produto.setId(id);
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
}
	
	@Test
	public void naoCadastrarProdutoSeNaoForAdmin() {
		produto = DynamicFactory.generateRandomProduct();
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		assertThat(response.statusCode(), is(201));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Cadastro realizado com sucesso"));
		
		String id = response.path("_id");
		produto.setId(id);
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
}
	
	@Test
	public void naoCadastrarProdutoSemToken() {
		produto = DynamicFactory.generateRandomProduct();
		
		Response response = rest.post(PRODUTOS, produto);
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/post/401.json"));
	
}
	@Test
	public void naoCadastrarProdutoComPrecoZeroOuNegativo() {
		produto.setNome(faker.commerce().productName());
		produto.setPreco(0);
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("preco"));
		assertThat(response.path("preco"), is(not(nullValue())));
		assertThat(response.path("preco").toString(), is("preco deve ser um número positivo"));
		;
}
	@Test
	public void naoCadastrarProdutoComNomeJaUtilizadoPorOutro() {
		produto.setNome("Logitech MX Vertical");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));

		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Já existe produto com esse nome"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/post/400.json"));
	}
	
	@Test
	public void naoCadastrarProdutoComNomeEDescricaoVazios() {
		produto.setNome("");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao("");
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("nome"));
		assertThat(response.asString(), containsString("descricao"));
		assertThat(response.path("nome"), is(not(nullValue())));
		assertThat(response.path("descricao"), is(not(nullValue())));
		assertThat(response.path("nome").toString(), is("nome não pode ficar em branco"));
		assertThat(response.path("descricao").toString(), is("descricao não pode ficar em branco"));
}
	
	@Test
	public void naoCadastrarProdutoComNomeEDescricaoEmBrancoBUG() {
		produto.setNome(" ");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(" ");
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = response.path("_id");
		produto.setId(id);
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
}
	@Test
	public void alterarInfoProdutos() {
		produto = DynamicFactory.generateRandomProduct();
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = response.path("_id");
		produto.setId(id);
		
		Product alteracao = new Product();
		alteracao.setNome(faker.commerce().productName());
		alteracao.setPreco(faker.random().nextInt(100, 1000));
		alteracao.setDescricao(faker.lorem().sentence());
		alteracao.setQuantidade(faker.random().nextInt(1, 300));
		Response alterar = rest.putToken(PRODUTOS, alteracao, id, logar.getAuthorization());
		assertThat(alterar.statusCode(), is(200));
		assertThat(alterar.path("message"), is(not(nullValue())));
		assertThat(alterar.path("message").toString(), is("Registro alterado com sucesso"));
		assertThat(alterar.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/put/200.json"));
			
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
					
	}
	
	@Test
	public void alterarNomedeProdutoParaOutroJaUtilizado() {
		produto.setNome("Headset Gamer Ryzen 700");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));

		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = response.path("_id");
		produto.setId(id);
		
		Product alteracao = new Product();
		alteracao.setNome("Logitech MX Vertical");
		alteracao.setPreco(faker.random().nextInt(100, 1000));
		alteracao.setDescricao(faker.lorem().sentence());
		alteracao.setQuantidade(faker.random().nextInt(1, 300));
		Response alterar = rest.putToken(PRODUTOS, alteracao, id, logar.getAuthorization());
		assertThat(alterar.statusCode(), is(400));
		assertThat(alterar.path("message"), is(not(nullValue())));
		assertThat(alterar.path("message").toString(), is("Já existe produto com esse nome"));
		assertThat(alterar.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/put/400.json"));
			
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
					
	}
	
	@Test
	public void alterarInfoProdutosSemToken() { 
		produto = DynamicFactory.generateRandomProduct();
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = response.path("_id");
		produto.setId(id);
		
		Product alteracao = new Product();
		alteracao.setNome(faker.commerce().productName());
		alteracao.setPreco(faker.random().nextInt(100, 1000));
		alteracao.setDescricao(faker.lorem().sentence());
		alteracao.setQuantidade(faker.random().nextInt(1, 300));
		Response alterar = rest.put(PRODUTOS, alteracao, id);
		assertThat(alterar.statusCode(), is(401));
		assertThat(alterar.path("message"), is(not(nullValue())));
		assertThat(alterar.path("message").toString(), is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		assertThat(alterar.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/put/401.json"));

	}
	
	@Test
	public void excluirProduto() {
		produto = DynamicFactory.generateRandomProduct();
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = response.path("_id");
		produto.setId(id);
		
		Response excluir = rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		assertThat(excluir.statusCode(), is(200));
		assertThat(excluir.asString(), containsString("message"));
		assertThat(excluir.path("message"), is(notNullValue()));
		assertThat(excluir.path("message").toString(), is("Registro excluído com sucesso"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/delete/200.json"));

	}
	
	@Test
	public void excluirProdutoQueFazParteDeCarrinho() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 80);
		
		
		rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());

		Response excluir = rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		assertThat(excluir.statusCode(), is(400));
		assertThat(excluir.asString(), containsString("message"));
		assertThat(excluir.path("message"), is(notNullValue()));
		assertThat(excluir.path("message").toString(), is("Não é permitido excluir produto que faz parte de carrinho"));
		assertThat(excluir.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/delete/400.json"));
		
		rest.concluirCompraToken(logar.getAuthorization());
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());

	}
	
	@Test
	public void exclurProdutoNaoExistente() {
		Response excluir = rest.deleteToken(PRODUTOS, "raweraserf", logar.getAuthorization());
		assertThat(excluir.statusCode(), is(200));
		assertThat(excluir.asString(), containsString("message"));
		assertThat(excluir.path("message"), is(notNullValue()));
		assertThat(excluir.path("message").toString(), is("Nenhum registro excluído"));
		assertThat(excluir.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/delete/200.json"));
	}
	
	@Test
	public void naoExcluirProdutoSemToken() {
		produto = DynamicFactory.generateRandomProduct();
		Response response = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = response.path("_id");
		produto.setId(id);
		
		Response excluir = rest.delete(PRODUTOS, id);
		assertThat(excluir.statusCode(), is(401));
		assertThat(excluir.asString(), containsString("message"));
		assertThat(excluir.path("message"), is(notNullValue()));
		assertThat(excluir.path("message").toString(), is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		assertThat(excluir.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/produtos/delete/401.json"));
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
	}
}
