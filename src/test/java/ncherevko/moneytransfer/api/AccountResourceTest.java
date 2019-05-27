package ncherevko.moneytransfer.api;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import ncherevko.moneytransfer.api.response.TransferResponse;
import ncherevko.moneytransfer.persistance.model.Account;
import org.apache.groovy.util.Maps;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

public class AccountResourceTest {

    private static final Logger log = LoggerFactory.getLogger(AccountResourceTest.class);

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
        log.info("Account {} is created", source);

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(target))
                .when()
                .post("http://localhost:8080/api/accounts")
                .then()
                .statusCode(201);
        log.info("Account {} is created", target);

        BigDecimal transferAmount = BigDecimal.valueOf(3.2);
        TransferResponse transferResponse = given()
                .when()
                .queryParams(Maps.of(
                        "receiver", target.getName(),
                        "amount", transferAmount))
                .pathParam("name", source.getName())
                .patch("http://localhost:8080/api/accounts/{name}/transfer")
                .then()
                .extract()
                .body()
                .as(TransferResponse.class);
        assertTrue(transferResponse.isSuccess());
        log.info("Transfer {} from {} to {} is executed successfully",
                transferAmount, source.getName(), target.getName());

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

    @Test
    public void shouldFailIfNotEnoughMoney() throws Exception {
        Account source = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(0.01));
        Account target = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(19.82));
        ObjectMapper mapper = new ObjectMapper();

        given()
                .body(mapper.writeValueAsString(source))
                .contentType(ContentType.JSON)
                .when()
                .post("http://localhost:8080/api/accounts")
                .then()
                .statusCode(201);
        log.info("Account {} is created", source);

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(target))
                .when()
                .post("http://localhost:8080/api/accounts")
                .then()
                .statusCode(201);
        log.info("Account {} is created", target);

        BigDecimal transferAmount = BigDecimal.valueOf(0.02);
        TransferResponse transferResponse = given()
                .when()
                .queryParams(Maps.of(
                        "receiver", target.getName(),
                        "amount", transferAmount))
                .pathParam("name", source.getName())
                .patch("http://localhost:8080/api/accounts/{name}/transfer")
                .then()
                .extract()
                .body()
                .as(TransferResponse.class);
        assertFalse(transferResponse.isSuccess());
        log.info("Transfer {} from {} to {} is not executed: {}",
                transferAmount, source.getName(), target.getName(), transferResponse.getDescription());

    }

    @Test
    public void shouldFailIfTargetAccountIsNotFound() throws Exception {
        Account source = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(10));
        ObjectMapper mapper = new ObjectMapper();

        given()
                .body(mapper.writeValueAsString(source))
                .contentType(ContentType.JSON)
                .when()
                .post("http://localhost:8080/api/accounts")
                .then()
                .statusCode(201);
        log.info("Account {} is created", source);

        BigDecimal transferAmount = BigDecimal.valueOf(5);
        TransferResponse transferResponse = given()
                .when()
                .queryParams(Maps.of(
                        "receiver", UUID.randomUUID().toString(),
                        "amount", transferAmount))
                .pathParam("name", source.getName())
                .patch("http://localhost:8080/api/accounts/{name}/transfer")
                .then()
                .extract()
                .body()
                .as(TransferResponse.class);
        assertFalse(transferResponse.isSuccess());
        log.info("Transfer {} from {} to {} is not executed: {}",
                transferAmount, source.getName(), UUID.randomUUID().toString(), transferResponse.getDescription());

    }
}
