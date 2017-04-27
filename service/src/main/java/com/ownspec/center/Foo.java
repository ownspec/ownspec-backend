package com.ownspec.center;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nlabrot on 23/04/17.
 */
public class Foo {
  private String a;
  private String b;
  private String c;
  private List<String> d = new ArrayList<>();

  public Foo(String a, String b, String c, List<String> d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public String getA() {
    return a;
  }

  public String getB() {
    return b;
  }

  public String getC() {
    return c;
  }

  public List<String> getD() {
    return d;
  }

  public void setA(String a) {
    this.a = a;
  }

  public void setB(String b) {
    this.b = b;
  }

  public void setC(String c) {
    this.c = c;
  }

  public void setD(List<String> d) {
    this.d = d;
  }
}

