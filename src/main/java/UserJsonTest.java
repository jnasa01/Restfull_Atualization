import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;
import org.junit.Test;

import io.restassured.http.Method;
import io.restassured.internal.http.Status;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {
	@Test
	public void deveVerificarPrimeiroNivel(){
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(21))
			;
	}
	
	@Test
	public void deveVerificarPrimeiroNivelOutrasFormas() {
		
		Response response = request(Method.GET, "https://restapi.wcaquino.me/users/1");
		
		//path
		assertEquals(new Integer(1), (response.path("id")));
		assertEquals(new Integer(1), (response.path("%s", "id")));
		
		//jsonpath
		JsonPath jpath = new JsonPath(response.asString());
		assertEquals(1, jpath.getInt("id"));
		
		//from
		int id = JsonPath.from(response.asString()).getInt("id");
		assertEquals(1, id);
	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"))
			.body("endereco.rua", containsString("bobos"))
			.body("endereco.numero", is (0))
			;
	}
	
	@Test
	public void devoValidarListaSegundoNivel() {
		
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2))
			//para lista abaixo, tratamos o objeto "filhos" como array e o primeiro indice � 0
			.body("filhos[0].name", containsString("Zezinho"))
			.body("filhos[1].name", containsString("Luizinho"))
			.body("filhos.name", hasItem("Luizinho"))
			.body("filhos.name", hasItems("Luizinho", "Luizinho"))
				;
	}
	
	@Test
	public void devoRetornarErroUsuarioInexistente() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/4")
		.then()
		 	.statusCode(404)
			.body("error", is("Usu�rio inexistente"))
			;
	}
	@Test
	public void deveVerificarListaRaiz() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
		 	.statusCode(200)
		 	.body("", hasSize(3))
		 	//.body("name", hasItems("Jo�o da Silva", "Maria Joaquina", "Ana J�lia"))
		 	.body("name", hasItems("Jo�o da Silva", "Maria Joaquina", "Ana J�lia"))
		 	.body("age[1]",is (25))
		 	.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
		 	.body("salary", contains(1234.5678f, 2500 , null))
		 	;
	}
	
	@Test
	public void devoFazerVerificacoesAvancadas() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
		 	.statusCode(200)
		 	.body("", hasSize(3))
		 	.body("age.findAll{it <= 25}.size()", is(2))
		 	.body("age.findAll{it <= 25 && it > 20}.size()", is(1))
		 	.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
		 	.body("findAll{it.age <= 25}.name", hasItems("Maria Joaquina", "Ana J�lia"))
		 	.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
		 	.body("findAll{it.age <= 25}[-1].name", is("Ana J�lia"))
		 	.body("find{it.age <= 25}.name", is("Maria Joaquina"))
		 	.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana J�lia"))
		 	.body("findAll{it.name.length() > 10}.name", hasItems("Jo�o da Silva", "Maria Joaquina"))
		 	.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
		 	.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
		 	.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase().toArray()", allOf(arrayContaining("Maria Joaquina"), arrayWithSize(1)))
		 			// allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
		 	
		 	;
	;
	}

}
