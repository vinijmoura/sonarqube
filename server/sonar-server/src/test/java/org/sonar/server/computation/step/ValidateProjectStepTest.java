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

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.CoreProperties;
import org.sonar.api.config.Settings;
import org.sonar.batch.protocol.Constants;
import org.sonar.batch.protocol.output.BatchReport;
import org.sonar.core.component.ComponentDto;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.DbTester;
import org.sonar.server.component.ComponentTesting;
import org.sonar.server.component.db.ComponentDao;
import org.sonar.server.computation.batch.BatchReportReaderRule;
import org.sonar.server.computation.batch.TreeRootHolderRule;
import org.sonar.server.computation.component.Component;
import org.sonar.server.computation.component.DumbComponent;
import org.sonar.server.db.DbClient;

public class ValidateProjectStepTest {

  private static final String PROJECT_KEY = "PROJECT_KEY";
  private static final String MODULE_KEY = "MODULE_KEY";

  @ClassRule
  public static DbTester dbTester = new DbTester();
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  @Rule
  public BatchReportReaderRule reportReader = new BatchReportReaderRule();
  @Rule
  public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

  DbClient dbClient;

  DbSession dbSession;

  Settings settings;

  ValidateProjectStep sut;

  @Before
  public void setUp() throws Exception {
    dbTester.truncateTables();
    dbClient = new DbClient(dbTester.database(), dbTester.myBatis(), new ComponentDao());
    dbSession = dbClient.openSession(false);
    settings = new Settings();

    sut = new ValidateProjectStep(dbClient, settings, reportReader, treeRootHolder);
  }

  @After
  public void tearDown() throws Exception {
    dbSession.close();
  }

