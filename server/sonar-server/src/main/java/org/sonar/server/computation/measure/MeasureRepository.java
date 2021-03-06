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
package org.sonar.server.computation.measure;

import com.google.common.base.Optional;
import java.util.Map;
import org.sonar.server.computation.component.Component;
import org.sonar.server.computation.metric.Metric;
import org.sonar.server.computation.metric.MetricImpl;

public interface MeasureRepository {

  /**
   * Retrieves the base measure (ie. the one currently existing in DB) for the specified {@link Component} for
   * the specified {@link MetricImpl} if it exists.
   *
   * @throws NullPointerException if either argument is {@code null}
   */
  Optional<Measure> getBaseMeasure(Component component, Metric metric);

  /**
   * Retrieves the measure created during the current analysis for the specified {@link Component} for the specified
   * {@link MetricImpl} if it exists (ie. one created by the Compute Engine or the Batch).
   */
  Optional<Measure> getRawMeasure(Component component, Metric metric);

  /**
   * @return {@link Measure}s for the specified {@link Component} mapped by their metric key.
   */
  Map<String, Measure> getRawMeasures(Component component);

  /**
   * Adds the specified measure for the specified Component and Metric. There can be no more than one measure for a
   * specific combination of Component and Metric.
   *
   * @throws NullPointerException if any of the argument is null
   * @throws UnsupportedOperationException when trying to add a measure when one already exists for the specified Component/Metric paar
   */
  void add(Component component, Metric metric, Measure measure);
}
