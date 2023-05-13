Feature: query resource requests

  Background:
    Given there is no resource request exist
    And the below resource requests are already exist:
      | type | id | content | reason                 | operation | status           | createdBy | createdTime             | updatedBy | updatedTime             |
      | USER | 1  | {}      | feature: create User A | CREATE    | APPROVED         | tester1   | 2023-02-01T00:00:00.000 | approver1 | 2023-02-02T00:00:00.000 |
      | USER | 2  | {}      | feature: create User B | CREATE    | PENDING_APPROVAL | tester2   | 2023-02-03T00:00:00.000 | approver1 | 2023-02-04T00:00:00.000 |
      | USER | 3  | {}      | bugfix: fix User A     | UPDATE    | APPROVED         | tester3   | 2023-02-05T00:00:00.000 | approver2 | 2023-02-06T00:00:00.000 |
      | USER | 4  | {}      | bugfix: fix User B     | UPDATE    | PENDING_APPROVAL | tester4   | 2023-02-07T00:00:00.000 | approver2 | 2023-02-08T00:00:00.000 |

  Scenario: should able to query resource requests without any query parameters
    When I query USER resource requests by using query parameters ""
    Then I would get the USER resource requests with id [1,2,3,4]
    And I would get the resource requests with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 4  |
      | totalPages       | 1  |
      | totalElements    | 4  |

  Scenario: should able to query resource requests with ordering
    When I query USER resource requests by using query parameters "sort=createdTime-"
    Then I would get the USER resource requests with id [4,3,2,1]
    And I would get the resource requests with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 4  |
      | totalPages       | 1  |
      | totalElements    | 4  |

  Scenario Outline: should able to query resource requests with different sizes
    When I query USER resource requests by using query parameters "size=<size>&sort=id"
    Then I would get the USER resource requests with id [<expectedIds>]
    And I would get the resource requests with below pagination info:
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

  Scenario Outline: should able to query resource requests with different page
    When I query USER resource requests by using query parameters "size=2&page=<page>&sort=id"
    Then I would get the USER resource requests with id [<expectedIds>]
    And I would get the resource requests with below pagination info:
      | pageNumber       | <page> |
      | pageSize         | 2      |
      | numberOfElements | 2      |
      | totalPages       | 2      |
      | totalElements    | 4      |
    Examples:
      | page | expectedIds |
      | 0    | 1, 2        |
      | 1    | 3, 4        |

  Scenario Outline: should able to query resource requests with different search criteria
    When I query USER resource requests by using query parameters "<query>"
    Then I would get the USER resource requests with id [<expectedIds>]
    Examples:
      | query                              | expectedIds |
      | eq(updatedBy,approver1)            | 1, 2        |
      | ne(updatedBy,approver1)            | 3, 4        |
      | gt(id,2)                           | 3, 4        |
      | ge(id,2)                           | 2, 3, 4     |
      | lt(id,2)                           | 1           |
      | le(id,2)                           | 1, 2        |
      | like(reason,feature*)              | 1, 2        |
      | ilike(reason,feature*)             | 3, 4        |
      | in(createdBy, (tester1, tester2))  | 1, 2        |
      | out(createdBy, (tester1, tester2)) | 3, 4        |

  Scenario: should able to query resource requests by multiple criteria
    When I query USER resource requests by using query parameters "eq(updatedBy,approver1)&like(reason,*User A*)"
    Then I would get the USER resource requests with id [1]
    And I would get the resource requests with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 1  |
      | totalPages       | 1  |
      | totalElements    | 1  |

  Scenario Outline: should ignore the unknown field
    When I query USER resource requests by using query parameters "<query>"
    Then I would get the USER resource requests with id [1, 2, 3, 4]
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
