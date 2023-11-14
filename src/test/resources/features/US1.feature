@wip
Feature: List of retrieved users

  US-1: Feature: As a librarian, I want to retrieve all users from the library2.cydeo.com API endpoint so that I can display them in my application.

Scenario:
  Given User logged in as "Librarian"
  When Sending request to API to retrieve all users bio
  And Sending query to DB to get expected users bio
  Then Verifying match of expected and actual bios