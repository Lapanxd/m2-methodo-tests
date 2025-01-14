Feature: Managing book creation and retrieval
  Scenario: Adding two books and verifying their retrieval
    When the user adds the book "1984" authored by "George Orwell"
    And the user adds the book "To Kill a Mockingbird" authored by "Harper Lee"
    And the user retrieves all books
    Then the list should include the following books in the same sequence
      | name                  | author      |
      | 1984                  | George Orwell |
      | To Kill a Mockingbird | Harper Lee  |
