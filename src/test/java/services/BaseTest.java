package services;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.*;

import static io.restassured.http.ContentType.JSON;
import org.junit.Before;
import org.junit.BeforeClass;

import helper.EnvConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BaseTest {
	protected BaseRest rest;
	protected UsuariosService usuariosService;
	protected ProdutosService produtosService;
	protected CarrinhosService carrinhosService;

	@BeforeClass
	public static void setupEnvironment() {
		baseURI = "http://localhost";
		port = 3000;
		
		RestAssured.requestSpecification = new RequestSpecBuilder()
				.setBaseUri(EnvConfig.getProperty("url", ""))
				.setContentType(JSON)
				.setAccept(JSON)
				.build();
		
		RestAssured.responseSpecification = new ResponseSpecBuilder()
				.log(LogDetail.ALL)
				.expectResponseTime(lessThan(3000L))
				.expectBody(is(not(nullValue())))
				.build();
	}
	
	@Before
	public void instantiateServices() {
		rest = new BaseRest();
		usuariosService = new UsuariosService(rest);
		produtosService = new ProdutosService(rest);
		
	}
}
