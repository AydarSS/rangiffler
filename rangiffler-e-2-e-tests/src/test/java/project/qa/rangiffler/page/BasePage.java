package project.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import project.qa.rangiffler.config.Config;
import project.qa.rangiffler.page.component.Header;
import project.qa.rangiffler.page.message.Msg;

public abstract class BasePage<T extends BasePage> {

  protected static final Config CFG = Config.getInstance();

  protected final SelenideElement message = $(".MuiAlert-message");

  protected final Header header = new Header();

  public abstract T waitForPageLoaded();


  @SuppressWarnings("unchecked")
  @Step("")
  public T checkMessage(Msg msg) {
    message.shouldHave(text(msg.getMessage()));
    return (T) this;
  }

  public Header getHeader() {
    return header;
  }

}
