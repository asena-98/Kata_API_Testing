package com.booking.stepdefinitions;

import com.booking.api.AuthApi;

import com.booking.api.BookingApi;

import io.cucumber.java.en.Given;

import io.cucumber.java.en.Then;

import io.cucumber.java.en.When;

import io.restassured.response.Response;

import java.io.IOException;

import java.nio.file.Files;

import java.nio.file.Path;

import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.Matchers.notNullValue;

public class BookingSteps {

    private final BookingApi bookingApi = new BookingApi();

    private final AuthApi authApi = new AuthApi();

    private Response response;

    private String token;

    private int bookingId;

    @When("I check the booking API health")

    public void iCheckTheBookingApiHealth() {

        response = bookingApi.healthCheck();

    }

    @Then("the API status should be up")

    public void theApiStatusShouldBeUp() {

        response.then()

                .statusCode(200)

                .body("status", equalTo("UP"));

    }

    @When("I create a valid booking")

    public void iCreateAValidBooking() throws IOException {

        String bookingJson = Files
                .readString(Path.of("src/test/resources/testdata/bookings/valid-booking.json"));

        long daysToAdd = 30 + (System.currentTimeMillis() % 1000);

        LocalDate checkin = LocalDate.now().plusDays(daysToAdd);

        LocalDate checkout = checkin.plusDays(2);

        bookingJson = bookingJson

                .replace("${checkin}", checkin.toString())

                .replace("${checkout}", checkout.toString());

        response = bookingApi.createBooking(bookingJson);

    }

    @Then("the booking should be created successfully")

    public void theBookingShouldBeCreatedSuccessfully() {

        response.then()

                .statusCode(201)

                .body("bookingid", notNullValue());

    }

    @Given("I am logged in as admin")

    public void iAmLoggedInAsAdmin() {

        token = authApi.getAdminToken();

    }

    @Given("a valid booking exists")

    public void aValidBookingExists() throws IOException {

        String bookingJson = Files
                .readString(Path.of("src/test/resources/testdata/bookings/valid-booking.json"));

        long daysToAdd = 30 + (System.currentTimeMillis() % 1000);

        LocalDate checkin = LocalDate.now().plusDays(daysToAdd);

        LocalDate checkout = checkin.plusDays(2);

        bookingJson = bookingJson

                .replace("${checkin}", checkin.toString())

                .replace("${checkout}", checkout.toString());

        response = bookingApi.createBooking(bookingJson);

        response.then()

                .statusCode(201)

                .body("bookingid", notNullValue());

        Integer createdBookingId = response.jsonPath().get("bookingid");

        bookingId = createdBookingId;

    }

    @When("I retrieve the booking by ID")

    public void iRetrieveTheBookingById() {

        response = bookingApi.getBookingById(bookingId, token);

    }

    @Then("the booking details should be returned")

    public void theBookingDetailsShouldBeReturned() {

        response.then()

                .statusCode(200)

                .body("firstname", notNullValue())

                .body("lastname", notNullValue())

                .body("bookingdates.checkin", notNullValue())

                .body("bookingdates.checkout", notNullValue());

    }

    @When("I update the booking")

    public void iUpdateTheBooking() throws IOException {

        String bookingJson = Files
                .readString(Path.of("src/test/resources/testdata/bookings/updated-booking.json"));

        long daysToAdd = 40 + (System.currentTimeMillis() % 1000);

        LocalDate checkin = LocalDate.now().plusDays(daysToAdd);

        LocalDate checkout = checkin.plusDays(2);

        bookingJson = bookingJson

                .replace("${checkin}", checkin.toString())

                .replace("${checkout}", checkout.toString());


        response = bookingApi.updateBooking(bookingId, token, bookingJson);

    }

    @Then("the booking should be updated successfully")

    public void theBookingShouldBeUpdatedSuccessfully() {

        response.then()

                .statusCode(200)

                .body("booking.firstname", equalTo("Updated"))

                .body("booking.lastname", equalTo("Booking"))

                .body("booking.depositpaid", equalTo(false));

    }
}

