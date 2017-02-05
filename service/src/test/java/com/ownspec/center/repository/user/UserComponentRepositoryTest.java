package com.ownspec.center.repository.user;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.ImmutableComponentVersionDto;
import com.ownspec.center.dto.ImmutableEstimatedTimeDto;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.model.user.UserComponent;
import com.ownspec.center.repository.component.ComponentRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 17/12/2016
 *
 * @author lyrold
 */
public class UserComponentRepositoryTest extends AbstractTest {

  private static final List<EstimatedTimeDto> ESTIMATED_TIMES = Arrays.asList(
      ImmutableEstimatedTimeDto.newEstimatedTimeDto()
          .userCategory(new UserCategory("Analyst", 430.00))
          .time(1d)
          .timeUnit(TimeUnit.DAYS)
          .build(),

      ImmutableEstimatedTimeDto.newEstimatedTimeDto()
          .userCategory(new UserCategory("Developer", 585.34))
          .time(6d)
          .timeUnit(TimeUnit.DAYS)
          .build(),

      ImmutableEstimatedTimeDto.newEstimatedTimeDto()
          .userCategory(new UserCategory("Analyst", 480.65))
          .time(3d)
          .timeUnit(TimeUnit.DAYS)
          .build()
  );
  @Autowired
  ComponentRepository componentRepository;

  @Autowired
  UserComponentRepository userComponentRepository;


  @Before
  public void DBInit() {
    for (Integer index = 0; index < 5; index++) {

      ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
          .title("REQUIREMENT " + index)
          .type(ComponentType.REQUIREMENT)
          .content("test1")
          .projectId(1L)
          .distributionLevel(DistributionLevel.INTERNAL)
          .coverageStatus(CoverageStatus.UNCOVERED)
          .requiredTest(true)
          .estimatedTimes(ESTIMATED_TIMES)
          .build();

      componentService.create(componentDto);
    }


    for (Integer index = 0; index < 7; index++) {
      ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()

          .title("DOCUMENT " + index)
          .type(ComponentType.DOCUMENT)
          .content("test1")
          .projectId(1L)
          .distributionLevel(DistributionLevel.INTERNAL)
          .coverageStatus(CoverageStatus.UNCOVERED)
          .requiredTest(false)
          .build();
      componentService.create(componentDto);
    }

  }

  @Test
  public void findOneByUserIdAndComponentId() throws Exception {

  }

  @Test
  public void findTop3ByUserIdAndFavoriteTrueOrderByLastModifiedDateDesc() throws Exception {
  }

  @Test
  public void findTop3ByUserIdAndComponentTypeLikeOrderByLastModifiedDateDesc() throws Exception {

    int index = 0;
    for (Component component : componentRepository.findAll()) {
      UserComponent userComponent = new UserComponent();
      userComponent.setUser(userRepository.findOne(4L));
      userComponent.setComponent(component);
      userComponent.setFavorite(false);
      userComponent.setLastModifiedDate(Instant.now().plus(index++, ChronoUnit.MINUTES));
      userComponentRepository.save(userComponent);
    }

    Assert.assertEquals(componentRepository.count(), userComponentRepository.count());

    List<UserComponent> userComponents =
        userComponentRepository.findTop3ByUserIdAndComponentTypeOrderByLastModifiedDateDesc(4L, ComponentType.DOCUMENT);
    Assert.assertEquals(3, userComponents.size());

    for (UserComponent userComponent : userComponents) {
      Assert.assertEquals("n.labrot@ownspec.com", userComponent.getUser().getUsername());
    }
  }

}