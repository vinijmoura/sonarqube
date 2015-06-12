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

package org.sonar.server.computation.step;

import java.util.Date;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.System2;
import org.sonar.batch.protocol.output.BatchReport;
import org.sonar.core.persistence.DbTester;
import org.sonar.server.computation.batch.BatchReportReaderRule;
import org.sonar.server.computation.batch.TreeRootHolderRule;
import org.sonar.server.computation.component.Component;
import org.sonar.server.computation.component.DbIdsRepository;
import org.sonar.server.computation.component.DumbComponent;
import org.sonar.server.computation.language.LanguageRepository;
import org.sonar.server.computation.metric.Metric;
import org.sonar.server.computation.metric.MetricImpl;
import org.sonar.server.computation.metric.MetricRepository;
import org.sonar.server.db.DbClient;
import org.sonar.server.measure.persistence.MeasureDao;
import org.sonar.server.source.index.SourceLineIndex;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersistNumberOfDaysSinceLastCommitStepTest extends BaseStepTest {

  @ClassRule
  public static DbTester db = new DbTester();

  @Rule
  public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

  @Rule
  public BatchReportReaderRule reportReader = new BatchReportReaderRule();

  DbIdsRepository dbIdsRepository = new DbIdsRepository();

  PersistNumberOfDaysSinceLastCommitStep sut;

  DbClient dbClient;
  SourceLineIndex sourceLineIndex;
  MetricRepository metricRepository;
  Settings projectSettings;
  LanguageRepository languageRepository;

  @Before
  public void setUp() throws Exception {
    db.truncateTables();
    dbClient = new DbClient(db.database(), db.myBatis(), new MeasureDao());
    sourceLineIndex = mock(SourceLineIndex.class);
    metricRepository = mock(MetricRepository.class);
    projectSettings = new Settings();
    languageRepository = mock(LanguageRepository.class);
    when(metricRepository.getByKey(anyString())).thenReturn(new MetricImpl(10, "key", "name", Metric.MetricType.STRING));

    sut = new PersistNumberOfDaysSinceLastCommitStep(System2.INSTANCE, dbClient, sourceLineIndex, metricRepository, treeRootHolder, reportReader, dbIdsRepository);
  }

  @Override
  protected ComputationStep step() {
    return sut;
  }

  @Test
  public void persist_number_of_days_since_last_commit_from_report() {
    long threeDaysAgo = DateUtils.addDays(new Date(), -3).getTime();
    initProject();
    reportReader.putChangesets(
      BatchReport.Changesets.newBuilder()
        .setComponentRef(2)
        .addChangeset(
          BatchReport.Changesets.Changeset.newBuilder()
            .setDate(threeDaysAgo)
        )
        .build()
    );

    sut.execute();

    db.assertDbUnit(getClass(), "insert-from-report-result.xml", new String[] {"id"}, "project_measures");
  }

  @Test
  public void persist_number_of_days_since_last_commit_from_index() {
    Date sixDaysAgo = DateUtils.addDays(new Date(), -6);
    when(sourceLineIndex.lastCommitDateOnProject("project-uuid")).thenReturn(sixDaysAgo);
    initProject();

    sut.execute();

    db.assertDbUnit(getClass(), "insert-from-index-result.xml", new String[] {"id"}, "project_measures");
  }

  @Test
  public void no_scm_information_in_report_and_index() {
    initProject();

    sut.execute();

    db.assertDbUnit(getClass(), "empty.xml");
  }

  private void initProject() {
    Component project = DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("project-uuid").addChildren(
      DumbComponent.builder(Component.Type.FILE, 2).setUuid("file-uuid").build())
      .build();
    treeRootHolder.setRoot(project);
    dbIdsRepository.setSnapshotId(project, 1000);
  }
}
