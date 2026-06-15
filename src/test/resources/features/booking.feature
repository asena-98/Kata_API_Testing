@booking
Feature: Booking API

  Scenario: Check that the booking API is running
    When I check the booking API health
    Then the API status should be up

  Scenario: Create a valid booking
    When I create a valid booking
    Then the booking should be created successfully
 
  Scenario: Retrieve a booking by ID
    Given I am logged in as admin 
    And a valid booking exists
    When I retrieve the booking by ID
    Then the booking details should be returned

  Scenario: Update an entire booking 
    Given I am logged in as admin
    And a valid booking exists
    When I update the booking
    Then the booking should be updated successfully

  Scenario: Delete an existing booking 
    Given I am logged in as admin
    And a valid booking exists
    When I delete the booking
    Then the booking should be deleted successfully

  Scenario: Prevent duplicate booking of the same room for the same dates
    Given a valid booking exists
    When I create another booking with the same room and date range
    Then the duplicate booking should not be created 

  Scenario: Prevent booking retrieval without authentication
    Given a valid booking exists
    When I request the booking details without an authentication token
    Then the booking details should not be returned
    

  