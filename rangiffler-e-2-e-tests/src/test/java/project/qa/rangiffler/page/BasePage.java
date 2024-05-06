package project.qa.rangiffler.page;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.page.component.Header;

public abstract class BasePage<T extends BasePage> {

  protected static final Config CFG = Config.getInstance();

  protected final SelenideElement toaster = $(".Toastify__toast-body");

  protected final Header header = new Header();

  public abstract T waitForPageLoaded();


  @SuppressWarnings("unchecked")
/*  @Step("")
  public T checkMessage(Msg msg) {
    toaster.shouldHave(text(msg.getMessage()));
    return (T) this;
  }*/

  public Header getHeader() {
    return header;
  }

}
