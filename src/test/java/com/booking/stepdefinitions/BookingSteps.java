package com.booking.stepdefinitions;

import com.booking.api.AuthApi;
import com.booking.api.BookingApi;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import java.util.concurrent.ThreadLocalRandom;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BookingSteps {

    private final BookingApi bookingApi = new BookingApi();
    private final AuthApi authApi = new AuthApi();
    private Response response;
    private String token;
    private int bookingId;
    private String existingBookingJson;
    private String createdBookingJson;
    private String updatedBookingJson;

    @When("I check the booking API health")
    public void iCheckTheBookingApiHealth() {
        response = bookingApi.healthCheck();
    }

    @Then("the API status should be up")
    public void theApiStatusShouldBeUp() {
        response.then().statusCode(200).body("status", equalTo("UP"));
    }

    @When("I create a valid booking")
    public void iCreateAValidBooking() throws IOException {
        String bookingJson = Files
                .readString(Path.of("src/test/resources/testdata/bookings/valid-booking.json"));
        int daysToAdd = ThreadLocalRandom.current().nextInt(365, 10000);
        LocalDate checkin = LocalDate.now().plusDays(daysToAdd);
        LocalDate checkout = checkin.plusDays(2);
        bookingJson = bookingJson.replace("${checkin}", checkin.toString()).replace("${checkout}",
                checkout.toString());
        createdBookingJson = bookingJson;
        response = bookingApi.createBooking(bookingJson);
    }

    @Then("the booking should be created successfully")
    public void theBookingShouldBeCreatedSuccessfully() {
        JsonPath expectedBooking = JsonPath.from(createdBookingJson);

        response.then().statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response-schema.json"))
                .body("bookingid", notNullValue())
                .body("roomid", equalTo(expectedBooking.getInt("roomid")))
                .body("firstname", equalTo(expectedBooking.getString("firstname")))
                .body("lastname", equalTo(expectedBooking.getString("lastname")))
                .body("depositpaid", equalTo(expectedBooking.getBoolean("depositpaid")))
                .body("bookingdates.checkin",
                        equalTo(expectedBooking.getString("bookingdates.checkin")))
                .body("bookingdates.checkout",
                        equalTo(expectedBooking.getString("bookingdates.checkout")));
    }

    @Given("I am logged in as admin")
    public void iAmLoggedInAsAdmin() {
        token = authApi.getAdminToken();
    }

    @Given("a valid booking exists")
    public void aValidBookingExists() throws IOException {
        String bookingJson = Files
                .readString(Path.of("src/test/resources/testdata/bookings/valid-booking.json"));
        int daysToAdd = ThreadLocalRandom.current().nextInt(365, 10000);
        LocalDate checkin = LocalDate.now().plusDays(daysToAdd);
        LocalDate checkout = checkin.plusDays(2);
        bookingJson = bookingJson.replace("${checkin}", checkin.toString()).replace("${checkout}",
                checkout.toString());
        existingBookingJson = bookingJson;
        response = bookingApi.createBooking(bookingJson);
        response.then().statusCode(201).body("bookingid", notNullValue());
        Integer createdBookingId = response.jsonPath().get("bookingid");
        bookingId = createdBookingId;
    }

    @When("I retrieve the booking by ID")
    public void iRetrieveTheBookingById() {
        response = bookingApi.getBookingById(bookingId, token);
    }

    @Then("the booking details should be returned")
    public void theBookingDetailsShouldBeReturned() {
        JsonPath expectedBooking = JsonPath.from(existingBookingJson);

        response.then().statusCode(200).body("roomid", equalTo(expectedBooking.getInt("roomid")))
                .body("firstname", equalTo(expectedBooking.getString("firstname")))
                .body("lastname", equalTo(expectedBooking.getString("lastname")))
                .body("depositpaid", equalTo(expectedBooking.getBoolean("depositpaid")))
                .body("bookingdates.checkin",
                        equalTo(expectedBooking.getString("bookingdates.checkin")))
                .body("bookingdates.checkout",
                        equalTo(expectedBooking.getString("bookingdates.checkout")));
    }

    @When("I update the booking")
    public void iUpdateTheBooking() throws IOException {
        String bookingJson = Files
                .readString(Path.of("src/test/resources/testdata/bookings/updated-booking.json"));
        int daysToAdd = ThreadLocalRandom.current().nextInt(365, 10000);
        LocalDate checkin = LocalDate.now().plusDays(daysToAdd);
        LocalDate checkout = checkin.plusDays(2);
        bookingJson = bookingJson.replace("${checkin}", checkin.toString()).replace("${checkout}",
                checkout.toString());
        updatedBookingJson = bookingJson;
        response = bookingApi.updateBooking(bookingId, token, bookingJson);
    }

    @Then("the booking should be updated successfully")
    public void theBookingShouldBeUpdatedSuccessfully() {
        JsonPath expectedBooking = JsonPath.from(updatedBookingJson);

        response.then().statusCode(200).body("bookingid", equalTo(bookingId))
                .body("booking.roomid", equalTo(expectedBooking.getInt("roomid")))
                .body("booking.firstname", equalTo(expectedBooking.getString("firstname")))
                .body("booking.lastname", equalTo(expectedBooking.getString("lastname")))
                .body("booking.depositpaid", equalTo(expectedBooking.getBoolean("depositpaid")))
                .body("booking.bookingdates.checkin",
                        equalTo(expectedBooking.getString("bookingdates.checkin")))
                .body("booking.bookingdates.checkout",
                        equalTo(expectedBooking.getString("bookingdates.checkout")));
    }

    @When("I delete the booking")
    public void iDeleteTheBooking() {
        response = bookingApi.deleteBooking(bookingId, token);
    }

    @Then("the booking should be deleted successfully")
    public void theBookingShouldBeDeletedSuccessfully() {
        response.then().statusCode(202);
    }

    @When("I create another booking with the same room and date range")
    public void iCreateAnotherBookingWithTheSameRoomAndDateRange() {
        response = bookingApi.createBooking(existingBookingJson);
    }

    @Then("the duplicate booking should not be created")
    public void theDuplicateBookingShouldNotBeCreated() {
        response.then().statusCode(409);
    }

    @When("I request the booking details without an authentication token")
    public void iRequestTheBookingDetailsWithoutAnAuthenticationToken() {
        response = bookingApi.getBookingByIdWithoutAuthentication(bookingId);
    }

    @Then("the booking details should not be returned")
    public void theBookingDetailsShouldNotBeReturned() {
        response.then().statusCode(403);
    }
}
