Feature: Manage USER

  Background:
    Given the current time is "2023-02-03T12:34:56.123"
    And there is no resource request exist
    And there is no USER exists

  Scenario: create USER
    When I fire the create USER resource request as
    """
    {
      "content": {
        "name": "Peter",
        "age": 18
      },
      "reason": "This is a test"
    }
    """
    Then the resource request is successfully processed with http status code 201
    And the query USER request response by request id "1" should contain the base info:
      | type        | USER                    |
      | id          | 1                       |
      | operation   | CREATE                  |
      | status      | PENDING_APPROVAL        |
      | reason      | This is a test          |
      | version     | 0                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T12:34:56.123 |
    And the content of the resource response should be:
      | name | Peter |
      | age  | 18    |

    When the current time is "2023-02-03T23:00:00.000"
    And I approve the USER resource request "1"
    Then the resource request is successfully processed with http status code 200
    And the approve USER request response by request id "1" should contain the base info:
      | type        | USER                    |
      | id          | 1                       |
      | status      | APPROVED                |
      | version     | 1                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T23:00:00.000 |
    And the USER "Peter" is persisted into the database with details:
      | id          | 1                       |
      | name        | Peter                   |
      | age         | 18                      |
      | version     | 0                       |
      | createdTime | 2023-02-03T23:00:00.000 |
      | updatedTime | 2023-02-03T23:00:00.000 |

  Scenario: should have validation error when create USER resource with user age is not provided
    When I fire the create USER resource request as
    """
    {
      "content": {
        "name": "Peter"
      },
      "reason": "validation test"
    }
    """
    Then the resource request is failed with http status code 400
    And I got the error messages:
      | 'age' must not be null |
