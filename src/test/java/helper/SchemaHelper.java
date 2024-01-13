package helper;

import java.io.InputStream;
import java.text.MessageFormat;

import io.restassured.module.jsv.JsonSchemaValidator;

public class SchemaHelper {
	
	// Método utilizado para criar o caminho para um arquivo de Schema Json
	public static InputStream jsonSchemaStream(String endpoint, String schema, int status) {
		
		String path = "/schemas/{0}/{1}/{2}.json";
		path = MessageFormat.format(path, endpoint, schema, status);
		return SchemaHelper.class.getResourceAsStream(path);
	}
	
	// Matcher(validador) para validar um Schema Json de acordo com os caminhos recebidos
	public static JsonSchemaValidator matchesJsonSchema(String endpoint, String schema, int status) {
		InputStream schemaToMatch = jsonSchemaStream(endpoint, schema, status);
		return JsonSchemaValidator.matchesJsonSchema(schemaToMatch);
	}
}
