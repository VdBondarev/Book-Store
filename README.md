# Hi there!!! Online book store application is welcoming you!

![1GIF](book_store.gif)

# Online Book Store project API README

## Introduction

Welcome to online book store made using Spring Boot. This one is created to provide you with a cool and memorable experience and to fall in love with books.

## Inspiration

This application is inspired by author’s love to reading books and attempting to grow number of reading people in the world, because reading is great, isn't it?

## Technologies used

- **Stripe API:** An API that lets you create payments and pay for them easily.
- **Telegram API:** Using this, author created a telegram bot for sending notification about such events in the application as: creation of a new book, successful payments etc.
- **Spring Boot (v3.2.2):** A super-powerful framework for creating Java-based applications (just like this one).
- **Spring Security:** Ensures application security with features such as authentication and authorization.
- **JWT (JSON Web Token):** Ensures secure user authentication.
- **Spring Data JPA:** Simplifies the data access layer and interactions with the database.
- **Swagger (springdoc-openapi):** Eases understanding and interaction with endpoints for other developers.
- **MapStruct (v1.5.5.Final):** Simplifies the implementation of mappings between Java bean types.
- **Liquibase:** A powerful way to ensure database-independence for project and database schema changes and control.
- **Docker:** A powerful tool for letting other developers use this application.

## Project structure

This Spring Boot application follows the most common structure with such **main layers** as:
- repository (for working with database).
- service (for business logic implementing).
- controller (for accepting client's requests and getting responses to them).

Also, it has other **important layers** such as:
- mapper (for converting models for different purposes).
- security (for letting users authorize and be secured while interacting with the application).
- exception (application's custom exceptions for better understanding the problems you may face).
- dto (for managing sensitive info about models and better representation of it).
- config (main security config, config for mappers, config for telegram bot and open API config).

## Key features

- **User authorization:** Secure user authorization using JWT for enhanced security.
- **User login:** Authentication by login and password for generating JWT token.
- **API Documentation:** Using Swagger to generate clear and interactive API documentation.

## Setup Instructions

To set up and run the project locally, follow these steps:

1. Clone the repository.
2. Ensure you have Java 21 installed.
3. Ensure you have Maven installed.
4. Ensure you have Docker installed.
5. Create the database configuration in the `.env` file. [take a look at an example in this file](env.sample)
6. Build the project using Maven: `mvn clean package` (it will create required jar-archive).
7. Build the image using Docker: `docker-compose build`.
8. Run the application using Docker: `docker-compose up` (send requests to port pointed in your .env file as SPRING_LOCAL_PORT).

## Roles explanation

- There are only 2 roles of users available: **user role and admin role**.
- Users (even those not authenticated) have access to such endpoints as searching books, getting a book by id and similar.
- But user doesn't get to delete books from database or update them. **(Remember it)**.
- To get the full comprehension of access to endpoints, see the swagger documentation above an every endpoint.

## Users managing

- There is 1 user added to a database with help of liquibase.
- This user is already an admin.
- Credential: email: admin@example.com, password: 1234567890.
- Using this admin credentials you can update other roles of other users to admins or users.

## For endpoints understanding

- First of all: see descriptions (@Operation annotation) on each endpoint;
- Second of all: [watch a video with my explanation of the endpoints](https://www.loom.com/share/a3d0bf0eaf044a2a90cc49e1a262e8ff?sid=fc1b1109-3e4a-4648-b425-9b67be1bb367).
- **Remember**: use JWT token generated by login endpoint for every request  (except for login and register).
- When signing-up, use nothing but required params.
- When authenticating (login endpoint), pass as params your login and password (you should already be signed-up).
- Also remember what you can do and what you are not allowed to do as a simple user (not an admin).