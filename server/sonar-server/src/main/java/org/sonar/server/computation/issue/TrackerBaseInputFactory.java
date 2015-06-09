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
package org.sonar.server.computation.issue;

import java.util.Iterator;
import java.util.List;
import org.sonar.core.issue.tracking.Input;
import org.sonar.core.issue.tracking.LazyInput;
import org.sonar.server.computation.component.Component;
import org.sonar.server.db.DbClient;

/**
 * Factory of {@link Input} of base data for issue tracking. Data are lazy-loaded.
 */
public class TrackerBaseInputFactory {

  private final BaseIssuesLoader baseIssuesLoader;
  private final DbClient dbClient;

  public TrackerBaseInputFactory(BaseIssuesLoader baseIssuesLoader, DbClient dbClient) {
    this.baseIssuesLoader = baseIssuesLoader;
    this.dbClient = dbClient;
  }

  public Input<BaseIssue> create(Component component) {
    return new BaseLazyInput(component);
  }

  private class BaseLazyInput extends LazyInput<BaseIssue> {
    private final Component component;

    private BaseLazyInput(Component component) {
      this.component = component;
    }

    @Override
    protected Iterator<String> loadSourceLines() {
      // DbSession session = dbClient.openSession(false);
      // dbClient.fileSourceDao().readLineHashesStream(session, component.getUuid(), Functions.identity());
      // FIXME see ComputeFileSourceData
      // TODO what about non-files ?
      throw new UnsupportedOperationException();
    }

    @Override
    protected List<BaseIssue> loadIssues() {
      return baseIssuesLoader.loadForComponentUuid(component.getUuid());
    }
  }
}
