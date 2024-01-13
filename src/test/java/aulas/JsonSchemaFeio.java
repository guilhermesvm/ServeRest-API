package aulas;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.InputStream;


import org.junit.Test;

import antigo.S4.Base_S4;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;

public class JsonSchemaFeio extends Base_S4{
	


	@Test
	public void deveValidarJsonSchemaNoRetornoDaListaUsuarios() { //GET
		Response response = when().get("/usuarios");
		InputStream schemaToValidate = getClass().getClassLoader().getResourceAsStream("get_usuarios_200.json");
		assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchema(schemaToValidate));
	}





}
