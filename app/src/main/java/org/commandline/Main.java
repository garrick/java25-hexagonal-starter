package org.commandline;

public class Main {

    public static void main(String[] args) {
          Main main = new Main();
          String greeting = main.helloWorld();
          assert "Hello, world!".equals(greeting);
        }

   public String helloWorld() {
          return "Hello, world!";
  }
}
