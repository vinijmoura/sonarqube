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

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.api.utils.Duration;

import static com.google.common.base.Strings.isNullOrEmpty;

public class IssueImpl implements MutableIssue<IssueImpl> {

  // immutable fields
  private final RuleKey ruleKey;
  private final int line;

  // mutable fields
  private String assigneeLogin = null;
  private boolean overriddenSeverity = false;
  private String message = null;
  private String severity = Severity.MAJOR;
  private String uuid = null;
  private Duration debt = null;
  private Double effortToFix = null;
  private String resolution = null;
  private String status = org.sonar.api.issue.Issue.STATUS_OPEN;
  private boolean isNew = true;
  private String lineHash = null;
  private Set<String> tags = new HashSet<>();

  public IssueImpl(RuleKey ruleKey, int line) {
    Objects.requireNonNull(ruleKey, "Issue rule key must not be null");
    Preconditions.checkArgument(line >= 0, "Issue line must be greater than or equal zero");
    this.ruleKey = ruleKey;
    this.line = line;
  }

  @Override
  public RuleKey getRuleKey() {
    return ruleKey;
  }

  @Override
  public String getAssigneeLogin() {
    return assigneeLogin;
  }

  @Override
  public IssueImpl setAssigneeLogin(@Nullable String s) {
    this.assigneeLogin = s;
    return this;
  }

  @Override
  public IssueImpl setSeverity(String s) {
    Preconditions.checkArgument(!isNullOrEmpty(s), "Issue severity must not be null");
    this.severity = s;
    return this;
  }

  /**
   * TODO rename to htmlMessage, markdownMessage or textMessage
   */
  @Override
  public IssueImpl setMessage(@Nullable String s) {
    this.message = s;
    return this;
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String getSeverity() {
    return severity;
  }

  @Override
  public Duration getDebt() {
    return debt;
  }

  @Override
  public IssueImpl setDebt(@Nullable Duration d) {
    this.debt = d;
    return this;
  }

  @Override
  public String getUuid() {
    if (uuid == null) {
      throw new IllegalStateException("Issue uuid has not been fed yet");
    }
    return uuid;
  }

  @Override
  public IssueImpl setUuid(String s) {
    if (this.uuid != null) {
      throw new IllegalStateException("Issue uuid is already set: " + this.uuid);
    }
    this.uuid = s;
    return this;
  }

  @Override
  public boolean isOverriddenSeverity() {
    return overriddenSeverity;
  }

  @Override
  public IssueImpl setOverriddenSeverity(boolean b) {
    this.overriddenSeverity = b;
    return this;
  }

  @Override
  public IssueImpl setResolution(@Nullable String s) {
    this.resolution = s;
    return this;
  }

  @Override
  public IssueImpl setStatus(String s) {
    Objects.requireNonNull(s, "Issue status must not be null");
    this.status = s;
    return null;
  }

  @Override
  public String getResolution() {
    return resolution;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }

  @Override
  public IssueImpl setIsNew(boolean b) {
    this.isNew = b;
    return this;
  }

  @Override
  public String getLineHash() {
    return lineHash;
  }

  @Override
  public IssueImpl setLineHash(@Nullable String s) {
    this.lineHash = s;
    return this;
  }

  @Override
  public Double getEffortToFix() {
    return effortToFix;
  }

  @Override
  public IssueImpl setEffortToFix(@Nullable Double d) {
    this.effortToFix = d;
    return this;
  }

  @Override
  public Set<String> getTags() {
    return tags;
  }

  @Override
  public IssueImpl setTags(Set<String> s) {
    Objects.requireNonNull(s, "Issue tags must not be null");
    this.tags = s;
    return this;
  }
}
