package com.ownspec.center.repository.component;

import static com.ownspec.center.model.Tables.OSUSER;
import static com.ownspec.center.model.Tables.PROJECT;
import static com.ownspec.center.model.Tables.WORKFLOW_INSTANCE;
import static com.ownspec.center.model.tables.Component.COMPONENT;
import static com.ownspec.center.model.tables.ComponentVersion.COMPONENT_VERSION;
import static com.ownspec.center.model.tables.WorkflowStatus.WORKFLOW_STATUS;

import com.ownspec.center.controller.component.ComponentVersionSearchBean;
import com.ownspec.center.dto.ImmutablePaginatedResult;
import com.ownspec.center.dto.PaginatedResult;
import com.ownspec.center.dto.component.ComponentVersionDto;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.tables.Osuser;
import com.ownspec.center.model.tables.WorkflowStatus;
import com.ownspec.center.model.tables.records.WorkflowStatusRecord;
import com.ownspec.center.repository.AnotherConverter;
import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 22/03/17.
 */
@Service
@Transactional
public class AnotherComponentVersionRepository {

  private final Configuration configuration;
  private final DSLContext dslContext;

  @Autowired
  private AnotherConverter anotherConverter;


  @Autowired
  public AnotherComponentVersionRepository(DSLContext dslContext) {
    this.configuration = dslContext.configuration();
    this.dslContext = dslContext;
  }

  /**
   * Find all pet ordered by name
   *
   * @return
   */
  public PaginatedResult<ComponentVersionDto> findAll(ComponentVersionSearchBean searchBean, long offset, long size) {

    DSLContext create = DSL.using(configuration);

    WorkflowStatus p1 = WORKFLOW_STATUS.as("p1");
    WorkflowStatus p2 = WORKFLOW_STATUS.as("p2");

    Table<WorkflowStatusRecord> lastStatusSubQuery = create.selectFrom(p1)
        .where(p1.ID.in(
            create.select(p2.ID)
                .from(p2)
                .where(p2.WORKFLOW_INSTANCE_ID.eq(p1.WORKFLOW_INSTANCE_ID))
                .orderBy(p2.WSORDER.desc())
                .limit(1)
        )).asTable();


    Osuser cvAssignee = OSUSER.as("cvAssignee");
    Osuser cvCreated = OSUSER.as("cvCreated");
    Osuser cvUpdated = OSUSER.as("cvUpdated");

    SelectOnConditionStep<Record> sql = create
        .select()
        .from(COMPONENT_VERSION)
        .join(COMPONENT).on(COMPONENT.ID.eq(COMPONENT_VERSION.COMPONENT_ID))
        .join(WORKFLOW_INSTANCE).on(WORKFLOW_INSTANCE.ID.eq(COMPONENT_VERSION.WORKFLOW_INSTANCE_ID))
        .join(lastStatusSubQuery).on(lastStatusSubQuery.field(p1.WORKFLOW_INSTANCE_ID).eq(WORKFLOW_INSTANCE.ID))
        .join(cvCreated).on(cvCreated.ID.eq(COMPONENT_VERSION.CREATED_USER_ID))
        .join(cvUpdated).on(cvUpdated.ID.eq(COMPONENT_VERSION.LAST_MODIFIED_USER_ID))
        .leftJoin(PROJECT).on(PROJECT.ID.eq(COMPONENT.PROJECT_ID))
        .leftJoin(cvAssignee).on(cvAssignee.ID.eq(COMPONENT_VERSION.ASSIGNED_TO_USER_ID));


    List<Condition> predicates = new ArrayList<>();

    if (StringUtils.isNotBlank(searchBean.getQuery())) {
      predicates.add(COMPONENT_VERSION.TITLE.lower().contains(searchBean.getQuery().toLowerCase()));
    }

    if (searchBean.getAssigneeId() != null) {
      predicates.add(COMPONENT_VERSION.ASSIGNED_TO_USER_ID.eq(searchBean.getAssigneeId()));
    }

    if (searchBean.getStatus() != null) {
      predicates.add(lastStatusSubQuery.field(p1.STATUS).eq(searchBean.getStatus().name()));
    }

    List<Condition> projectPredicates = new ArrayList<>();

    if (searchBean.getProjectId() != null) {
      projectPredicates.add(PROJECT.ID.eq(searchBean.getProjectId()));
    }

    if (searchBean.isGeneric() != null) {
      if (searchBean.isGeneric()) {
        projectPredicates.add(PROJECT.ID.isNull());
      } else {
        projectPredicates.add(PROJECT.ID.isNotNull());
      }
    }

    if (!projectPredicates.isEmpty()) {
      if (projectPredicates.size() == 1) {
        predicates.addAll(projectPredicates);
      } else {
        predicates.add(DSL.or(projectPredicates));
      }
    }

    if (!searchBean.getComponentTypes().isEmpty()) {
      predicates.add(COMPONENT.TYPE.in(searchBean.getComponentTypes().stream().map(ComponentType::name).toArray(String[]::new)));
    }



    /*if (StringUtils.isNotBlank(sort)) {
      Arrays.stream(sort.split(",")).map(s -> {
        String[] sortItem = s.split(":");
        if (sortItem.length > 0){

        }
      });
    }*/

    if (predicates.size() > 0) {
      sql.where(predicates);
    }


    int count = dslContext.fetchCount(sql);

    sql.offset((int) offset);
    sql.limit((int) size);

    Result<Record> records = sql.fetch();

    return ImmutablePaginatedResult.<ComponentVersionDto>newPaginatedResult()
        .offset(offset)
        .size(size)
        .total(count)
        .result(records.stream().map(r -> anotherConverter.convert(r.into(COMPONENT), r.into(COMPONENT_VERSION), r.into(cvCreated), r.into(cvUpdated), r.into(cvAssignee))
            .workflowInstance(anotherConverter.convert(r.into(WORKFLOW_INSTANCE), r.into(lastStatusSubQuery)).build())
            .build()).collect(Collectors.toList()))
        .build();
  }


}
