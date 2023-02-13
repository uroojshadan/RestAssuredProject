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

public class CreateOneProductUsingHashMapMethod {

	String baseURI;
	SoftAssert softAssert;
	String firstProductID;
	String readOneProductID;
	HashMap<String, String> createProductData;//passing createOneProductPayload as hashmap

	public CreateOneProductUsingHashMapMethod() {
		baseURI = "https://techfios.com/api-prod/api/product";
		softAssert = new SoftAssert();
	}

	//  method 2: sending createPayload as a hashmap
	
	public HashMap<String, String> getCreatePayloadAsHashMap() {

		createProductData = new HashMap<String, String>();

		createProductData.put("name", "Amazing Pillow 5.0");
		createProductData.put("price", "500");
		createProductData.put("description", "The best pillow for amazing programmers2.");
		createProductData.put("category_id", "2");

		return createProductData;

	}

	@Test(priority = 1)
	public void createOneProductUsingHashMap() {

		
		Response response =

				given().baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json; charset=UTF-8")
						.header("Authorization", "Bearer KKKhughghkkIIInkwlhd")// bearer token
						.body(getCreatePayloadAsHashMap()).

						/*
						 * ways to pass post payload: 1. external json file 2. pojo class 3. hashMap 4.
						 * org.Json
						 */

						when().post("/create.php").then()

						.extract().response();

		// validating statusCode
		int statusCode = response.getStatusCode();
		System.out.println("Status code : " + statusCode);
		softAssert.assertEquals(statusCode, 201, "Status codes do not match");

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
		softAssert.assertEquals(messageFromResponseBody, "Product was created.", "Messages not matching");
		softAssert.assertAll();// should be very last
		// statement after all softasserts catches all the failed assertions if any

	}

	@Test(priority = 2) // using readAllProducts to get first id
	public void readAllProducts() {

		Response response =

				given().log().all().baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json; charset=UTF=8").auth().preemptive()
						.basic("demo@techfios.com", "abc123").

						when().get("/read.php").then().extract().response();

		String responseBody = response.getBody().asString();

		System.out.println("Response Body : " + responseBody);// -->astring() prints body in single line
		// response.getBody().prettyPrint();//gets in a nice format and not in single
		// line

		JsonPath jsonPath = new JsonPath(responseBody);
		firstProductID = jsonPath.getString("records[0].id");
		System.out.println("first product id : " + firstProductID);
		readOneProductID = firstProductID;

	}

	
	// readOneProduct implemetation when creating One product using hashmap
	@Test(priority = 3)
	public void readOneProductWhenCreatingProductUsingHashMap() {
		Response response =

				given()
						// log().all().
						.baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json").queryParam("id", readOneProductID)
						.header("Authorization", "Bearer KKKhughghkkIIInkwlhd").// bearer token

						when().get("/read_one.php").then()

						.extract().response();

		String responseBody = response.getBody().asString();

		System.out.println("Response Body : " + responseBody);

		JsonPath jsonPath = new JsonPath(responseBody);

		String actualProductId = jsonPath.getString("id");
		softAssert.assertEquals(actualProductId, readOneProductID, "Product ids donot match");

		String actualProductName = jsonPath.getString("name");
		String expectedProductName = getCreatePayloadAsHashMap().get("name");
		softAssert.assertEquals(actualProductName, expectedProductName, "Product names are not matching");

		String actualproductDescription = jsonPath.getString("description");
		String expectedProductDescription = getCreatePayloadAsHashMap().get("description");
		softAssert.assertEquals(actualproductDescription, expectedProductDescription,
				"Product Description not matching");

		String actualProductPrice = jsonPath.getString("price");
		String expectedProductPrice = getCreatePayloadAsHashMap().get("price");
		softAssert.assertEquals(actualProductPrice, expectedProductPrice, "Prices not matching");

		String actualProductCategory_id = jsonPath.getString("category_id");
		String expectedProductCategory_id = getCreatePayloadAsHashMap().get("category_id");
		softAssert.assertEquals(actualProductCategory_id, expectedProductCategory_id, "category ids not matching");

		softAssert.assertAll();
	}

}
