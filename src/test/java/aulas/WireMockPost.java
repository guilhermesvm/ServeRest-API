package aulas;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


import org.junit.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import model.User;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@WireMockTest(httpPort = 9999)
public class WireMockPost {
	
	@RegisterExtension
	static WireMockExtension wiremock = WireMockExtension.newInstance()
	.options(wireMockConfig()
			.port(9999)
			.extensions(new ResponseTemplateTransformer(true)))
			.build();

	@Test
	public void postUsuariosComWireMock() {
		User user = new User();
		user.setNome("Mr.Blues");
		user.setEmail("mrblues@pixar.com");
		user.setPassword("qwerty123");
		user.setAdministrador(false);
		
		String expectedName = "Bruce";
		
			when()
				.post("http://localhost:9999/usuarios")
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
