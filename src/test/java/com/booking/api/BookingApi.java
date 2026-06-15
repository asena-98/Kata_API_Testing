package com.booking.api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookingApi {

    private static final String BASE_URL = "https://automationintesting.online";

    private static final String BOOKING_PATH = "/api/booking";

    public Response healthCheck() {
        return given().baseUri(BASE_URL).when().get(BOOKING_PATH + "/actuator/health");

    }

    public Response createBooking(String bookingJson) {
        return given().baseUri(BASE_URL).header("Content-Type", "application/json")
                .header("Accept", "application/json").body(bookingJson).when().post(BOOKING_PATH);

    }

    public Response getBookingById(int bookingId, String token) {
        return given().baseUri(BASE_URL).header("Accept", "application/json").cookie("token", token)
                .when().get(BOOKING_PATH + "/" + bookingId);

    }

    public Response updateBooking(int bookingId, String token, String bookingJson) {
        return given().baseUri(BASE_URL).header("Content-Type", "application/json")
                .header("Accept", "application/json").cookie("token", token).body(bookingJson)
                .when().put(BOOKING_PATH + "/" + bookingId);

    }

    public Response deleteBooking(int bookingId, String token) {
        return given().baseUri(BASE_URL).cookie("token", token).when()
                .delete(BOOKING_PATH + "/" + bookingId);

    }

    public Response getBookingByIdWithoutAuthentication(int bookingId) {
        return given().baseUri(BASE_URL).header("Accept", "application/json").when()
                .get(BOOKING_PATH + "/" + bookingId);
    }
}
