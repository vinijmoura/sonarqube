/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.measure.custom.ws;

import org.sonar.api.measures.Metric;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.core.component.ComponentDto;
import org.sonar.core.measure.custom.db.CustomMeasureDto;
import org.sonar.core.metric.db.MetricDto;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.MyBatis;
import org.sonar.server.db.DbClient;
import org.sonar.server.user.UserSession;

import static com.google.common.base.Preconditions.checkArgument;

public class CreateAction implements CustomMeasuresWsAction {
  public static final String ACTION = "create";
  private static final String PARAM_PROJECT_ID = "projectId";
  private static final String PARAM_PROJECT_KEY = "projectKey";
  private static final String PARAM_METRIC_ID = "metricId";
  private static final String PARAM_VALUE = "value";
  private static final String PARAM_DESCRIPTION = "description";

  private final DbClient dbClient;
  private final UserSession userSession;
  private final System2 system;

  public CreateAction(DbClient dbClient, UserSession userSession, System2 system) {
    this.dbClient = dbClient;
    this.userSession = userSession;
    this.system = system;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction(ACTION)
      .setDescription("Create a custom measure.<br /> " +
        "The project id or the project key must be provided. <br/>" +
        "Requires 'Administer System' permission or 'Administer' permission on the project.")
      .setSince("5.2")
      .setPost(true)
      .setHandler(this);

    action.createParam(PARAM_PROJECT_ID)
      .setDescription("Project id")
      .setExampleValue("ce4c03d6-430f-40a9-b777-ad877c00aa4d");

    action.createParam(PARAM_PROJECT_KEY)
      .setDescription("Project key")
      .setExampleValue("org.apache.hbas:hbase");

    action.createParam(PARAM_METRIC_ID)
      .setRequired(true)
      .setDescription("Metric id")
      .setExampleValue("16");

    action.createParam(PARAM_VALUE)
      .setRequired(true)
      .setDescription("Measure value. Value type depends on metric type.")
      .setExampleValue("47");

    action.createParam(PARAM_DESCRIPTION)
      .setDescription("Description")
      .setExampleValue("Team size growing.");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {

    DbSession dbSession = dbClient.openSession(false);
    String description = request.param(PARAM_DESCRIPTION);
    long now = system.now();

    try {
      ComponentDto component = searchComponent(dbSession, request);
      MetricDto metric = searchMetric(dbSession, request.mandatoryParamAsInt(PARAM_METRIC_ID));
      CustomMeasureDto measure = new CustomMeasureDto()
        .setComponentUuid(component.uuid())
        .setComponentId(component.getId())
        .setMetricId(metric.getId())
        .setDescription(description)
        .setCreatedAt(now);
      setMeasureValue(measure, request, metric);
      dbClient.customMeasureDao().insert(dbSession, measure);
    } finally {
      MyBatis.closeQuietly(dbSession);
    }
  }

  private void setMeasureValue(CustomMeasureDto measure, Request request, MetricDto metric) {
    String valueAsString = request.mandatoryParam(PARAM_VALUE);
    Metric.ValueType metricType = Metric.ValueType.valueOf(metric.getValueType());
    switch (metricType) {
      case BOOL:
        boolean booleanValue = request.mandatoryParamAsBoolean(PARAM_VALUE);
        measure.setValue(booleanValue ? 1.0d : 0.0d);
        break;
      case INT:
      case MILLISEC:
        int intValue = request.mandatoryParamAsInt(PARAM_VALUE);
        measure.setValue(intValue);
      case FLOAT:
      case PERCENT:
      case RATING:
        measure.setValue(Double.valueOf(valueAsString));
        break;
      case STRING:
      case LEVEL:
      case DATA:
      case DISTRIB:
      default:
        measure.setTextValue(valueAsString);
        break;
    }
  }

  /*
   * 
   * private void setValueAccordingToMetricType(Measure<?> measure, org.sonar.api.measures.Metric<?> m, org.sonar.api.measures.Measure
   * measureToSave) {
   * switch (m.getType()) {
   * case BOOL:
   * measureToSave.setValue(Boolean.TRUE.equals(measure.value()) ? 1.0 : 0.0);
   * break;
   * case INT:
   * case MILLISEC:
   * measureToSave.setValue(Double.valueOf((Integer) measure.value()));
   * break;
   * case FLOAT:
   * case PERCENT:
   * case RATING:
   * measureToSave.setValue((Double) measure.value());
   * break;
   * case STRING:
   * case LEVEL:
   * case DATA:
   * case DISTRIB:
   * measureToSave.setData((String) measure.value());
   * break;
   * case WORK_DUR:
   * measureToSave.setValue(Double.valueOf((Long) measure.value()));
   * break;
   * default:
   * throw new UnsupportedOperationException("Unsupported type :" + m.getType());
   * }
   * }
   */

  private MetricDto searchMetric(DbSession dbSession, int metricId) {
    return dbClient.metricDao().selectById(dbSession, metricId);
  }

  private ComponentDto searchComponent(DbSession dbSession, Request request) {
    String projectUuid = request.param(PARAM_PROJECT_ID);
    String projectKey = request.param(PARAM_PROJECT_KEY);
    checkArgument(projectUuid != null ^ projectKey != null, "The component key or the component id must be provided.");

    if (projectUuid != null) {
      return dbClient.componentDao().selectByUuid(dbSession, projectUuid);
    } else {
      return dbClient.componentDao().selectByKey(dbSession, projectKey);
    }
  }
}
