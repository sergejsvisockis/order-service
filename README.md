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

## Scaling horizontally

### Functional requirements

Let's imagine that the marketing campaign takes place and once per quarter (3 months) the service experiences an
unusual number of orders for the whole week.

Due to the aforementioned the system just comes down periodically.

### Non-functional requirements

During the Grafana dashboard measures (let's leave it behind in the scope of this task) it was identified
that an overall system is experiencing a particularly high number of requests at about 10k per second
for both reads and writes with deviation 2-3k.

* 10k requests per second

## Scaling horizontally - the solution

Since this task is for experimentation purposes we assume that our traffic increases up to 10k requests per second,
therefore we could scale horizontally by adding more instances of the same application and the load balancing.

To run "N" number of instances of the order-service in a Docker environment run the following command:

```shell
docker-compose up --scale order-service=N
```

The actual number of instances strictly depends on the occasion and particular monitoring results therefore at times it
could be 5 while in others 10. This one depends on the load and other non-functional requirements.

Due to the fact strong consistency is one of the key non-functional requirements of this application an SQL database
is being used.
Relational databases have one drawback in the sense that these are quite challenging to scale horizontally due to
the ACID guarantees.
Besides that, such a huge number of database reads could potentially be a bottleneck due to the latency and network
bandwidth.
A connection pool has to be used as well since the database connection opening is an expensive operation.

Since an application is experiencing a high load occasionally (once in 3 months) leveraging the local cache in each
instance of the distributed application might be more applicable instead of the distributed one. For the local in-memory
cache a Caffeine cache is being used. Since there is a high load occasionally from multiple threads simultaneously
Caffeine cache provides robust capabilities to achieve a higher performance especially due to its circular buffer data
structure-based implementation.

When it comes to a high number of writes then because the database connection opening is quite expensive
operation a proper connection pooling mechanism has to be in place.
Since the high load is in place only once per quarter (3 months) there is no need for an additional batch save neither
queuing with a scheduling mechanism commonly applied in this case scenario.
At the same time if non-functional requirements do eventually change and the load increases up to the 10k requests per
second daily then perhaps it would make sense to have a batching and queuing mechanism. In that case, it would also make
sense to make a database sharding. In addition, it would also be beneficial to leverage distributed caching in that
case despite latency and accidental complexity. This particular case is pretty straightforward and the connection
pooling along with the local caching are more than enough.

There is also no place for a CQRS since the number of read and write operations is almost the same with a minor
deviation of 2-3k during the peak. Also, considering that it takes place only once in 3 months the CQRS will lead to an
accidental complexity meanwhile increasing the support cost. It would also be challenging to achieve a strong
consistency.
