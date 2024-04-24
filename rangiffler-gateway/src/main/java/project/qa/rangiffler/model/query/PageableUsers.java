package project.qa.rangiffler.model.query;

import java.util.List;

public class PageableUsers extends PageableObjects<User> {

  public PageableUsers(List<User> objects, boolean hasNext) {
    super(objects, hasNext);
  }

}
