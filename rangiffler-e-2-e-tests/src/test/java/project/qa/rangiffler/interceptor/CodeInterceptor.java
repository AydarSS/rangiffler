package project.qa.rangiffler.interceptor;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import project.qa.rangiffler.extension.AddUsersExtension;

public class CodeInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    final Response response = chain.proceed(chain.request());
    if (response.isRedirect()) {
      final String location = response.header("Location");
      if (location.contains("code=")) {
        final String code = StringUtils.substringAfter(location, "code=");
      }
    } else if (!response.headers("Set-Cookie").isEmpty()) {
      for (String header : response.headers("Set-Cookie")) {
        String[] headerPath = header.split(";", 2);
        AddUsersExtension.forXsrfToken.set(headerPath[0].replace("XSRF-TOKEN=",""));
      }
    }
    return response;
  }
}
