package antigo.S4;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import com.github.javafaker.Faker;
import model.Logar;
import model.Product;


public class Produtos_S4 extends Base_S4 {
	private static Faker faker = new Faker (Locale.ENGLISH);
	private static Product produto = new Product();
	private static Logar logar = new Logar();
	
	@Before
	public void fazerLogin() {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("defaultpass");
		
		String token = 
		given()
			.body(logar)
		.when()
			.post("/login")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body("message", is("Login realizado com sucesso"))
			.body("authorization", containsString("Bearer"))
			.body("authorization", matchesRegex("^Bearer [A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$"))
			.extract()
				.path("authorization");
		logar.setAuthorization(token);
		}

	@Test
	public void retornaListaProdutos() {
		given()
		.when()
			.get("/produtos")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body(containsString("quantidade"))
			.body("quantidade", is(greaterThan(0)))
			.body(containsString("produtos"))
			.extract()
				.response()
			;
	}
	
	@Test
	public void retornaListaProdutosPorID() {
		given()
			.pathParam("id", "K6leHdftCeOJj8BJ")
		.when()
			.get("produtos/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body(containsString("nome"))
			.body(containsString("preco"))
			.body(containsString("descricao"))
			.body(containsString("quantidade"))
			.body(containsString("_id"))
			.body("nome", is(not(nullValue())))
			.body("preco", is(greaterThan(0)))
			.body("descricao", is(not(nullValue())))
			.body("_id", is(not(nullValue())))
		;
	}
	
	@Test
	public void naoRetornaListaProdutosPorID() {
		given()
			.pathParam("id", "K6leHdftCeOJj8BJasd")
		.when()
			.get("produtos/{id}")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Produto não encontrado"))
		;
	}
	
	@Test
	public void naoCadastrarSemToken() {
		produto.setNome("Headset Gamer Ryzen Max 8000");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		given()
			.body(produto)
		.when()
			.post("/produtos")
		.then()
			.log().all()
			.statusCode(401)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"))
			;
}
	
	@Test
	public void cadastrarProduto() {
		produto.setNome(faker.commerce().productName());
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		String id =
		given()
			.header("Authorization", logar.getAuthorization())
			.body(produto)
		.when()
			.post("/produtos")
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
		.header("Authorization", logar.getAuthorization())
		.pathParam("id", id)
	.when()
		.delete("produtos/{id}")
	.then()
		.statusCode(200)
		.time(lessThan(3000L))
		.body(is(not(nullValue())))
		.body("message", is("Registro excluído com sucesso"))
		;
}
	
	@Test
	public void naoCadastrarProdutoSemToken() {
		produto.setNome(faker.commerce().productName());
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		given()
			.body(produto)
		.when()
			.post("/produtos")
		.then()
			.log().all()
			.statusCode(401)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"))
		.extract()
			.path("_id")
		;
}
	@Test
	public void naoCadastrarProdutoComPrecoZeroOuNegativo() {
		produto.setNome(faker.commerce().productName());
		produto.setPreco(0);
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));
	
