package com.ownspec.center.controller;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.apache.commons.lang.RandomStringUtils.random;

import com.ownspec.center.dto.ImmutableEstimatedTimeDto;
import com.ownspec.center.dto.ImmutableProjectDto;
import com.ownspec.center.dto.ImmutableRiskAssessmentDto;
import com.ownspec.center.dto.component.ComponentVersionDto;
import com.ownspec.center.dto.component.ImmutableComponentVersionDto;
import com.ownspec.center.dto.user.UserCategoryDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.component.ComponentCodeCounter;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.component.DistributionLevel;
import com.ownspec.center.model.component.RequirementType;
import com.ownspec.center.model.riskassessment.FailureImpactType;
import com.ownspec.center.model.riskassessment.FrequencyOfUse;
import com.ownspec.center.model.riskassessment.Level;
import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.repository.ComponentCodeCounterRepository;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.user.UserCategoryRepository;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.ProjectService;
import com.ownspec.center.service.component.ComponentService;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import javax.annotation.PostConstruct;

/**
 * Created by nlabrot on 03/10/16.
 */
@RestController("/api/bootstrap")
public class BootstrapController {

  private static final String[] LOREM =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vel lorem quam. Etiam faucibus, enim in vehicula gravida, leo neque venenatis dui, vel consequat elit mi quis velit. Sed rhoncus sodales lacinia. Quisque fermentum fringilla ligula non porttitor. Cras tellus sapien, sodales eget laoreet et, interdum ac augue. Nunc molestie dui in turpis aliquet accumsan. Phasellus mollis lobortis nisl. Nullam pharetra placerat erat, quis euismod sem molestie ac. Mauris nec elit et mauris mattis tristique eget at metus. Maecenas blandit sagittis risus. Nunc at libero orci. Maecenas viverra est at eros tempus, at fringilla neque tempus."
          .split(" ");

  private static final String CONTENT = "<h1 id=\"1_\">Summary</h1>\n" +
                                        "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu</p>\n" +
                                        "<p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.</p>\n" +
                                        "<h1 id=\"2_\">Header</h1>\n" +
                                        "  <h2 id=\"2_1\">Logo</h2>\n" +
                                        "  <h2 id=\"2_2\">Menu</h2>\n" +
                                        "    <h3 id=\"2_2_1\">Features</h3>\n" +
                                        "<p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "  Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "  Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "  Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.</p>\n" +
                                        "<p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "  Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "  Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "  Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.</p>\n" +
                                        "    <h3 id=\"2_2_2\">Solutions</h3>\n" +
                                        "    <h3 id=\"2_2_3\">About</h3>\n" +
                                        "      <p>Section 1.10.33 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC\n" +
                                        "\n" +
                                        "        \"At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.\"</p>\n" +
                                        "      <h4 id=\"2_2_1_1\">Company</h4>\n" +
                                        "      <h4 id=\"2_2_1_2\">Team</h4>\n" +
                                        "    <h3 id=\"2_2_4\">Contact</h3>\n" +
                                        "\n" +
                                        "  <h2 id=\"2_3\">User account</h2>\n" +
                                        "\n" +
                                        "<h1 id=\"3_\">Body</h1>\n" +
                                        "  <h2 id=\"3_1\">Layout</h2>\u200B\n" +
                                        "  <p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "    Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "    Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.\n" +
                                        "    Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.</p>\n" +
                                        "<h1 id=\"4_\">Footer</h1>\n";

  @Autowired
  protected ComponentService componentService;

  @Autowired
  protected ProjectRepository projectRepository;

  @Autowired
  private ComponentCodeCounterRepository componentCodeCounterRepository;

  @Autowired
  private UserCategoryRepository userCategoryRepository;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private ProjectService projectService;

  private UserCategory analyst;
  private UserCategory developer;
  private UserCategory tester;

  @PostConstruct
  public void init() {
    // User Categories
    analyst = userCategoryRepository.findByName("Analyst");
    if (analyst == null) {
      analyst = new UserCategory();
      analyst.setName("Analyst");
      analyst.setHourlyPrice(225d);
      userCategoryRepository.save(analyst);
    }

    developer = userCategoryRepository.findByName("Developer");
    if (developer == null) {
      developer = new UserCategory();
      developer.setName("Developer");
      developer.setHourlyPrice(450d);
      userCategoryRepository.save(developer);
    }

    tester = userCategoryRepository.findByName("Tester");
    if (tester == null) {
      tester = new UserCategory();
      tester.setName("Tester");
      tester.setHourlyPrice(400d);
      userCategoryRepository.save(tester);
    }
  }

