Feature: query resources

  Background:
    Given there is no ACCOUNT exists
    And the below ACCOUNTs are already exist:
      | id                                   | holder | amount | createdBy | createdTime             | updatedBy | updatedTime             |
      | 00000000-0000-0000-0000-000000000001 | Peter  | 1000   | tester1   | 2023-02-01T00:00:00.000 | approver1 | 2023-02-02T00:00:00.000 |
      | 00000000-0000-0000-0000-000000000002 | Paul   | 2000   | tester2   | 2023-02-03T00:00:00.000 | approver1 | 2023-02-04T00:00:00.000 |
      | 00000000-0000-0000-0000-000000000003 | Mary   | 3000   | tester3   | 2023-02-05T00:00:00.000 | approver2 | 2023-02-06T00:00:00.000 |
      | 00000000-0000-0000-0000-000000000004 | David  | 4000   | tester4   | 2023-02-07T00:00:00.000 | approver2 | 2023-02-08T00:00:00.000 |

  Scenario: should able to query ACCOUNTs without any query parameters
    When I query ACCOUNTs by using query parameters ""
    Then I would get the ACCOUNTs with id [00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002, 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004]
    And I would get the ACCOUNTs with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 4  |
      | totalPages       | 1  |
      | totalElements    | 4  |

  Scenario: should able to query ACCOUNTs with ordering
    When I query ACCOUNTs by using query parameters "sort=createdTime-"
    Then I would get the ACCOUNTs with id [00000000-0000-0000-0000-000000000004, 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000002, 00000000-0000-0000-0000-000000000001]
    And I would get the ACCOUNTs with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 4  |
      | totalPages       | 1  |
      | totalElements    | 4  |

  Scenario Outline: should able to query ACCOUNTs with different sizes
    When I query ACCOUNTs by using query parameters "size=<size>&sort=id"
    Then I would get the ACCOUNTs with id [<expectedIds>]
    And I would get the ACCOUNTs with below pagination info:
      | pageNumber       | 0            |
      | pageSize         | <size>       |
      | numberOfElements | <size>       |
      | totalPages       | <totalPages> |
      | totalElements    | 4            |
    Examples:
      | size | totalPages | expectedIds                                                                                                                                            |
      | 1    | 4          | 00000000-0000-0000-0000-000000000001                                                                                                                   |
      | 2    | 2          | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002                                                                             |
      | 3    | 2          | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002, 00000000-0000-0000-0000-000000000003                                       |
      | 4    | 1          | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002, 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004 |

  Scenario Outline: should able to query ACCOUNTs with different page
    When I query ACCOUNTs by using query parameters "size=2&page=<page>&sort=id"
    Then I would get the ACCOUNTs with id [<expectedIds>]
    And I would get the ACCOUNTs with below pagination info:
      | pageNumber       | <page> |
      | pageSize         | 2      |
      | numberOfElements | 2      |
      | totalPages       | 2      |
      | totalElements    | 4      |
    Examples:
      | page | expectedIds                                                                |
      | 0    | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002 |
      | 1    | 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004 |

  Scenario Outline: should able to query ACCOUNTs with different search criteria
    When I query ACCOUNTs by using query parameters "<query>"
    Then I would get the ACCOUNTs with id [<expectedIds>]
    Examples:
      | query                              | expectedIds                                                                                                      |
      | eq(updatedBy,approver1)            | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002                                       |
      | ne(updatedBy,approver1)            | 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004                                       |
      | gt(amount,2000)                    | 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004                                       |
      | ge(amount,2000)                    | 00000000-0000-0000-0000-000000000002, 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004 |
      | lt(amount,2000)                    | 00000000-0000-0000-0000-000000000001                                                                             |
      | le(amount,2000)                    | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002                                       |
      | like(holder,P*)                    | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002                                       |
      | ilike(holder,P*)                   | 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004                                       |
      | in(createdBy, (tester1, tester2))  | 00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002                                       |
      | out(createdBy, (tester1, tester2)) | 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004                                       |

  Scenario: should able to query ACCOUNTs by multiple criteria
    When I query ACCOUNTs by using query parameters "eq(updatedBy,approver2)&like(holder,*ar*)"
    Then I would get the ACCOUNTs with id [00000000-0000-0000-0000-000000000003]
    And I would get the ACCOUNTs with below pagination info:
      | pageNumber       | 0  |
      | pageSize         | 50 |
      | numberOfElements | 1  |
      | totalPages       | 1  |
      | totalElements    | 1  |

  Scenario Outline: should ignore the unknown field
    When I query ACCOUNTs by using query parameters "<query>"
    Then I would get the ACCOUNTs with id [00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002, 00000000-0000-0000-0000-000000000003, 00000000-0000-0000-0000-000000000004]
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