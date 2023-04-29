Feature: Manage resource

  Background:
    Given the current time is "2023-02-03T12:34:56.123"
    And there is no resource request exist
    And there is no USER exists

  Scenario: query empty resource requests
    When I query all USER resource requests
    Then the resource response is an empty array

  Scenario: should able to cancel resource request
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

  Scenario: should have not found error when querying resource request which does not exists
    When I query USER resource by request id "1"
    Then the resource request is failed with http status code 404

  Scenario Outline: should have not found error when approving or cancelling resource request which does not exists
    When I <operation> the USER resource request "1"
    Then the resource request is failed with http status code 404
    Examples:
      | operation |
      | approve   |
      | cancel    |

  Scenario Outline: should have validation error when approving or cancelling resource request is not in pending approval status
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
      | 'age' must not be null |

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
