package com.ownspec.center.repository.component;

import static com.ownspec.center.model.component.QComponentVersion.componentVersion;

import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by labrot
 */
public class ComponentVersionRepositoryImpl implements ComponentVersionRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<ComponentVersion> findAll(Long projectId, List<ComponentType> types, String query, String sort) {

    //JPASQLQuery sqlQuery = new JPASQLQuery(entityManager, new PostgreSQLTemplates());
    JPAQuery jpaQuery = new JPAQuery(entityManager);

    jpaQuery.from(componentVersion)
        .innerJoin(componentVersion.component)
        .leftJoin(componentVersion.component.project);

    List<Predicate> predicates = new ArrayList<>();

    if (StringUtils.isNotBlank(query)) {
      predicates.add(componentVersion.title.likeIgnoreCase(query));
    }

    if (projectId != null) {
      predicates.add(componentVersion.component.project.id.eq(projectId));
    }

    if (!types.isEmpty()) {
      predicates.add(componentVersion.component.type.in(types));
    }

    /*if (StringUtils.isNotBlank(sort)) {
      Arrays.stream(sort.split(",")).map(s -> {
        String[] sortItem = s.split(":");
        if (sortItem.length > 0){
        }
      });
    }*/

    if (predicates.size() > 0) {
      jpaQuery.where(predicates.toArray(new Predicate[]{}));
    }

    return jpaQuery.select(componentVersion).fetch();
  }


}
