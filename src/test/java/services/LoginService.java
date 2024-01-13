package services;

import lombok.Getter;
import lombok.Setter;
import model.Logar;

@Getter
@Setter
public class LoginService {
	private static Logar logar = new Logar();
	public static Logar loginAccount() {
		logar.setEmail("defaultuser@qa.com");
		logar.setPassword("defaultpass");
		return logar;
		
	}
}
