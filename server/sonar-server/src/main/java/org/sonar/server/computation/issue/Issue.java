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
import javax.annotation.CheckForNull;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Duration;
import org.sonar.core.issue.tracking.Trackable;

public interface Issue extends Trackable {

  boolean isNew();

  RuleKey getRuleKey();

  /**
   * FIXME document 0 vs 1
   */
  int getLine();

  // FIXME html or markdown ?
  String getMessage();

  /**
   * Returns MAJOR by default.
   */
  String getSeverity();

  boolean isOverriddenSeverity();

  @CheckForNull
  String getAssigneeLogin();

  @CheckForNull
  Duration getDebt();

  @CheckForNull
  Double getEffortToFix();

  /**
   * Return the issue uuid only when {@link xxx} has been executed, otherwise it will throw an exception.
   */
  String getUuid();

  @CheckForNull
  String getResolution();

  /**
   * Never null, by default it's {@link org.sonar.api.issue.Issue#STATUS_OPEN}.
   */
  String getStatus();

  Set<String> getTags();
}
