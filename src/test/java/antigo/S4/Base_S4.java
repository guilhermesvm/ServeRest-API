package antigo.S4;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;


import org.junit.BeforeClass;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;



public class Base_S4 {

	@BeforeClass
	public static void setup() {
		baseURI = "http://localhost";
		port = 3000;
		
		RestAssured.requestSpecification = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.build();
		
		/*RestAssured.responseSpecification = new ResponseSpecBuilder()
				.expectStatusCode(200)
				.expectResponseTime(lessThan(5000L))
				.expectBody(is(not(nullValue())))
				.log(LogDetail.ALL)
				.build();*/
		
	}
}
