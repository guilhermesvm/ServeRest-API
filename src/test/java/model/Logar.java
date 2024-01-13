package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;


@Data
public class Logar {
	private String email;
	private String password;
	
	@JsonProperty(value = "authorization", access = Access.WRITE_ONLY)
	private String authorization;
}