  @RequestMapping
  @Transactional
  public void bootstrap(@RequestParam(value = "projectName", required = false) String projectName,
                        @RequestParam(value = "nbComponents", defaultValue = "5") Integer nbComponents,
                        @RequestParam(value = "nbRequirements", defaultValue = "20") Integer nbRequirements,
                        @RequestParam(value = "nbDocuments", defaultValue = "5") Integer nbDocuments
  ) {

    Long projectId = null;
    Project project = null;

    if (projectName != null) {
      ComponentCodeCounter ccc = new ComponentCodeCounter();
      ccc.setKey(projectName.toUpperCase());
      ccc = componentCodeCounterRepository.save(ccc);


      project = new Project();
      project.setTitle(projectName);
      project.setManager(authenticationService.getAuthenticatedUser());
      project.setComponentCodeCounter(ccc);
      project = projectRepository.save(project);
      projectId = project.getId();
    }


    String prefix = "";
    if (project != null) {
      prefix = "[Project " + projectId + "]";
    }

    // Projects
    createProject("OSCENTER",
        "Ownspec center",
        "Define and re-use your standard requirements, components and test cases, to boost your Time To Market");

    createProject("OSTESTING",
        "Ownspec testing",
        "Write, run and link test cases to your defined requirements, to release On-Scope with High Level Of Quality");

    createProject("OSAI",
        "Ownspec AI",
        "Data analysis will suggest you relative standards, law projects or any contents that help you staying modern and compliant");

    // Components
    for (Integer index = 0; index < nbComponents; index++) {
      createComponentVersion(ComponentType.COMPONENT, projectId, index);
    }

    // Requirements
    for (Integer index = 0; index < nbRequirements; index++) {
      createComponentVersion(ComponentType.REQUIREMENT, projectId, index);
    }

    // Documents
    if (projectId != null) {
      for (Integer index = 0; index < nbDocuments; index++) {
        createComponentVersion(ComponentType.DOCUMENT, projectId, index);
      }
    }

  }

  private String generateTitle() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < current().nextInt(6, 16); i++) {
      builder.append(LOREM[current().nextInt(LOREM.length)]).append(" ");
    }

    return builder.toString().trim();
  }

  private void createProject(String key, String title, String description) {
    if (projectRepository.findByComponentCodeCounter_Key(key) == null) {
      ImmutableProjectDto project = ImmutableProjectDto.newProjectDto()
          .key(key)
          .title(title)
          .description(description)
          .manager(UserDto.fromUser(authenticationService.getAuthenticatedUser()))
          .createdDate(Instant.now())
          .createdUser(UserDto.fromUser(authenticationService.getAuthenticatedUser()))
          .build();
      projectService.create(project);
    }
  }

  private void createComponentVersion(ComponentType type, Long projectId, int index) {
    ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
        .title(generateTitle())
        .version(String.valueOf(current().nextInt()))
        .type(type)
        .content(CONTENT)
        .projectId(projectId)
        .requirementType(RequirementType.values()[current().nextInt(RequirementType.values().length)])
        .distributionLevel(DistributionLevel.values()[current().nextInt(DistributionLevel.values().length)])
        .coverageStatus(CoverageStatus.values()[current().nextInt(CoverageStatus.values().length)])
        .requiredTest(RandomUtils.nextBoolean())
        .addTags(LOREM[current().nextInt(LOREM.length)], LOREM[current().nextInt(LOREM.length)])
        .riskAssessment(ImmutableRiskAssessmentDto.newRiskAssessmentDto()
            .failureImpactLevel(Level.values()[current().nextInt(Level.values().length)])
            .failureProbability(Level.values()[current().nextInt(Level.values().length)])
            .frequencyOfUse(FrequencyOfUse.values()[current().nextInt(FrequencyOfUse.values().length)])
            .failureImpactType(FailureImpactType.values()[current().nextInt(FailureImpactType.values().length)])
            .failureProcedure(
                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.")
            .riskDescription(random(current().nextInt(65)))
            .build())
        .addEstimatedTimes(
            ImmutableEstimatedTimeDto.newEstimatedTimeDto()
                .userCategory(UserCategoryDto.fromUserCategory(analyst))
                .duration(String.valueOf(current().nextInt(50)) + (index % current().nextInt() == 0 ? "d" : "h"))
                .build(),
            ImmutableEstimatedTimeDto.newEstimatedTimeDto()
                .userCategory(UserCategoryDto.fromUserCategory(developer))
                .duration(String.valueOf(current().nextInt(50)) + (index % current().nextInt() == 0 ? "d" : "h"))
                .build(),
            ImmutableEstimatedTimeDto.newEstimatedTimeDto()
                .userCategory(UserCategoryDto.fromUserCategory(tester))
                .duration(String.valueOf(current().nextInt(50)) + (index % current().nextInt() == 0 ? "d" : "h"))
                .build())
        .build();

    ComponentVersion component = componentService.create(componentDto).getRight();
    /*if (current().nextBoolean()) {
      workflowService.updateStatus(component.getId(),
          Status.values()[current().nextInt(Status.values().length)],
          random(current().nextInt(65), true, false));
    }*/
  }
}
