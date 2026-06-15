package com.booking.api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthApi {

    private static final String BASE_URL = "https://automationintesting.online";

    public Response login(String username, String password) {
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
        return given().baseUri(BASE_URL).header("Content-Type", "application/json").body(body)
                .when().post("/api/auth/login");
    }

    public String getAdminToken() {
        Response response = login("admin", "password");

        return response.jsonPath().getString("token");
    }
}
