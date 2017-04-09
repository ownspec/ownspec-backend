package com.ownspec.center.repository.component;

import static com.ownspec.center.model.component.QComponentVersion.componentVersion;

import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
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
  public List<ComponentVersion> findAll(Long projectId, Boolean generic, List<ComponentType> types, String query, String sort) {

    //JPASQLQuery sqlQuery = new JPASQLQuery(entityManager, new PostgreSQLTemplates());
    JPAQuery jpaQuery = new JPAQuery(entityManager);

    jpaQuery.from(componentVersion)
        .innerJoin(componentVersion.component)
        .leftJoin(componentVersion.component.project);

    List<Predicate> predicates = new ArrayList<>();

    if (StringUtils.isNotBlank(query)) {
      predicates.add(componentVersion.title.containsIgnoreCase(query));
    }

    //BooleanExpression
    //Expressions.booleanOperation(Ops.OR).or();

    List<Predicate> projectPredicates = new ArrayList<>();

    if (projectId != null) {
      projectPredicates.add(componentVersion.component.project.id.eq(projectId));
    }

    if (generic != null && generic) {
        projectPredicates.add(componentVersion.component.project.id.isNull());
    }

    if (!projectPredicates.isEmpty()) {
      if (projectPredicates.size() == 1) {
        predicates.addAll(projectPredicates);
      } else {
        predicates.add(Expressions.booleanOperation(Ops.OR, projectPredicates.toArray(new Predicate[]{})));
      }
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

    jpaQuery.orderBy(componentVersion.component.codeNumber.asc());

    if (predicates.size() > 0) {
      jpaQuery.where(predicates.toArray(new Predicate[]{}));
    }

    return jpaQuery.select(componentVersion).fetch();
  }


}
