package project.qa.rangiffler.ex;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class DataFetcherExceptionHandler extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    if (ex instanceof ForbiddedToChangeResource) {
      return  GraphqlErrorBuilder
          .newError()
          .message(ex.getMessage())
          .errorType(ErrorType.DataFetchingException)
          .build();
    } else {
      return super.resolveToSingleError(ex, env);
    }
  }
}
