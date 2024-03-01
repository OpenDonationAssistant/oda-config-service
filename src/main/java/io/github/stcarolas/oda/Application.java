package io.github.stcarolas.oda;

import io.micronaut.runtime.Micronaut;

public class Application {

  public static void main(String[] args) {
    Beans.context = Micronaut.build(args).banner(false).start();
  }
}
