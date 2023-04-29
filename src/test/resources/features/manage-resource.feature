Feature: Manage resource

  Background:
    Given the current time is "2023-02-03T12:34:56.123"
    And there is no resource request exist
    And there is no USER exists

  Scenario: query USER resource requests
    When I query all USER resource requests
    Then the resource response is an empty array

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

  Scenario: should able to cancel USER resource request
    And I create a PENDING_APPROVAL USER resource request "1" in DB with content
    """
    {
      "name": "Peter",
      "age": 18
    }
    """
    When the current time is "2023-02-03T23:00:00.000"
    And I cancel the USER resource request "1"
    Then the resource request is failed with http status code 200
    And the query USER request response by request id "1" should contain the base info:
      | type        | USER                    |
      | id          | 1                       |
      | operation   | CREATE                  |
      | status      | CANCELLED               |
      | version     | 1                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T23:00:00.000 |

  Scenario: should have not found error when querying USER resource request which does not exists
    When I query USER resource by request id "1"
    Then the resource request is failed with http status code 404

  Scenario Outline: should have not found error when approving or cancelling USER resource request which does not exists
    When I <operation> the USER resource request "1"
    Then the resource request is failed with http status code 404
    Examples:
      | operation |
      | approve   |
      | cancel    |

  Scenario Outline: should have validation error when approving or cancelling USER resource request is not in pending approval status
    And I create a <originalRequestStatus> USER resource request "1" in DB with content
    """
    {
      "name": "Peter",
      "age": 18
    }
    """
    When I <operation> the USER resource request "1"
    Then the resource request is failed with http status code 400
    And I got the error messages:
      | cannot <operation> USER resource request with id '1' because it is in '<originalRequestStatus>' state |
    Examples:
      | operation | originalRequestStatus |
      | approve   | APPROVED              |
      | approve   | CANCELLED             |
      | cancel    | APPROVED              |
      | cancel    | CANCELLED             |

  Scenario: should have validation error when create resource with reason is not provided
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
      | missing 'age' |

  Scenario: should perform validation on approval
    And I create a PENDING_APPROVAL USER resource request "1" in DB with content
    """
    {
      "name": "Peter"
    }
    """
    When I approve the USER resource request "1"
    Then the resource request is failed with http status code 400
    And I got the error messages:
      | missing 'age' |

  Scenario: should not perform validation on cancel
    And I create a PENDING_APPROVAL USER resource request "1" in DB with content
    """
    {
      "name": "Peter"
    }
    """
    When the current time is "2023-02-03T23:00:00.000"
    And I cancel the USER resource request "1"
    Then the resource request is failed with http status code 200
    And the query USER request response by request id "1" should contain the base info:
      | type        | USER                    |
      | id          | 1                       |
      | operation   | CREATE                  |
      | status      | CANCELLED               |
      | version     | 1                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T23:00:00.000 |
