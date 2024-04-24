package project.qa.rangiffler.model.query;

import java.util.List;

public  abstract class PageableObjects<T> {
  List<T> objects;
  boolean hasNext;

  public PageableObjects(List<T> objects, boolean hasNext) {
    this.objects = objects;
    this.hasNext = hasNext;
  }

  public List<T> getObjects() {
    return objects;
  }

  public boolean isHasNext() {
    return hasNext;
  }
}
