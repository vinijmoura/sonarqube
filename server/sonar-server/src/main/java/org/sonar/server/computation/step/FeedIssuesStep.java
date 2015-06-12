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

import org.sonar.core.issue.tracking.Tracker;
import org.sonar.core.issue.tracking.Tracking;
import org.sonar.server.computation.component.Component;
import org.sonar.server.computation.component.DepthTraversalTypeAwareVisitor;
import org.sonar.server.computation.component.TreeRootHolder;
import org.sonar.server.computation.issue.BaseIssue;
import org.sonar.server.computation.issue.IssueCache;
import org.sonar.server.computation.issue.MutableIssue;
import org.sonar.server.computation.issue.TrackerBaseInputFactory;
import org.sonar.server.computation.issue.TrackerRawInputFactory;

import static org.sonar.server.computation.component.DepthTraversalTypeAwareVisitor.Order.POST_ORDER;

public class FeedIssuesStep implements ComputationStep {

  private final TreeRootHolder treeRootHolder;
  private final TrackerBaseInputFactory baseInputFactory;
  private final TrackerRawInputFactory rawInputFactory;
  private final Tracker<MutableIssue, BaseIssue> issueTracker;
  private final IssueCache issueCache;

  public FeedIssuesStep(TreeRootHolder treeRootHolder, TrackerBaseInputFactory baseInputFactory,
                        TrackerRawInputFactory rawInputFactory, Tracker<MutableIssue, BaseIssue> issueTracker, IssueCache issueCache) {
    this.treeRootHolder = treeRootHolder;
    this.baseInputFactory = baseInputFactory;
    this.rawInputFactory = rawInputFactory;
    this.issueTracker = issueTracker;
    this.issueCache = issueCache;
  }

  @Override
  public void execute() {
    // for each component:
    // load base issues DONE
    // read raw issues DONE
    // load QProfile DONE
    // tracking DONE
    // relocate manual issues TODO
    // apply workflow
    // if new, auto-assign
    // if new, copy rule tags
    // if new, guess author
    // execute IssueHandlers
    // close base issues
    // complete changelog

    new DepthTraversalTypeAwareVisitor(Component.Type.FILE, POST_ORDER) {
      @Override
      protected void visitAny(Component component) {
        processIssues(component);
      }
    }.visit(treeRootHolder.getRoot());
  }

  private void processIssues(Component component) {
    Tracking<MutableIssue, BaseIssue> tracking = issueTracker.track(rawInputFactory.create(component), baseInputFactory.create(component));

  }

  @Override
  public String getDescription() {
    return "Feed issues";
  }
}