  @Test
  public void not_fail_if_provisioning_enforced_and_project_exists() throws Exception {
    reportReader.setMetadata(BatchReport.Metadata.newBuilder().build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .build());

    settings.appendProperty(CoreProperties.CORE_PREVENT_AUTOMATIC_PROJECT_CREATION, "true");
    dbClient.componentDao().insert(dbSession, ComponentTesting.newProjectDto("ABCD").setKey(PROJECT_KEY));
    dbSession.commit();
    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY).build());

    sut.execute();
  }

  @Test
  public void fail_if_provisioning_enforced_and_project_does_not_exists() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unable to scan non-existing project '" + PROJECT_KEY + "'");

    reportReader.setMetadata(BatchReport.Metadata.newBuilder().build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .build());

    settings.appendProperty(CoreProperties.CORE_PREVENT_AUTOMATIC_PROJECT_CREATION, "true");
    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY).build());

    sut.execute();
  }

  @Test
  public void fail_if_provisioning_not_enforced_and_project_does_not_exists() throws Exception {
    reportReader.setMetadata(BatchReport.Metadata.newBuilder().build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .build());

    settings.appendProperty(CoreProperties.CORE_PREVENT_AUTOMATIC_PROJECT_CREATION, "false");
    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY).build());

    sut.execute();
  }

  @Test
  public void not_fail_on_valid_branch() throws Exception {
    reportReader.setMetadata(BatchReport.Metadata.newBuilder()
      .setBranch("origin/master")
      .build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .build());
    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY + ":origin/master").build());

    sut.execute();
  }

  @Test
  public void fail_on_invalid_branch() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Validation of project failed:\n" +
      "  o \"bran#ch\" is not a valid branch name. Allowed characters are alphanumeric, '-', '_', '.' and '/'.");

    reportReader.setMetadata(BatchReport.Metadata.newBuilder()
      .setBranch("bran#ch")
      .build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .build());
    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY + ":bran#ch").build());

    sut.execute();
  }

  @Test
  public void fail_on_invalid_key() throws Exception {
    String invalidProjectKey = "Project\\Key";

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Validation of project failed:\n" +
      "  o \"Project\\Key\" is not a valid project or module key. Allowed characters are alphanumeric, '-', '_', '.' and ':', with at least one non-digit.\n" +
      "  o \"Module$Key\" is not a valid project or module key. Allowed characters are alphanumeric, '-', '_', '.' and ':', with at least one non-digit");

    reportReader.setMetadata(BatchReport.Metadata.newBuilder().build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(invalidProjectKey)
      .addChildRef(2)
      .build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(2)
      .setType(Constants.ComponentType.MODULE)
      .setKey("Module$Key")
      .build());
    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(invalidProjectKey).addChildren(
      DumbComponent.builder(Component.Type.MODULE, 2).setUuid("BCDE").setKey("Module$Key").build())
      .build());

    sut.execute();
  }

  @Test
  public void fail_if_module_key_is_already_used_as_project_key() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Validation of project failed:\n" +
      "  o The project \"" + MODULE_KEY + "\" is already defined in SonarQube but not as a module of project \"" + PROJECT_KEY + "\". " +
      "If you really want to stop directly analysing project \"" + MODULE_KEY + "\", please first delete it from SonarQube and then relaunch the analysis of project \""
      + PROJECT_KEY + "\".");

    reportReader.setMetadata(BatchReport.Metadata.newBuilder().build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .addChildRef(2)
      .build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(2)
      .setType(Constants.ComponentType.MODULE)
      .setKey(MODULE_KEY)
      .build());

    ComponentDto project = ComponentTesting.newProjectDto("ABCD").setKey(MODULE_KEY);
    dbClient.componentDao().insert(dbSession, project);
    dbSession.commit();

    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY).addChildren(
      DumbComponent.builder(Component.Type.MODULE, 2).setUuid("BCDE").setKey(MODULE_KEY).build())
      .build());

    sut.execute();
  }

  @Test
  public void fail_if_module_key_already_exists_in_another_project() throws Exception {
    String anotherProjectKey = "ANOTHER_PROJECT_KEY";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Validation of project failed:\n" +
      "  o Module \"" + MODULE_KEY + "\" is already part of project \"" + anotherProjectKey + "\"");

    reportReader.setMetadata(BatchReport.Metadata.newBuilder().build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .addChildRef(2)
      .build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(2)
      .setType(Constants.ComponentType.MODULE)
      .setKey(MODULE_KEY)
      .build());

    ComponentDto project = ComponentTesting.newProjectDto("ABCD").setKey(PROJECT_KEY);
    ComponentDto anotherProject = ComponentTesting.newProjectDto().setKey(anotherProjectKey);
    dbClient.componentDao().insert(dbSession, project, anotherProject);
    ComponentDto module = ComponentTesting.newModuleDto("BCDE", anotherProject).setKey(MODULE_KEY);
    dbClient.componentDao().insert(dbSession, module);
    dbSession.commit();

    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY).addChildren(
      DumbComponent.builder(Component.Type.MODULE, 2).setUuid("BCDE").setKey(MODULE_KEY).build())
      .build());

    sut.execute();
  }

  @Test
  public void fail_if_project_key_already_exists_as_module() throws Exception {
    String anotherProjectKey = "ANOTHER_PROJECT_KEY";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Validation of project failed:\n" +
      "  o The project \"" + PROJECT_KEY + "\" is already defined in SonarQube but as a module of project \"" + anotherProjectKey + "\". " +
      "If you really want to stop directly analysing project \"" + anotherProjectKey + "\", please first delete it from SonarQube and then relaunch the analysis of project \""
      + PROJECT_KEY + "\".");

    reportReader.setMetadata(BatchReport.Metadata.newBuilder().build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(1)
      .setType(Constants.ComponentType.PROJECT)
      .setKey(PROJECT_KEY)
      .addChildRef(2)
      .build());
    reportReader.putComponent(BatchReport.Component.newBuilder()
      .setRef(2)
      .setType(Constants.ComponentType.MODULE)
      .setKey(MODULE_KEY)
      .build());

    ComponentDto anotherProject = ComponentTesting.newProjectDto().setKey(anotherProjectKey);
    dbClient.componentDao().insert(dbSession, anotherProject);
    ComponentDto module = ComponentTesting.newModuleDto("ABCD", anotherProject).setKey(PROJECT_KEY);
    dbClient.componentDao().insert(dbSession, module);
    dbSession.commit();

    treeRootHolder.setRoot(DumbComponent.builder(Component.Type.PROJECT, 1).setUuid("ABCD").setKey(PROJECT_KEY).addChildren(
      DumbComponent.builder(Component.Type.MODULE, 2).setUuid("BCDE").setKey(MODULE_KEY).build())
      .build());

    sut.execute();
  }
}
