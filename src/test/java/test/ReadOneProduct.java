package test;

//static import because given() when() then() are static methods.We need to import static packages manually
import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class ReadOneProduct {

	String baseURI;
	SoftAssert softAssert;
	
	public ReadOneProduct() {//constructor called first implicitly
		baseURI="https://techfios.com/api-prod/api/product";
		softAssert=new SoftAssert();
	}
	@Test
	public void readOneProduct() {

		
		/*
		 * Read One Product
		 * EndpointURL: https://techfios.com/api-prod/api/product/read_one.php
		 * QueryParameters: id=6739(we can also provide 2 ids-->https://techfios.com/api-prod/api/product/read_one.php?id=6739&id=6740)
		 * Authorization: basic Auth: Username and Password/ bearer token
		 * Header/s : Content-Type=application/json
		 *  StatusCode=200
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
			.header("Content-Type","application/json")
			.queryParam("id", "6742")
			.header("Authorization","Bearer KKKhughghkkIIInkwlhd").//bearer token
			
		when()
			.get("/read_one.php").
		then()
			
			.extract().response();
		
			
		//validating statusCode
		int statusCode=response.getStatusCode();
		System.out.println("Status code : "+statusCode);
		//softAssert.assertEquals(statusCode, 201,"Status codes do not match");//intetionally failing to test softAssert
		softAssert.assertEquals(statusCode, 200,"Status codes do not match");
		
		//validating response header
		String responseHeadercontentType=response.getHeader("Content-Type");
		System.out.println("Response body header : "+responseHeadercontentType);
		Assert.assertEquals(responseHeadercontentType,"application/json","Response body headers do not match");
		
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
		
		/*Response from read one product:
		 * {
		 *  "id": "6742",
		 *  "name": "Amazing Pillow 2.0",
		 *  "description": "The best pillow for amazing programmers.",
		 *  "price": "199",
		 *  "category_id": "2",
		 *  "category_name": "Electronics"
		 *  }
		 */
		
		/*
		 * String productName=jsonPath.getString("name");
		 * Assert.assertEquals(productName,
		 * "Amazing Pillow 2.0","Product name not matching");
		 * 
		 * String productDescription=jsonPath.getString("description");
		 * Assert.assertEquals(productDescription,
		 * "The best pillow for amazing programmers.");
		 * 
		 * String productPrice=jsonPath.getString("price");
		 * Assert.assertEquals(productPrice, "199","Prices not matching");
		 * 
		 * String productCategory_id=jsonPath.getString("category_id");
		 * Assert.assertEquals(productCategory_id, "2","category ids not matching");
		 */
		
		/*
		 * here if status code hard assertion or any hard assert fails then program execution is 
		 * halted hence to prevent this and run entire execution without halting  we can use soft asserts
		 */
		/*
		 * Soft asserts are the asserts which continue the execution even after the Assert condition fails.
		 * Soft asserts are mainly used to verify certain test conditions in the code. 
		 * It is used in a case when the passing of one test condition is not necessary to execute the upcoming tests 
		 * 
		 */
		
		 String productName=jsonPath.getString("name");
		 softAssert.assertEquals(productName,"Amazing Pillow 2.0","Product name not matching");
		 
		 String productDescription=jsonPath.getString("description");
		 softAssert.assertEquals(productDescription,"The best pillow for amazing programmers.");
		 
		 String productPrice=jsonPath.getString("price");
		 softAssert.assertEquals(productPrice, "199","Prices not matching");
		
		 String productCategory_id=jsonPath.getString("category_id");
		 softAssert.assertEquals(productCategory_id, "2","category ids not matching");
		
		 softAssert.assertAll();//should be very last 
		 //statement after all softasserts catches all the failed assertions if any
		
	}
}
