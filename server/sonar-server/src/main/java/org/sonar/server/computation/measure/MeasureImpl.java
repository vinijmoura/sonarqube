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

import com.google.common.base.Preconditions;
import java.util.Locale;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public final class MeasureImpl implements Measure {

  private final ValueType valueType;
  @Nullable
  private final Double value;
  @Nullable
  private final String data;
  @Nullable
  private final Level dataLevel;
  @Nullable
  private QualityGateStatus qualityGateStatus;

  protected MeasureImpl(ValueType valueType, @Nullable Double value, @Nullable String data, @Nullable Level dataLevel) {
    this.valueType = valueType;
    this.value = value;
    this.data = data;
    this.dataLevel = dataLevel;
  }

  public static MeasureImpl create(boolean value, @Nullable String data) {
    return new MeasureImpl(ValueType.BOOLEAN, value ? 1.0d : 0.0d, data, null);
  }

  public static MeasureImpl create(int value, @Nullable String data) {
    return new MeasureImpl(ValueType.INT, (double) value, data, null);
  }

  public static MeasureImpl create(long value, @Nullable String data) {
    return new MeasureImpl(ValueType.LONG, (double) value, data, null);
  }

  public static MeasureImpl create(double value, @Nullable String data) {
    return new MeasureImpl(ValueType.DOUBLE, value, data, null);
  }

  public static MeasureImpl create(String value) {
    return new MeasureImpl(ValueType.STRING, null, requireNonNull(value), null);
  }

  public static MeasureImpl create(Level level) {
    return new MeasureImpl(ValueType.LEVEL, null, null, requireNonNull(level));
  }

  public static MeasureImpl createNoValue() {
    return new MeasureImpl(ValueType.NO_VALUE, null, null, null);
  }

  @Override
  public ValueType getValueType() {
    return valueType;
  }

  @Override
  public boolean getBooleanValue() {
    checkValueType(ValueType.BOOLEAN);
    return value == 1.0d;
  }

  @Override
  public int getIntValue() {
    checkValueType(ValueType.INT);
    return value.intValue();
  }

  @Override
  public long getLongValue() {
    checkValueType(ValueType.LONG);
    return value.longValue();
  }

  @Override
  public double getDoubleValue() {
    checkValueType(ValueType.DOUBLE);
    return value;
  }

  @Override
  public String getStringValue() {
    checkValueType(ValueType.STRING);
    return data;
  }

  @Override
  public Level getLevelValue() {
    checkValueType(ValueType.LEVEL);
    return dataLevel;
  }

  @Override
  public String getData() {
    return data;
  }

  private void checkValueType(ValueType expected) {
    if (valueType != expected) {
      throw new IllegalStateException(
          String.format(
              "value can not be converted to %s because current value type is a %s",
              expected.toString().toLowerCase(Locale.US),
              valueType
          ));
    }
  }

  public MeasureImpl setQualityGateStatus(QualityGateStatus qualityGateStatus) {
    this.qualityGateStatus = requireNonNull(qualityGateStatus, "Can not set a null QualityGate status");
    return this;
  }

  @Override
  public boolean hasQualityGateStatus() {
    return this.qualityGateStatus != null;
  }

  @Override
  public QualityGateStatus getQualityGateStatus() {
    Preconditions.checkState(qualityGateStatus != null, "Measure does not have an QualityGate status");
    return this.qualityGateStatus;
  }

}
