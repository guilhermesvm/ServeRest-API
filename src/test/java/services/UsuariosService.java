package services;

import static constants.Endpoints.USUARIOS;

import java.util.List;
import datafactory.DynamicFactory;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import model.User;

@Getter
@Setter
public class UsuariosService {
	private BaseRest rest;
	
	public UsuariosService(BaseRest rest) {
		this.rest = rest;
	}
	
	/*public User getUser(String id) {
		return rest.get(USUARIOS +"/"+ id).as(User.class);
	}
	
	public void getUser(BaseService rest) {
		this.rest = rest;
	}*/
	
	/*public void deleteUser(BaseRest rest) {
		this.rest = rest;
	}
	
	public Response deleteUser(String userId) {
		return rest.delete(USUARIOS + "/" + userId);
	}*/
	

	public List<User> getUsers(){
		return rest.get(USUARIOS).jsonPath().getList("usuarios", User.class);
	}
	
	public Response createRandomUser() {
		User user = DynamicFactory.generateRandomUser(true);
		return rest.post(USUARIOS, user);
	}
}

