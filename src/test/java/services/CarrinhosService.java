package services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarrinhosService {
	private BaseRest rest;

	public CarrinhosService(BaseRest rest) {
		this.rest = rest;
	}
	
	/*public void getCart(BaseRest rest) {
		this.rest = rest;
	}
	
	public Cart getCart(String userId) {
		return rest.get(CARRINHOS +"/"+ userId).as(Cart.class);
	}*/
	
	
	/*public void deleteCart(BaseRest rest) {
		this.rest = rest;
	}
	
	public Response deleteCart(String userId) {
		return rest.delete(CARRINHOS + "/" + userId);
	}*/
}
