package project.qa.rangiffler.api.aycookie;

import java.io.IOException;
import java.util.HashSet;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public enum CookieInterceptor implements Interceptor {
  INSTANCE;

  private final HashSet<String> cookies = new HashSet<>();

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    request = addCookiesToRequest(request);
    Response response = chain.proceed(request);
    if(!response.headers("Set-Cookie").isEmpty()) {
      for (String header : response.headers("Set-Cookie")) {
        String[] headerPath = header.split(";",2);
        cookies.add(headerPath[0]);
      }
    }

    return response;
  }

  private Request addCookiesToRequest(Request request) {
    if (request.url().toString().contains("9000/login") ||
        request.url().toString().contains("9000/oauth2/authorize?")) {
      for (String cookie : cookies) {
        request = request.newBuilder().addHeader("Cookie", cookie).build();
      }
    }
    return request;
  }

  public String getCookie(String cookie) {
    for (String cook : cookies) {
      String[] paths = cook.split("=");
      if(paths.length == 2 && paths[0].equals(cookie)) {
        return paths[1];
      }
    }
    return null;
  }
}
