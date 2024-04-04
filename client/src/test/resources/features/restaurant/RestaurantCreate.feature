Feature: Restaurant Create
  As a user
  I want to create new restaurant
  So that I can pass data to endpoint

  Scenario: Valid restaurant creation
    Given the system is ready to create a new restaurant
    When a request is made to create a restaurant with valid details
    Then the system should respond with a success status code
    And the new restaurant should be persisted in the database

  Scenario: Attempt to create a restaurant with missing information
    Given the system is ready to create a new restaurant
    When a request is made to create a restaurant with missing required information
    Then the system should respond with a validation error status code
    And the response should indicate the missing information

  Scenario: Attempt to create a restaurant with invalid information
    Given the system is ready to create a new restaurant
    When a request is made to create a restaurant with invalid details
    Then the system should respond with a validation error status code
    And the response should indicate the specific validation errors

  Scenario: Attempt to create a restaurant with duplicate name
    Given the system is ready to create a new restaurant
    And a restaurant with the name "COMIDA BOA" already exists in the system
    When a request is made to create a restaurant with the same name "COMIDA BOA"
    Then the system should respond with a conflict status code
    And the response should indicate that a restaurant with the same name already exists
