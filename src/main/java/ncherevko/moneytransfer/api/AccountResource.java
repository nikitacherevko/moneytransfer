package ncherevko.moneytransfer.api;

import ncherevko.moneytransfer.api.response.TransferResponse;
import ncherevko.moneytransfer.persistance.model.Account;
import ncherevko.moneytransfer.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("accounts")
public class AccountResource {

    private final AccountService accountService = new AccountService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAccounts() {
        return accountService.getAllAccounts();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAccount(Account account) {
        accountService.addAccount(account);
        return Response.status(Status.CREATED).build();
    }

    @PATCH
    @Path("/{name}/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransferResponse addAccount(@PathParam("name") String sender, @QueryParam("receiver") String receiver,
                                       @QueryParam("amount") BigDecimal amount) {
        return accountService.transfer(sender, receiver, amount);
    }
}
