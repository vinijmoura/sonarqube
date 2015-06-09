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

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.sonar.api.rule.RuleKey;
import org.sonar.batch.protocol.output.BatchReport;
import org.sonar.core.issue.tracking.Input;
import org.sonar.core.issue.tracking.LazyInput;
import org.sonar.core.issue.tracking.LineHashSequence;
import org.sonar.server.computation.batch.BatchReportReader;
import org.sonar.server.computation.component.Component;

public class TrackerRawInputFactory {

  private final BatchReportReader reportReader;

  public TrackerRawInputFactory(BatchReportReader reportReader) {
    this.reportReader = reportReader;
  }

  public Input<MutableIssue> create(Component component) {
    return new RawLazyInput(component);
  }

  private class RawLazyInput extends LazyInput<MutableIssue> {
    private final Component component;

    private RawLazyInput(Component component) {
      this.component = component;
    }

    @Override
    protected Iterator<String> loadSourceLines() {
      return reportReader.readFileSource(component.getRef());
    }

    @Override
    protected List<MutableIssue> loadIssues() {
      List<BatchReport.Issue> reportIssues = reportReader.readComponentIssues(component.getRef());
      List<MutableIssue> issues = new ArrayList<>();
      if (!reportIssues.isEmpty()) {
        // optimization - do not load line hashes if there are no issues
        LineHashSequence lineHashSeq = getLineHashSequence();
        for (BatchReport.Issue reportIssue : reportIssues) {
          issues.add(toIssue(lineHashSeq, reportIssue));
        }
      }
      return issues;
    }

    private IssueImpl toIssue(LineHashSequence lineHashSeq, BatchReport.Issue reportIssue) {
      RuleKey ruleKey = RuleKey.of(reportIssue.getRuleRepository(), reportIssue.getRuleKey());
      int line = reportIssue.hasLine() ? reportIssue.getLine() : 0;

      IssueImpl issue = new IssueImpl(ruleKey, line);
      if (reportIssue.hasMsg()) {
        issue.setMessage(reportIssue.getMsg());
      }
      if (reportIssue.hasSeverity()) {
        issue.setSeverity(reportIssue.getSeverity().name());
      }
      if (reportIssue.hasEffortToFix()) {
        issue.setEffortToFix(reportIssue.getEffortToFix());
      }
      issue.setTags(Sets.newHashSet(reportIssue.getTagList()));
      issue.setLineHash(lineHashSeq.getHashForLine(line));
      // TODO issue attributes
      return issue;
    }
  }
}
