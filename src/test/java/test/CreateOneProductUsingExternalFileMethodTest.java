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

public class CreateOneProductUsingExternalFileMethodTest {

	String baseURI;
	SoftAssert softAssert;
	String firstProductID;
	String createOneProductPayloadPath;
	String readOneProductID;
	
	public CreateOneProductUsingExternalFileMethodTest() {
		baseURI = "https://techfios.com/api-prod/api/product";
		softAssert = new SoftAssert();
		createOneProductPayloadPath = "src/main/java/data/createPayload.json";
	}

	 //1. method 1: sending createPayload as a external json file
	@Test(priority=1)
	public void createOneProductUsingExternalJsonFile() {

		/*
		 * create one product payload: { "name" : "Amazing Pillow 2.0", "price" : "199",
		 * "description" : "The best pillow for amazing programmers.", "category_id" :
		 * "2", "category_name": "Electronics"
		 * 
		 * }
		 * 
		 * 
		 */
		
		Response response =

				given().baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json; charset=UTF-8")
						.header("Authorization", "Bearer KKKhughghkkIIInkwlhd")// bearer token
						.body(new File(createOneProductPayloadPath)).

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

	// readOneProduct implemetation when creating One product using externalJsonFile
	@Test(priority=3)
	public void readOneProductWhenCreatingProductUsingExternalJson() {
		Response response =

				given()
						// log().all().
						.baseUri(baseURI)// gets baseURI from constructor
						.header("Content-Type", "application/json").queryParam("id", readOneProductID)
						.header("Authorization", "Bearer KKKhughghkkIIInkwlhd").// bearer token authorization as header

						when().get("/read_one.php").then()

						.extract().response();

		String responseBody = response.getBody().asString();

		System.out.println("Response Body : " + responseBody);

		JsonPath jsonPath1 = new JsonPath(responseBody);// for parsing response Body in readOneProduct response as Json
		JsonPath jsonPath2 = new JsonPath(new File(createOneProductPayloadPath));// for parsing the file and validating
																					// in json format

		// actual variable values come from readOneProduct response body
		// expected variable values come from file that we provide in createpayload file(json)

		String actualProductId = jsonPath1.getString("id");
		// product id from response body of readOneProduct should be the same as the
		// first product id
		// that we get from readAllProducts-->which is assigned to readOnePructID
		softAssert.assertEquals(actualProductId, readOneProductID, "Product ids donot match");

		String actualProductName = jsonPath1.getString("name");
		String expectedProductName = jsonPath2.getString("name");
		softAssert.assertEquals(actualProductName, expectedProductName, "Product names are not matching");

		String actualproductDescription = jsonPath1.getString("description");
		String expectedProductDescription = jsonPath2.getString("description");
		softAssert.assertEquals(actualproductDescription, expectedProductDescription,
				"Product Description not matching");

		String actualProductPrice = jsonPath1.getString("price");
		String expectedProductPrice = jsonPath2.getString("price");
		softAssert.assertEquals(actualProductPrice, expectedProductPrice, "Prices not matching");

		String actualProductCategory_id = jsonPath1.getString("category_id");
		String expectedProductCategory_id = jsonPath2.getString("category_id");
		softAssert.assertEquals(actualProductCategory_id, expectedProductCategory_id, "category ids not matching");

		softAssert.assertAll();
	}

	

}
