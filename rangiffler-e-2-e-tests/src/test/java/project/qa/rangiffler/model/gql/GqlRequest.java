package project.qa.rangiffler.model.gql;

import java.util.Map;
import project.qa.rangiffler.model.FriendshipAction;

public record GqlRequest(String operationName,
                         Map<String, Object> variables,
                         String query) {

  public static GqlRequest friendShipIdentityGqlRequest(String userId, FriendshipAction action) {
    return new GqlRequest(
        "FriendshipAction",
        Map.of("input",
            Map.of("user", userId,
                "action", action.name())),
        """
            mutation FriendshipAction($input: FriendshipInput!) {
              friendship(input: $input) {
                id
                username
                friendStatus
                __typename
              }
            }
            """);
  }
}
