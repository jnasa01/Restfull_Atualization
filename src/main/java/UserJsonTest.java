import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
			//para lista abaixo, tratamos o objeto "filhos" como array e o primeiro indice é 0
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
			.body("error", is("Usuário inexistente"))
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
		 	.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
		 	;
	}

}
