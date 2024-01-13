package services;

import static constants.Endpoints.*;
import static io.restassured.RestAssured.*;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import model.Logar;

@Getter
@Setter
public class BaseRest {
	public Response get(String endpoint) {
		return
		given()
		.when()
			.get(endpoint)
		.then()
			.extract()
				.response();		
	}
	
	public Response getId(String endpoint, String id) {
		return
		given()
			.pathParam("id", id)
		.when()
			.get(endpoint + "/" + "{id}")
		.then()
			.extract()
				.response();		
	}
	
	public Response post(String endpoint, Object user) {
		return
			given()
				.body(user)
			.when()
				.post(endpoint)
			.then()
				.extract()
					.response();
	}
	
	public Response postToken(String endpoint, Object produto, String token) {
		return
			given()
				.header("Authorization", token)
				.body(produto)
			.when()
				.post(endpoint)
			.then()
				.extract()
					.response();
	}
	
	public Response put(String endpoint, Object alteracao, String id) {
		return
			given()
				.body(alteracao)
				.pathParam("id", id)
			.when()
				.put(endpoint + "/" + "{id}")
			.then()
			.extract()
				.response();
	}
	
	public Response putToken(String endpoint, Object alteracao, String id, String token) {
		return
			given()
				.header("Authorization", token)
				.body(alteracao)
			.	pathParam("id", id)
			.when()
				.put(endpoint + "/" + "{id}")
			.then()
				.extract()
					.response();
	}
	
	public Response delete(String endpoint, String id) {
		return
			given()
				.pathParam("id", id)
			.when()
				.delete(endpoint + "/" + "{id}")
			.then()
				.extract()
					.response();
	}
	
	public Response deleteToken(String endpoint, String id, String token) {
		return
			given()
				.header("Authorization", token)
			.	pathParam("id", id)
			.when()
				.delete(endpoint + "/" + "{id}")
			.then()
				.extract()
					.response();
	}
	
	public Response concluirCompraToken(String token) {
		return
				given()
					.header("Authorization", token)
				.when()
				.delete(CARRINHOS + CONCLUIR_COMPRA)
				.then()
					.extract()
						.response();
		}
	
	public Response cancelarCompraToken(String token) {
		return
				given()
					.header("Authorization", token)
				.when()
				.delete(CARRINHOS + CANCELAR_COMPRA)
				.then()
					.extract()
						.response();
		}
	public Response concluirCompra() {
		return
				given()
				.when()
				.delete(CARRINHOS + CONCLUIR_COMPRA)
				.then()
					.extract()
						.response();
		}
	
	public Response cancelarCompra() {
		return
				given()
				.when()
				.delete(CARRINHOS + CANCELAR_COMPRA)
				.then()
					.extract()
						.response();
		}
	
	public Response logar(String endpoint, Logar logar) {
		return
		given()
			.body(logar)
		.when()
			.post(endpoint)
		.then()
			.extract()
				.response();		
	}
		
	}

