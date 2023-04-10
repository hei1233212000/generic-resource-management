Feature: Manage resource

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
      | type   | USER             |
      | id     | 123              |
      | status | PENDING_APPROVAL |
    And the content of the resource response should be:
      | name | Peter |
      | age  | 18    |
