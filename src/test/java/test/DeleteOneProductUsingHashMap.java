package test;

import static io.restassured.RestAssured.given;
import java.io.File;
import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

//end to end validation: 1.create a product 2.read all products to get firstProductID 
//3.read One product with id to validate if the product we provided in payload is appearing

public class DeleteOneProductUsingHashMap {

	String baseURI;
	SoftAssert softAssert;
	String readOneProductID;
	HashMap<String, String> deleteProductData;// passing createOneProductPayload as hashmap

	public DeleteOneProductUsingHashMap() {
		baseURI = "https://techfios.com/api-prod/api/product";
		softAssert = new SoftAssert();
	}

	
	public HashMap<String, String> getDeletePayloadAsHashMap() {

		deleteProductData = new HashMap<String, String>();

		deleteProductData.put("id", "6954");
		
		return deleteProductData;

	}

	@Test(priority = 1)
	public void deleteOneProductUsingHashMap() {

		Response response =

				given().baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json; charset=UTF-8")
						.header("Authorization", "Bearer KKKhughghkkIIInkwlhd")// bearer token
						.body(getDeletePayloadAsHashMap()).

						/*
						 * ways to pass post payload: 1. external json file 2. pojo class 3. hashMap 4.
						 * org.Json
						 */

						when().delete("/delete.php").then()

						.extract().response();

		// validating statusCode
		int statusCode = response.getStatusCode();
		System.out.println("Status code : " + statusCode);
		softAssert.assertEquals(statusCode, 200, "Status codes do not match");

		// validating response header
		String responseHeadercontentType = response.getHeader("Content-Type");
		System.out.println("Response body header : " + responseHeadercontentType);
		Assert.assertEquals(responseHeadercontentType, "application/json; charset=UTF-8",
				"Response body headers do not match");

		String responseBody = response.getBody().asString();
		System.out.println("Response Body : " + responseBody);

		JsonPath jsonPath = new JsonPath(responseBody);

		String messageFromResponseBody = jsonPath.getString("message");
		System.out.println("message from response body : " + messageFromResponseBody);
		softAssert.assertEquals(messageFromResponseBody, "Product was deleted.", "Messages not matching");
		softAssert.assertAll();// should be very last
		// statement after all softasserts catches all the failed assertions if any
		readOneProductID = getDeletePayloadAsHashMap().get("id");// assigning readOneProductId the id we are sending in
																	// update hashmap

	}

	@Test(priority = 2)
	public void readOneProduct() {

		Response response =

				given()
						// log().all().
						.baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json").queryParam("id", readOneProductID)
						.header("Authorization", "Bearer KKKhughghkkIIInkwlhd").// bearer token

						when().get("/read_one.php").then()

						.extract().response();

		// validating statusCode
		int statusCode = response.getStatusCode();
		System.out.println("Status code : " + statusCode);
	
		softAssert.assertEquals(statusCode, 404, "Status codes do not match");

		
		String responseBody = response.getBody().asString();

		System.out.println("Response Body : " + responseBody);

		JsonPath jsonPath = new JsonPath(responseBody);

		String actualDeletMessage = jsonPath.getString("message");
		String expectedDeleteMessage = "Product does not exist.";
		softAssert.assertEquals(actualDeletMessage, expectedDeleteMessage, "Messages are not matching");


		softAssert.assertAll();
	}

}
