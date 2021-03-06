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
import org.junit.Test;
import org.sonar.batch.protocol.output.BatchReport;
import org.sonar.server.computation.metric.Metric;
import org.sonar.server.computation.metric.MetricImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

public class BatchMeasureToMeasureTest {
  private static final Metric SOME_INT_METRIC = new MetricImpl("key", "name", Metric.MetricType.INT);
  private static final Metric SOME_LONG_METRIC = new MetricImpl("key", "name", Metric.MetricType.WORK_DUR);
  private static final Metric SOME_DOUBLE_METRIC = new MetricImpl("key", "name", Metric.MetricType.FLOAT);
  private static final Metric SOME_STRING_METRIC = new MetricImpl("key", "name", Metric.MetricType.STRING);
  private static final Metric SOME_BOOLEAN_METRIC = new MetricImpl("key", "name", Metric.MetricType.BOOL);
  private static final Metric SOME_LEVEL_METRIC = new MetricImpl("key", "name", Metric.MetricType.LEVEL);

  private static final String SOME_DATA = "some_data man!";
  private static final String SOME_ALERT_TEXT = "some alert text_be_careFul!";
  private static final BatchReport.Measure EMPTY_BATCH_MEASURE = BatchReport.Measure.newBuilder().build();

  private BatchMeasureToMeasure underTest = new BatchMeasureToMeasure();

  @Test
  public void toMeasure_returns_absent_for_null_argument() {
    assertThat(underTest.toMeasure(null, SOME_INT_METRIC)).isAbsent();
  }

  @Test(expected = NullPointerException.class)
  public void toMeasure_throws_NPE_if_metric_argument_is_null() {
    underTest.toMeasure(EMPTY_BATCH_MEASURE, null);
  }

