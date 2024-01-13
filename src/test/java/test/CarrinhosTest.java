package test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static constants.Endpoints.CARRINHOS;
import static constants.Endpoints.LOGIN;
import static constants.Endpoints.PRODUTOS;


import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import datafactory.DynamicFactory;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import model.Cart;
import model.Logar;
import model.Product;
import services.BaseTest;
import services.LoginService;

public class CarrinhosTest extends BaseTest {
	private static Product produto = new Product();
	private static Logar logar = new Logar();
	@SuppressWarnings("unused")
	private static Cart carrinho = new Cart();

	@Before
	public void fazerLogin() {
		logar = LoginService.loginAccount();

		Response response = rest.post(LOGIN, logar);
		String token = response.path("authorization");
		logar.setAuthorization(token);
	}

	@Test
	public void retornaListaCarrinhos() {
		Response response = rest.get(CARRINHOS);

		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("quantidade"));
		assertThat(response.asString(), containsString("carrinhos"));
		assertThat(response.asString(), containsString("produtos"));
		//assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/get/200.json"));
	}

	@Test
	public void retornaListaCarrinhosPorID() {
		Response response = rest.getId(CARRINHOS, "qbMqntef4iTOwWfg");
		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("produtos"));
		assertThat(response.asString(), containsString("quantidadeTotal"));
		assertThat(response.asString(), containsString("precoTotal"));
		assertThat(response.asString(), containsString("quantidadeTotal"));
		assertThat(response.asString(), containsString("idUsuario"));
		assertThat(response.asString(), containsString("_id"));
		assertThat(response.path("quantidadeTotal"), is(instanceOf(Integer.class)));
		assertThat(response.path("precoTotal"), is(instanceOf(Integer.class)));
		assertThat((List<?>) response.path("produtos"), hasSize(greaterThan(0)));
		//assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/get_{id}/200.json"));
		List<Map<String, ?>> produtos = response.path("produtos");
		for (Map<String, ?> produto : produtos) {
			assertThat(produto, hasKey("idProduto"));
			assertThat(produto, hasKey("quantidade"));
			assertThat(produto, hasKey("precoUnitario"));
			assertThat(produto, hasKey("precoUnitario"));
			assertThat(produto.get("quantidade"), is(instanceOf(Integer.class)));
			assertThat(produto.get("precoUnitario"), is(instanceOf(Integer.class)));
			assertThat((Integer) produto.get("quantidade"), greaterThan(0));
			assertThat((Integer) produto.get("precoUnitario"), greaterThan(0));
		}
	}

	@Test
	public void naoRetornaListaCarrinhosPorID() {
		Response response = rest.getId(CARRINHOS, "LJ5BBTasfaf5EmoLc1");
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message"), is("Carrinho não encontrado"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/get_{id}/400.json"));
	}

	@Test
	public void cadastrarCarrinho() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 80);
		
		
		Response response = rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
		assertThat(response.statusCode(), is(201));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message").toString(), is("Cadastro realizado com sucesso"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/post/200.json"));
		
		Response concluir = rest.concluirCompraToken(logar.getAuthorization());
		assertThat(concluir.statusCode(), is(200));
		
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());

}

	@Test
	public void naoCadastrarCarrinhoSemToken() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 200);

		Response response = rest.post(CARRINHOS, carrinho);
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/post/401.json"));
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
	}

	@Test
	public void naoCadastrarCarrinhoSeAQuantidadeDeProdutoForMaiorDoQueADoEstoque() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 800);
		Response response = rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Produto não possui quantidade suficiente"));
		assertThat(response.asString(), containsString("item"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/post/400.json"));
		
		String idProduto = response.path("item.idProduto");
	    int quantidade = response.path("item.quantidade");
	    int quantidadeEstoque = response.path("item.quantidadeEstoque");
	    assertThat(idProduto, is(not(nullValue())));
	    assertThat(quantidade, is(not(nullValue())));
	    assertThat(quantidadeEstoque, is(not(nullValue())));
	   
	    rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
	}

	@Test
	public void naoCadastrarCarrinhoComProdutoInexistente() {
		String carrinho = DynamicFactory.generateCart("e4234sdfaf45", 800);
		Response response = rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Produto não encontrado"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/post/400.json"));
		
	}

	@Test
	public void naoCadastrarCarrinhoComQuantidadeZeroDeUmProduto() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 0);
		Response response = rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
		assertThat(response.statusCode(), is(400));
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
	}
	
	@Test
	public void naoCadastrarProdutoNoCarrinhoComProdutoJaExistente() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 80);
		
		
		rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
		Response response = rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
		assertThat(response.statusCode(), is(400));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Não é permitido ter mais de 1 carrinho"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/post/400.json"));
		
		
		rest.concluirCompraToken(logar.getAuthorization());
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
	}

	@Test
	public void ConcluirCompra() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 80);
		rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
	
		Response response = rest.concluirCompraToken(logar.getAuthorization());
		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Registro excluído com sucesso"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/concluir_compra/200.json"));

		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
	}
	
	@Test
	public void naoConcluirCompraSemExistirCarrinho() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		Response response = rest.concluirCompraToken(logar.getAuthorization());
		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Não foi encontrado carrinho para esse usuário"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/concluir_compra/200.json"));
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
	}
	
	@Test
	public void naoConcluirCompraSemToken() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 80);
		rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
	
		Response response = rest.concluirCompra();
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/concluir_compra/401.json"));
		
		rest.concluirCompraToken(logar.getAuthorization());
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
	}
	
	@Test
	public void CancelarCompra() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 80);
		rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
	
		Response response = rest.cancelarCompraToken(logar.getAuthorization());
		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Registro excluído com sucesso. Estoque dos produtos reabastecido"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/cancelar_compra/200.json"));
	
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
	}
	
	@Test
	public void naoCancelarCompraSemToken() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		String carrinho = DynamicFactory.generateCart(id, 80);
		rest.postToken(CARRINHOS, carrinho, logar.getAuthorization());
	
		Response response = rest.cancelarCompra();
		assertThat(response.statusCode(), is(401));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/cancelar_compra/401.json"));
		
		
		rest.cancelarCompraToken(logar.getAuthorization());
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
	}
	
	@Test
	public void naoCancelarCompraSemExistirCarrinho() {
		produto = DynamicFactory.generateRandomProduct();
		Response product = rest.postToken(PRODUTOS, produto, logar.getAuthorization());
		String id = product.path("_id");
		produto.setId(id);
		
		Response response = rest.cancelarCompraToken(logar.getAuthorization());
		assertThat(response.statusCode(), is(200));
		assertThat(response.asString(), containsString("message"));
		assertThat(response.path("message"), is(not(nullValue())));
		assertThat(response.path("message").toString(), is("Não foi encontrado carrinho para esse usuário"));
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/carrinhos/cancelar_compra/200.json"));
		
		
		rest.deleteToken(PRODUTOS, id, logar.getAuthorization());
		
	}

	
}
