Feature: Manage ACCOUNT

  Background:
    Given the current time is "2023-02-03T12:34:56.123"
    And there is no resource request exist
    And there is no ACCOUNT exists

  Scenario: create ACCOUNT
    When I fire the create ACCOUNT resource request as
    """
    {
      "content": {
        "id": "59e5ec44-799a-44ad-b202-27a01a1b660f",
        "holder": "Peter Chan",
        "amount": "1000000"
      },
      "reason": "create new account"
    }
    """
    Then the resource request is successfully processed with http status code 201
    And the query ACCOUNT request response by request id "1" should contain the base info:
      | type        | ACCOUNT                 |
      | id          | 1                       |
      | operation   | CREATE                  |
      | status      | PENDING_APPROVAL        |
      | reason      | create new account      |
      | version     | 0                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T12:34:56.123 |
    And the content of the resource response should be:
      | id     | 59e5ec44-799a-44ad-b202-27a01a1b660f |
      | holder | Peter Chan                           |
      | amount | 1000000                              |

    When the current time is "2023-02-03T23:00:00.000"
    And I approve the ACCOUNT resource request "1"
    Then the resource request is successfully processed with http status code 200
    And the approve ACCOUNT request response by request id "1" should contain the base info:
      | type        | ACCOUNT                 |
      | id          | 1                       |
      | status      | APPROVED                |
      | version     | 1                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T23:00:00.000 |
    And the ACCOUNT "59e5ec44-799a-44ad-b202-27a01a1b660f" is persisted into the database with details:
      | id          | 59e5ec44-799a-44ad-b202-27a01a1b660f |
      | holder      | Peter Chan                           |
      | amount      | 1E+6                                 |
      | version     | 0                                    |
      | createdTime | 2023-02-03T23:00:00.000              |
      | updatedTime | 2023-02-03T23:00:00.000              |

  Scenario: should have validation error when create ACCOUNT resource without id
    When I fire the create ACCOUNT resource request as
    """
    {
      "content": {
        "holder": "Peter Wong",
        "amount": "1000"
      },
      "reason": "validation test"
    }
    """
    Then the resource request is failed with http status code 400
    And I got the error messages:
      | 'id' must not be null |
