package services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutosService {
	private BaseRest rest;

	public ProdutosService(BaseRest rest) {
		this.rest = rest;
	}
	
	/*public void getProd(BaseService rest) {
		this.rest = rest;
	}
	
	public Product getProd(String userId) {
		return rest.get(PRODUTOS +"/"+ userId).as(Product.class);
	}*/
	
	/*public void deleteProd(BaseRest rest) {
		this.rest = rest;
	}
	
	public Response deleteProd(String userId) {
		return rest.delete(PRODUTOS + "/" + userId);
	}*/
}


