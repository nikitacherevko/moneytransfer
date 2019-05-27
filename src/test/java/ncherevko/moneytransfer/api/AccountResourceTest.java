package ncherevko.moneytransfer.api;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import ncherevko.moneytransfer.model.Account;
import org.apache.groovy.util.Maps;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

public class AccountResourceTest {

    @Test
    public void shouldTransferMoneySuccessfully() throws Exception {
        Account source = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(39.21));
        Account target = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(22.82));
        ObjectMapper mapper = new ObjectMapper();

        given()
                .body(mapper.writeValueAsString(source))
                .contentType(ContentType.JSON)
                .when()
                .post("http://localhost:8080/api/accounts")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(target))
                .when()
                .post("http://localhost:8080/api/accounts")
                .then()
                .statusCode(201);

        BigDecimal transferAmount = BigDecimal.valueOf(3.2);
        given()
                .when()
                .queryParams(Maps.of(
                        "receiver", target.getName(),
                        "amount", transferAmount))
                .pathParam("name", source.getName())
                .patch("http://localhost:8080/api/accounts/{name}/transfer")
                .then()
                .statusCode(200);

        Account[] accounts = given()
                .when()
                .get("http://localhost:8080/api/accounts")
                .then()
                .extract()
                .body()
                .as(Account[].class);

        Account sourceResult = Stream.of(accounts)
                .filter(item -> item.getName().equals(source.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to find source account"));

        assertThat(sourceResult.getBalance(), is(source.getBalance().subtract(transferAmount)));

        Account destinationResult = Stream.of(accounts)
                .filter(item -> item.getName().equals(target.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to find target account"));

        assertThat(destinationResult.getBalance(), is(target.getBalance().add(transferAmount)));
    }
}
