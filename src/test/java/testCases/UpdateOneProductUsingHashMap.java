package testCases;

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

public class UpdateOneProductUsingHashMap {

	String baseURI;
	SoftAssert softAssert;
	String readOneProductID;
	HashMap<String, String> updateProductData;// passing createOneProductPayload as hashmap

	public UpdateOneProductUsingHashMap() {
		baseURI = "https://techfios.com/api-prod/api/product";
		softAssert = new SoftAssert();
	}

	
	public HashMap<String, String> getUpdatePayloadAsHashMap() {

		updateProductData = new HashMap<String, String>();

		updateProductData.put("id", "6954");
		updateProductData.put("name", "Amazing Pillow 5.0");
		updateProductData.put("price", "500");
		updateProductData.put("description", "The best pillow for amazing programmers2.");
		updateProductData.put("category_id", "2");

		return updateProductData;

	}

	@Test(priority = 1)
	public void updateOneProductUsingHashMap() {

		Response response =

				given().baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json; charset=UTF-8")
						.header("Authorization", "Bearer KKKhughghkkIIInkwlhd")// bearer token
						.body(getUpdatePayloadAsHashMap()).

						/*
						 * ways to pass post payload: 1. external json file 2. pojo class 3. hashMap 4.
						 * org.Json
						 */

						when().put("/update.php").then()

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
		softAssert.assertEquals(messageFromResponseBody, "Product was updated.", "Messages not matching");
		softAssert.assertAll();// should be very last
		// statement after all softasserts catches all the failed assertions if any
		readOneProductID = getUpdatePayloadAsHashMap().get("id");// assigning readOneProductId the id we are sending in
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
		// softAssert.assertEquals(statusCode, 201,"Status codes do not
		// match");//intetionally failing to test softAssert
		softAssert.assertEquals(statusCode, 200, "Status codes do not match");

		// validating response header
		String responseHeadercontentType = response.getHeader("Content-Type");
		System.out.println("Response body header : " + responseHeadercontentType);
		Assert.assertEquals(responseHeadercontentType, "application/json", "Response body headers do not match");

		String responseBody = response.getBody().asString();

		System.out.println("Response Body : " + responseBody);

		JsonPath jsonPath = new JsonPath(responseBody);

		String actualProductId = jsonPath.getString("id");
		softAssert.assertEquals(actualProductId, readOneProductID, "Product ids donot match");

		String actualProductName = jsonPath.getString("name");
		String expectedProductName = getUpdatePayloadAsHashMap().get("name");
		softAssert.assertEquals(actualProductName, expectedProductName, "Product names are not matching");

		String actualproductDescription = jsonPath.getString("description");
		String expectedProductDescription = getUpdatePayloadAsHashMap().get("description");
		softAssert.assertEquals(actualproductDescription, expectedProductDescription,
				"Product Description not matching");

		String actualProductPrice = jsonPath.getString("price");
		String expectedProductPrice = getUpdatePayloadAsHashMap().get("price");
		softAssert.assertEquals(actualProductPrice, expectedProductPrice, "Prices not matching");

		String actualProductCategory_id = jsonPath.getString("category_id");
		String expectedProductCategory_id = getUpdatePayloadAsHashMap().get("category_id");
		softAssert.assertEquals(actualProductCategory_id, expectedProductCategory_id, "category ids not matching");

		softAssert.assertAll();
	}

}
