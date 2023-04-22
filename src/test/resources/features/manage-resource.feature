Feature: Manage resource

  Background:
    Given the current time is "2023-02-03T12:34:56.123"

  Scenario: query USER resources
    Given there is no resource exist
    When I query all USER resources
    Then the resource response is an empty array

  Scenario: create USER resource
    Given there is no resource exist
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
    And the query USER response by request id "1" should contain the base info:
      | type        | USER                    |
      | id          | 1                       |
      | status      | PENDING_APPROVAL        |
      | reason      | This is a test          |
      | version     | 0                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T12:34:56.123 |
    And the content of the resource response should be:
      | name | Peter |
      | age  | 18    |

    When the current time is "2023-02-03T23:00:00.000"
    And I approve the create USER resource request "1"
    Then the resource request is successfully processed with http status code 200
    And the approve USER response by request id "1" should contain the base info:
      | type        | USER                    |
      | id          | 1                       |
      | status      | APPROVED                |
      | version     | 1                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T23:00:00.000 |
#    And the USER is persisted into the database with details:
#      | name | Peter |
#      | age  | 18    |

  Scenario: should have not found error when querying USER resource which does not exists
    Given there is no resource exist
    When I query USER resource by request id "1"
    Then the resource request is failed with http status code 404

  Scenario: should have not found error when approving USER resource which does not exists
    Given there is no resource exist
    When I approve the create USER resource request "1"
    Then the resource request is failed with http status code 404

  Scenario: should have validation error if reason is not provided
    Given there is no resource exist
    When I fire the create USER resource request as
    """
    {
      "content": {
        "name": "Peter",
        "age": 18
      },
      "reason": ""
    }
    """
    Then the resource request is failed with http status code 400
    And I got the error messages:
      | 'reason' must not be blank |

  Scenario: should have validation error if user age is not provided
    Given there is no resource exist
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
      | missing 'age' |

  Scenario: should have validation on approval
    Given there is no resource exist
    And I create a PENDING_APPROVAL USER resource request "1" in DB with content
    """
    {
      "name": "Peter"
    }
    """
    When I approve the create USER resource request "1"
    Then the resource request is failed with http status code 400
    And I got the error messages:
      | missing 'age' |