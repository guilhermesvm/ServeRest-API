package aulas;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest(httpPort = 9999)
public class WireMockGet {

	@Test
	public void getUsuariosDeveRetornarComWireMock() {
		String expectedName = "Bruce";
		
			when()
				.get("http://localhost:9999/usuarios")
			.then()
				.log().all()
			.and()
				.assertThat()
					.statusCode(200)
			.and()
				.body("[0].name", is(expectedName))
				;
	}
}
