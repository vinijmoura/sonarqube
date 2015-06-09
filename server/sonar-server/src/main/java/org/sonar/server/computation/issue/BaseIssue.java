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

import java.util.Set;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Duration;
import org.sonar.core.issue.db.IssueDto;
import org.sonar.core.issue.tracking.Trackable;

/**
 * A base issue is an open issue that already exists when integrating
 * the analysis report. It's visible by end-users.
 * <p/>
 * This class is basically an adapter over {@link IssueDto}.
 */
public class BaseIssue implements Issue, Trackable {
  private final IssueDto dto;

  BaseIssue(IssueDto dto) {
    this.dto = dto;
  }

  public IssueDto getDto() {
    return dto;
  }

  @Override
  public int getLine() {
    Integer line = dto.getLine();
    return line == null ? 0 : line;
  }

  @Override
  public String getMessage() {
    return dto.getMessage();
  }

  @Override
  public String getSeverity() {
    return dto.getSeverity();
  }

  @Override
  public boolean isOverriddenSeverity() {
    return dto.isManualSeverity();
  }

  @Override
  public String getAssigneeLogin() {
    return dto.getAssignee();
  }

  @Override
  public Duration getDebt() {
    Long debt = dto.getDebt();
    return debt == null ? null : Duration.create(debt);
  }

  @Override
  public Double getEffortToFix() {
    return dto.getEffortToFix();
  }

  @Override
  public String getUuid() {
    return dto.getKey();
  }

  @Override
  public String getResolution() {
    return dto.getResolution();
  }

  @Override
  public String getStatus() {
    return dto.getStatus();
  }

  @Override
  public Set<String> getTags() {
    return dto.getTags();
  }

  @Override
  public String getLineHash() {
    return dto.getChecksum();
  }

  @Override
  public boolean isNew() {
    // false by definition
    return false;
  }

  @Override
  public RuleKey getRuleKey() {
    return dto.getRuleKey();
  }
}
