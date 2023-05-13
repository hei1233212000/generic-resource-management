Feature: query resources

  Background:
    Given there is no USER exists
    And the below USERs are already exist:
      | id | name  | age | createdBy | createdTime             | updatedBy | updatedTime             |
      | 1  | Peter | 18  | tester1   | 2023-02-01T00:00:00.000 | approver1 | 2023-02-02T00:00:00.000 |
      | 2  | Paul  | 22  | tester2   | 2023-02-03T00:00:00.000 | approver1 | 2023-02-04T00:00:00.000 |
      | 3  | Mary  | 32  | tester3   | 2023-02-05T00:00:00.000 | approver2 | 2023-02-06T00:00:00.000 |
      | 4  | David | 40  | tester4   | 2023-02-07T00:00:00.000 | approver2 | 2023-02-08T00:00:00.000 |

  Scenario: should able to query USERs without any query parameters
    When I query USERs by using query parameters ""
    Then I would get the USERs with id [1,2,3,4]
    And I would get the USERs with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 4  |
      | totalPages       | 1  |
      | totalElements    | 4  |

  Scenario: should able to query USERs with ordering
    When I query USERs by using query parameters "sort=createdTime-"
    Then I would get the USERs with id [4,3,2,1]
    And I would get the USERs with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 4  |
      | totalPages       | 1  |
      | totalElements    | 4  |

  Scenario Outline: should able to query USERs with different sizes
    When I query USERs by using query parameters "size=<size>&sort=id"
    Then I would get the USERs with id [<expectedIds>]
    And I would get the USERs with below pagination info:
      | pageNumber       | 0            |
      | pageSize         | <size>       |
      | numberOfElements | <size>       |
      | totalPages       | <totalPages> |
      | totalElements    | 4            |
    Examples:
      | size | totalPages | expectedIds |
      | 1    | 4          | 1           |
      | 2    | 2          | 1, 2        |
      | 3    | 2          | 1, 2, 3     |
      | 4    | 1          | 1, 2, 3, 4  |

  Scenario Outline: should able to query USERs with different page
    When I query USERs by using query parameters "size=2&page=<page>&sort=id"
    Then I would get the USERs with id [<expectedIds>]
    And I would get the USERs with below pagination info:
      | pageNumber       | <page> |
      | pageSize         | 2      |
      | numberOfElements | 2      |
      | totalPages       | 2      |
      | totalElements    | 4      |
    Examples:
      | page | expectedIds |
      | 0    | 1, 2        |
      | 1    | 3, 4        |

  Scenario Outline: should able to query USERs with different search criteria
    When I query USERs by using query parameters "<query>"
    Then I would get the USERs with id [<expectedIds>]
    Examples:
      | query                              | expectedIds |
      | eq(updatedBy,approver1)            | 1, 2        |
      | ne(updatedBy,approver1)            | 3, 4        |
      | gt(id,2)                           | 3, 4        |
      | ge(id,2)                           | 2, 3, 4     |
      | lt(id,2)                           | 1           |
      | le(id,2)                           | 1, 2        |
      | like(name,P*)                      | 1, 2        |
      | ilike(name,P*)                     | 3, 4        |
      | in(createdBy, (tester1, tester2))  | 1, 2        |
      | out(createdBy, (tester1, tester2)) | 3, 4        |

  Scenario: should able to query USERs by multiple criteria
    When I query USERs by using query parameters "eq(updatedBy,approver2)&like(name,*ar*)"
    Then I would get the USERs with id [3]
    And I would get the USERs with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 1  |
      | totalPages       | 1  |
      | totalElements    | 1  |

  Scenario Outline: should ignore the unknown field
    When I query USERs by using query parameters "<query>"
    Then I would get the USERs with id [1, 2, 3, 4]
    Examples:
      | query                            |
      | eq(unknown,approver1)            |
      | ne(unknown,approver1)            |
      | gt(unknown,2)                    |
      | ge(unknown,2)                    |
      | lt(unknown,2)                    |
      | le(unknown,2)                    |
      | like(unknown,feature*)           |
      | ilike(unknown,feature*)          |
      | in(unknown, (tester1, tester2))  |
      | out(unknown, (tester1, tester2)) |