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
import javax.annotation.Nullable;
import org.sonar.api.utils.Duration;

public interface MutableIssue<SELF extends MutableIssue> extends Issue {

  SELF setIsNew(boolean b);

  SELF setAssigneeLogin(@Nullable String s);

  SELF setSeverity(String s);

  SELF setOverriddenSeverity(boolean b);

  SELF setMessage(@Nullable String s);

  SELF setDebt(@Nullable Duration d);

  SELF setEffortToFix(@Nullable Double d);

  SELF setUuid(String s);

  SELF setResolution(@Nullable String s);

  SELF setStatus(String s);

  SELF setLineHash(@Nullable String s);

  SELF setTags(Set<String> s);
}
