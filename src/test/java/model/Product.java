package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Data
public class Product {
	private String nome;
	private String descricao;
	
	@JsonFormat(shape = Shape.STRING)
	private Integer preco;
	
	@JsonFormat(shape = Shape.STRING)
	private Integer quantidade;
	
	@JsonProperty(value = "_id", access = Access.WRITE_ONLY)
	private String  id; //É lido apenas na desserialização
}
