package com.ownspec.center.model;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
public class Business {

  public enum Sector {
    NA(Collections.emptyList()),
    ENERGY(asList(
        Industry.IND_1_1,
        Industry.IND_1_2,
        Industry.IND_1_3
    )),
    MATERIAL(asList(
        Industry.IND_2_1,
        Industry.IND_2_2,
        Industry.IND_2_3
    )),
    INDUSTRIAL(asList(
        Industry.IND_3_1,
        Industry.IND_3_2,
        Industry.IND_3_3,
        Industry.IND_3_4
    )),
    SERVICES_AND_RETAILS(asList(
        Industry.IND_4_1,
        Industry.IND_4_2,
        Industry.IND_4_3,
        Industry.IND_4_4
    )),
    HEALTH(asList(
        Industry.IND_5_1,
        Industry.IND_5_2
    )),
    FINANCIAL(asList(
        Industry.IND_6_1,
        Industry.IND_6_2,
        Industry.IND_6_3,
        Industry.IND_6_4
    )),
    ICT(asList(
        Industry.IND_7_1,
        Industry.IND_7_2,
        Industry.IND_7_3
    ));

    private List<Industry> industries;

    private Sector(List<Industry> industries) {
      this.industries = industries;
    }

    public List<Industry> getIndustries() {
      return industries;
    }
  }

  public enum Industry {
    NA(null),
    IND_1_1("Energy Equipment & Services"),
    IND_1_2("Oil, Gas & Consumable Fuels"),
    IND_1_3("Intelligent Energy"),

    IND_2_1("Chemicals"),
    IND_2_2("Construction Materials"),
    IND_2_3("Containers & Packaging"),

    IND_3_1("Aerospace, Defense & Security"),
    IND_3_2("Automobile, Transport and Logistic"),
    IND_3_3("Construction & Engineering"),
    IND_3_4("Electrical Equipment"),

    IND_4_1("Media"),
    IND_4_2("Food"),
    IND_4_3("Textiles, Apparel & Luxury Goods"),
    IND_4_4("Hotels, Restaurant & Leisure"),

    IND_5_1("Health Care Equipment & Services"),
    IND_5_2("Pharmaceuticals, Biotechnology & Life Sciences"),

    IND_6_1("Banks"),
    IND_6_2("Diversified Financial"),
    IND_6_3("Insurance"),
    IND_6_4("Real Estate"),

    IND_7_1("Software & Services"),
    IND_7_2("Technology Hardware & Equipment"),
    IND_7_3("Telecommunication Services");

    private String definition;

    private Industry(String definition) {
      this.definition = definition;
    }

    public String getDefinition() {
      return definition;
    }
  }
}