  @Test(expected = NullPointerException.class)
  public void toMeasure_throws_NPE_if_both_arguments_are_null() {
    underTest.toMeasure(null, null);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_no_string_value_for_LEVEL_Metric() {
    Optional<Measure> measure = underTest.toMeasure(EMPTY_BATCH_MEASURE, SOME_LEVEL_METRIC);

    assertThat(measure).isPresent();
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_invalid_string_value_for_LEVEL_Metric() {
    Optional<Measure> measure = underTest.toMeasure(BatchReport.Measure.newBuilder().setStringValue("trololo").build(), SOME_LEVEL_METRIC);

    assertThat(measure).isPresent();
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_value_in_wrong_case_for_LEVEL_Metric() {
    Optional<Measure> measure = underTest.toMeasure(BatchReport.Measure.newBuilder().setStringValue("waRn").build(), SOME_LEVEL_METRIC);

    assertThat(measure).isPresent();
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_returns_value_for_LEVEL_Metric() {
    for (Measure.Level alertStatus : Measure.Level.values()) {
      verify_toMeasure_returns_value_for_LEVEL_Metric(alertStatus);
    }
  }

  private void verify_toMeasure_returns_value_for_LEVEL_Metric(Measure.Level expectedQualityGateStatus) {
    Optional<Measure> measure = underTest.toMeasure(BatchReport.Measure.newBuilder().setStringValue(expectedQualityGateStatus.name()).build(), SOME_LEVEL_METRIC);
    assertThat(measure).isPresent();
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.LEVEL);
    assertThat(measure.get().getLevelValue()).isEqualTo(expectedQualityGateStatus);
  }

  @Test
  public void toMeasure_for_LEVEL_Metric_maps_QualityGateStatus() {
    BatchReport.Measure batchMeasure = BatchReport.Measure.newBuilder()
      .setStringValue(Measure.Level.OK.name())
      .setAlertStatus(Measure.Level.ERROR.name())
      .setAlertText(SOME_ALERT_TEXT)
      .build();

    Optional<Measure> measure = underTest.toMeasure(batchMeasure, SOME_LEVEL_METRIC);

    assertThat(measure).isPresent();
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.LEVEL);
    assertThat(measure.get().getLevelValue()).isEqualTo(Measure.Level.OK);
    assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(Measure.Level.ERROR);
    assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(SOME_ALERT_TEXT);
  }

  @Test
  public void toMeasure_for_LEVEL_Metric_parses_level_from_data() {
    for (Measure.Level level : Measure.Level.values()) {
      verify_toMeasure_for_LEVEL_Metric_parses_level_from_data(level);
    }
  }

  private void verify_toMeasure_for_LEVEL_Metric_parses_level_from_data(Measure.Level expectedLevel) {
    BatchReport.Measure batchMeasure = BatchReport.Measure.newBuilder()
      .setStringValue(expectedLevel.name())
      .build();

    Optional<Measure> measure = underTest.toMeasure(batchMeasure, SOME_LEVEL_METRIC);

    assertThat(measure).isPresent();
    assertThat(measure.get().getLevelValue()).isEqualTo(expectedLevel);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_no_value_for_Int_Metric() {
    Optional<Measure> measure = underTest.toMeasure(EMPTY_BATCH_MEASURE, SOME_INT_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_maps_data_and_alert_properties_in_dto_for_Int_Metric() {
    BatchReport.Measure batchMeasure = BatchReport.Measure.newBuilder()
      .setIntValue(10)
      .setStringValue(SOME_DATA)
      .setAlertStatus(Measure.Level.OK.name()).setAlertText(SOME_ALERT_TEXT)
      .build();

    Optional<Measure> measure = underTest.toMeasure(batchMeasure, SOME_INT_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.INT);
    assertThat(measure.get().getIntValue()).isEqualTo(10);
    assertThat(measure.get().getData()).isEqualTo(SOME_DATA);
    assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(Measure.Level.OK);
    assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(SOME_ALERT_TEXT);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_no_value_for_Long_Metric() {
    Optional<Measure> measure = underTest.toMeasure(EMPTY_BATCH_MEASURE, SOME_LONG_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_returns_long_part_of_value_in_dto_for_Long_Metric() {
    Optional<Measure> measure = underTest.toMeasure(BatchReport.Measure.newBuilder().setLongValue(15l).build(), SOME_LONG_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.LONG);
    assertThat(measure.get().getLongValue()).isEqualTo(15);
  }

  @Test
  public void toMeasure_maps_data_and_alert_properties_in_dto_for_Long_Metric() {
    BatchReport.Measure batchMeasure = BatchReport.Measure.newBuilder()
      .setLongValue(10l)
      .setStringValue(SOME_DATA)
      .setAlertStatus(Measure.Level.OK.name()).setAlertText(SOME_ALERT_TEXT)
      .build();

    Optional<Measure> measure = underTest.toMeasure(batchMeasure, SOME_LONG_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.LONG);
    assertThat(measure.get().getLongValue()).isEqualTo(10);
    assertThat(measure.get().getData()).isEqualTo(SOME_DATA);
    assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(Measure.Level.OK);
    assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(SOME_ALERT_TEXT);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_no_value_for_Double_Metric() {
    Optional<Measure> measure = underTest.toMeasure(EMPTY_BATCH_MEASURE, SOME_DOUBLE_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_maps_data_and_alert_properties_in_dto_for_Double_Metric() {
    BatchReport.Measure batchMeasure = BatchReport.Measure.newBuilder()
      .setDoubleValue(10.6395d)
      .setStringValue(SOME_DATA)
      .setAlertStatus(Measure.Level.OK.name()).setAlertText(SOME_ALERT_TEXT)
      .build();

    Optional<Measure> measure = underTest.toMeasure(batchMeasure, SOME_DOUBLE_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.DOUBLE);
    assertThat(measure.get().getDoubleValue()).isEqualTo(10.6395d);
    assertThat(measure.get().getData()).isEqualTo(SOME_DATA);
    assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(Measure.Level.OK);
    assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(SOME_ALERT_TEXT);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_no_value_for_Boolean_metric() {
    Optional<Measure> measure = underTest.toMeasure(EMPTY_BATCH_MEASURE, SOME_BOOLEAN_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric() {
    verify_toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric(true);
    verify_toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric(false);
  }

  private void verify_toMeasure_returns_false_value_if_dto_has_invalid_value_for_Boolean_metric(boolean expected) {
    Optional<Measure> measure = underTest.toMeasure(BatchReport.Measure.newBuilder().setBooleanValue(expected).build(), SOME_BOOLEAN_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.BOOLEAN);
    assertThat(measure.get().getBooleanValue()).isEqualTo(expected);
  }

  @Test
  public void toMeasure_maps_data_and_alert_properties_in_dto_for_Boolean_metric() {
    BatchReport.Measure batchMeasure = BatchReport.Measure.newBuilder()
      .setBooleanValue(true).setStringValue(SOME_DATA).setAlertStatus(Measure.Level.OK.name()).setAlertText(SOME_ALERT_TEXT).build();

    Optional<Measure> measure = underTest.toMeasure(batchMeasure, SOME_BOOLEAN_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.BOOLEAN);
    assertThat(measure.get().getBooleanValue()).isTrue();
    assertThat(measure.get().getData()).isEqualTo(SOME_DATA);
    assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(Measure.Level.OK);
    assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(SOME_ALERT_TEXT);
  }

  @Test
  public void toMeasure_returns_no_value_if_dto_has_no_value_for_String_Metric() {
    Optional<Measure> measure = underTest.toMeasure(EMPTY_BATCH_MEASURE, SOME_STRING_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.NO_VALUE);
  }

  @Test
  public void toMeasure_maps_alert_properties_in_dto_for_String_Metric() {
    BatchReport.Measure batchMeasure = BatchReport.Measure.newBuilder()
      .setStringValue(SOME_DATA)
      .setAlertStatus(Measure.Level.OK.name()).setAlertText(SOME_ALERT_TEXT)
      .build();

    Optional<Measure> measure = underTest.toMeasure(batchMeasure, SOME_STRING_METRIC);

    assertThat(measure.isPresent());
    assertThat(measure.get().getValueType()).isEqualTo(Measure.ValueType.STRING);
    assertThat(measure.get().getStringValue()).isEqualTo(SOME_DATA);
    assertThat(measure.get().getData()).isEqualTo(SOME_DATA);
    assertThat(measure.get().getQualityGateStatus().getStatus()).isEqualTo(Measure.Level.OK);
    assertThat(measure.get().getQualityGateStatus().getText()).isEqualTo(SOME_ALERT_TEXT);
  }

}