		given()
			.header("Authorization", logar.getAuthorization())
			.body(produto)
		.when()
			.post("/produtos")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("preco", is("preco deve ser um número positivo"))
		.extract()
			.path("_id")
		;
}
	@Test
	public void naoCadastrarProdutoComNomeJaUtilizadoPorOutro() {
		produto.setNome("Logitech MX Vertical");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		given()
			.header("Authorization", logar.getAuthorization())
			.body(produto)
		.when()
			.post("/produtos")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Já existe produto com esse nome"))
		.extract()
			.path("_id")
		;
	}
	
	@Test
	public void naoCadastrarProdutoComNomeEDescricaoVazios() {
		produto.setNome("");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao("");
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		given()
			.header("Authorization", logar.getAuthorization())
			.body(produto)
		.when()
			.post("/produtos")
		.then()
			.log().all()
			.statusCode(400)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("nome", is("nome não pode ficar em branco"))
			.body("descricao", is("descricao não pode ficar em branco"))
			;
}
	
	@Test
	public void naoCadastrarProdutoComNomeEDescricaoEmBrancoBUG() {
		produto.setNome(" ");
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(" ");
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		String id =
		given()
			.header("Authorization", logar.getAuthorization())
			.body(produto)
		.when()
			.post("/produtos")
		.then()
			.log().all()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
		.extract()
			.path("_id")
			;
		;
		given()
		.header("Authorization", logar.getAuthorization())
		.pathParam("id", id)
	.when()
		.delete("produtos/{id}")
	.then()
		.statusCode(200)
		.time(lessThan(3000L))
		.body(is(not(nullValue())))
		.body("message", is("Registro excluído com sucesso"))
		;
}
	@Test
	public void alterarInfoProdutos() {
			produto.setNome(faker.commerce().productName());
			produto.setPreco(faker.random().nextInt(100, 1000));
			produto.setDescricao(faker.lorem().sentence());
			produto.setQuantidade(faker.random().nextInt(1, 300));
			
			String id =
					given()
						.header("Authorization", logar.getAuthorization())
						.body(produto)
					.when()
						.post("/produtos")
					.then()
						.log().all()
						.statusCode(201)
						.time(lessThan(3000L))
						.body(is(not(nullValue())))
						.body("message", is("Cadastro realizado com sucesso"))
					.extract()
						.path("_id")
					;
			Product alteracao = new Product();
			alteracao.setNome(faker.commerce().productName());
			alteracao.setPreco(faker.random().nextInt(100, 1000));
			alteracao.setDescricao(faker.lorem().sentence());
			alteracao.setQuantidade(faker.random().nextInt(1, 300));
			
			given()
				.header("Authorization", logar.getAuthorization())
				.body(alteracao)
				.pathParam("id", id)
			.when()
				.put("/produtos/{id}")
			.then()
				.statusCode(200)
				.time(lessThan(3000L))
				.body(is(not(nullValue())))
				.body("message", is("Registro alterado com sucesso"))
			;
			given()
			.header("Authorization", logar.getAuthorization())
			.pathParam("id", id)
		.when()
			.delete("produtos/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"))
		;	
	}
	
	@Test
	public void alterarInfoProdutosSemToken() { 
			produto.setNome(faker.commerce().productName());
			produto.setPreco(faker.random().nextInt(100, 1000));
			produto.setDescricao(faker.lorem().sentence());
			produto.setQuantidade(faker.random().nextInt(1, 300));
			
			String id =
					given()
						.header("Authorization", logar.getAuthorization())
						.body(produto)
					.when()
						.post("/produtos")
					.then()
						.statusCode(201)
						.time(lessThan(3000L))
						.body(is(not(nullValue())))
						.body("message", is("Cadastro realizado com sucesso"))
					.extract()
						.path("_id")
					;
			Product alteracao = new Product();
			alteracao.setNome(faker.commerce().productName());
			alteracao.setPreco(faker.random().nextInt(100, 1000));
			alteracao.setDescricao(faker.lorem().sentence());
			alteracao.setQuantidade(faker.random().nextInt(1, 300));
			
			given()
				.body(alteracao)
				.pathParam("id", id)
			.when()
				.put("/produtos/{id}")
			.then()
				.statusCode(401)
				.time(lessThan(3000L))
				.body(is(not(nullValue())))
				.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"))
			;
			given()
			.header("Authorization", logar.getAuthorization())
			.pathParam("id", id)
		.when()
			.delete("produtos/{id}")
		.then()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"))
		;	
	}
	
	@Test
	public void excluirProduto() {
		produto.setNome(faker.name().fullName());
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(1, 300));
		
		String id =
		given()
			.header("Authorization", logar.getAuthorization())
			.body(produto)
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Cadastro realizado com sucesso"))
			.body("_id", is(not(nullValue())))
		.extract()
			.path("_id")
			;
		given()
			.header("Authorization", logar.getAuthorization())
			.pathParam("id", id)
		.when()
			.delete("produtos/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.body(is(not(nullValue())))
			.body("message", is("Registro excluído com sucesso"))
		;
	}
	
	@Test
	public void exclurProdutoNaoExistente() {
		given()
			.header("Authorization", logar.getAuthorization())
			.pathParam("id", "000")
		.when()
			.delete("produtos/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Nenhum registro excluído"))
		;
	}
	
	@Test
	public void naoExclurProdutoSemToken() {
		given()
			.pathParam("id", "000")
		.when()
			.delete("produtos/{id}")
		.then()
			.log().all()
			.statusCode(401)
			.time(lessThan(3000L))
			.body(is(not(nullValue())))
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"))
		;
	}
}
