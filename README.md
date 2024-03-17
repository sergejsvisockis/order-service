# Order service

Create a simple application that will store information about orders and included products for a given user.
To keep it simple, the application must have 3 API endpoints:

- create a new order:
    - this endpoint should accept a list of items as a request body and header user-id in UUID format.
    - The response should contain the id of the created order. After triggering the endpoint, a new order and all items
      should be saved in the DB.
    - Item as request body should contain the following fields: unit price, amount of ordered items, item ID (UUID), and
      item name.
    - When there are 5 or more of the same items in the order, a 10% discount should be applied to the unit price of
      that product.
    - If there are more than 10 products, apply a 15% discount (instead of 10%).
    - Multiple products can have discounts applied if the minimum amount requirement is fulfilled.
- get the total price of all orders in the given date range of a given user
- get all user orders: this endpoint should show all user orders with all ordered items based on the user ID provided in
  the user-id header.

Technical requirements:

- Java 17 or higher
- Spring boot 3
- Jpa & Hibernate
- No spring security is needed
