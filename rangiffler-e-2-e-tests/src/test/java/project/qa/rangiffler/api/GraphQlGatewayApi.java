package project.qa.rangiffler.api;

import org.apache.hc.client5.http.async.AsyncExecChainHandler;
import project.qa.rangiffler.model.gql.GqlRequest;
import project.qa.rangiffler.model.gql.GqlUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GraphQlGatewayApi {

  @POST("/graphql")
  Call<GqlUser> currentUser(@Header("Authorization") String bearerToken,
      @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUser> getLinkedPersons(@Header("Authorization") String bearerToken,
      @Body GqlRequest gqlRequest);


  @POST("/graphql")
  Call<GqlUser> updateUser(@Header("Authorization") String bearerToken,
      @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUser> identityFriendship(@Header("Authorization") String bearerToken,
      @Body GqlRequest gqlRequest);
}
