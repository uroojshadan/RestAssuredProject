package test;

//static import because given() when() then() are static methods.We need to import static packages manually
import static io.restassured.RestAssured.given;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;


public class ReadAllProductsTest {

	String baseURI;
	
	public ReadAllProductsTest() {//constructor
		baseURI="https://techfios.com/api-prod/api/product";
	}
	@Test
	public void readAllProducts() {

		/*
		 * Read All Products 
		 * HttpMethod used for reading: GET (green color in
		 * postman) 
		 * EndPointURL: https://techfios.com/api-prod/api/product/read.php
		 * Authorization: basic Auth: Username and Password/ bearer token Header/s: (add
		 * these headers on request window) 
		 * Content-Type=applications/json; charset=UTF-8 
		 * StatusCode: 200 (this is expected when we get/read all products
		 * and all products are displayed in the response window)
		 */
		
		/*
		 * given: all input details(baseURI,Headers,Authorization,Payload/Body,QueryParameters)
		 * when: submit api requests(Http method,Endpoint/Resource) 
		 * then: validate response(status code, Headers, responseTime, Payload/Body)  
		 */
		Response response=
		
		given()
			//log().all().
			.baseUri(baseURI)//gets baseURI from constructor
			.header("Content-Type","application/json; charset=UTF=8")
			.auth().preemptive().basic("demo@techfios.com", "abc123").
			/*
			 * preemptive()
			 * Returns the preemptive authentication view. 
			 * This means that the authentication details are sent in the request header 
			 * regardless if the server has challenged for authentication or not.
			 * 
			 */
			// in ReadAllProducts we donot need body or query paramaters hence coming to
			// when part
		when()
			//.log().all()
			.get("/read.php").
		then()
			//.log().all()//printing response body
			//.statusCode(200)
			//.header("Content-Type", "application/json; charset=UTF-8")
		 	//.body("records[0].id",equalTo("6907"))->using hamcrest dependency
			//import static org.hamcrest.Matchers.*;->write this manually
		/*
		 * we can validate the entire response by using extract().response() and store
		 * the entire response in a var and then sift out any particular field that we
		 * want like status code ,time etc
		 */
			.extract().response();
		
		
			
		//validating statusCode
		int statusCode=response.getStatusCode();
		System.out.println("Status code : "+statusCode);
		Assert.assertEquals(statusCode, 200,"Status codes do not match");
		
		//validating response header
		String responseHeadercontentType=response.getHeader("Content-Type");
		System.out.println("Response body header : "+responseHeadercontentType);
		Assert.assertEquals(responseHeadercontentType,"application/json; charset=UTF-8","Response body headers do not match");
		
		String responseBody=response.getBody().asString();
		/*
		 * String responseBody=response.getBody().toString();-->not using toString()
		 * beacuse this method returns a string representation of the object whereas
		 * asString() returns the body as a string.
		 */
		System.out.println("Response Body : "+responseBody);//-->astring() prints body in single line
		//response.getBody().prettyPrint();//gets in a nice format and not in single line
		
		//to convert the response body from string to json format so that we can traverse it and get id etc from body
		/*
		 * JsonPath is an alternative to using XPath for easily getting values from a Object document. 
		 * to traverse through the response body and get a particular element we need to
		 * convert the String response body to json path this uses the dependencies
		 * json-path and json-schema-validator from pom.xml
		 */
		JsonPath jsonPath=new JsonPath(responseBody);
		String firstProductIDFromResponseBody=jsonPath.getString("records[0].id");
		System.out.println("first product id : "+firstProductIDFromResponseBody);
		
		if(firstProductIDFromResponseBody!=null) {
			System.out.println("Product list is not empty");
		}
		else {
			System.out.println("Product list is empty");
		}
	}
}
