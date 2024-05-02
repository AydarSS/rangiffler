package project.qa.rangiffler.model.query;

import java.util.List;

public class PageablePhoto extends PageableObjects<Photo> {

  public PageablePhoto(List<Photo> objects, boolean hasNext) {
    super(objects, hasNext);
  }

}
