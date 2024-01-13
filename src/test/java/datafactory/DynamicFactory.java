package datafactory;

import java.util.Locale;

import com.github.javafaker.Faker;
import model.Product;
import model.User;

public class DynamicFactory {
	private static Faker faker = new Faker (Locale.ENGLISH);
	private static User user = new User();
	private static Product produto = new Product();

	
	public static User generateRandomUser(boolean isAdmin) {
		user.setNome(faker.name().fullName());
		user.setEmail(faker.internet().emailAddress());
		user.setPassword(faker.internet().password());
		user.setAdministrador(isAdmin);
		return user;
	}
	public static Product generateRandomProduct() {
		produto.setNome(faker.commerce().productName());
		produto.setPreco(faker.random().nextInt(100, 1000));
		produto.setDescricao(faker.lorem().sentence());
		produto.setQuantidade(faker.random().nextInt(100, 500));
		return produto;
	}
	

	public static String generateCart(String id, Integer quantidade) {
		String carrinho = "{\r\n"
				+ "  \"produtos\": [\r\n"
				+ "    {\r\n"
				+ "      \"idProduto\":\"" + id +"\",\r\n"
				+ "      \"quantidade\":\"" + quantidade +"\"\r\n"
				+ "    }\r\n"
				+ "  ]\r\n"
				+ "}";
		
		return carrinho;
	}
}
