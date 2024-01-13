package aulas;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static helper.SchemaHelper.matchesJsonSchema;

import org.junit.Test;

import io.restassured.response.Response;
import services.BaseTest;

public class JsonSchema extends BaseTest{

	@Test
	public void deveValidarJsonSchemaNoRetornoDaListaUsuarios() { //GET
		Response response = when().get("/usuarios");
		assertThat(response.asString(), matchesJsonSchema("usuarios", "get", 200));
	}





}
