# Takehome Task

### How to run:

First run `./mvnw clean install` to build the project.

1. From the terminal - open a terminal at the root of the project and run `./mvnw spring-boot:run`
2. From IntelliJ - Load the project and run a regular spring boot run configuration

No additional requirement are needed to run the project. 

The project uses H2 Database so a database setup is not required to run the project.

After the project is started it's accessible on port `8080`

### Test the application: 

After running the application you can access the OpenAPI docs at `http://localhost:8080/api/v1/swagger-ui/index.html`

When accessing the OpenAPI docs, you will need to authenticate to use the rest of the endpoints. Authentication is done by creating a JWT token from the `/auth/token` endpoint. After you create the token you must put it in the `Authorize` section at the top or any other endpoint. When you authenticate you can use the other endpoints.

### Available Users:

Admin `{username: "Admin", password: "thegame"}` This user can use all endpoints.

Regular `{username: "Regular", password: "thegame"}` This user can only make bookings, but can't create Hotels or add Rooms to them.