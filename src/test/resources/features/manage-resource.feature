Feature: Manage resource

  Background:
    Given the current time is "2023-02-03T12:34:56.123"

  Scenario: query USER resources
    Given there is no resource exist
    When query all USER resources
    Then the resource response is an empty array

  Scenario: create USER resource
    Given there is no resource exist
    When I fire the create USER resource request as
    """
    {
      "id": "123",
      "content": {
        "name": "Peter",
        "age": 18
      }
    }
    """
    Then the resource request is successfully processed
    And the USER response of with id "123" should contain the base info:
      | type        | USER                    |
      | id          | 123                     |
      | status      | PENDING_APPROVAL        |
      | version     | 0                       |
      | createdTime | 2023-02-03T12:34:56.123 |
      | updatedTime | 2023-02-03T12:34:56.123 |
    And the content of the resource response should be:
      | name | Peter |
      | age  | 18    |
