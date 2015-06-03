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
package org.sonar.core.issue.tracking;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeRecognizerTest {

  @Test
  public void lengthOfMaximalBlock() {
    /**
     * - line 4 of first sequence is "d"
     * - line 4 of second sequence is "d"
     * - in each sequence, the 3 lines before and the line after are similar -> block size is 5
     */
    assertThat(compute(seq("abcde"), seq("abcde"), 4, 4)).isEqualTo(5);

    assertThat(compute(seq("abcde"), seq("abcd"), 4, 4)).isEqualTo(4);
    assertThat(compute(seq("bcde"), seq("abcde"), 4, 4)).isEqualTo(0);
    assertThat(compute(seq("bcde"), seq("abcde"), 3, 4)).isEqualTo(4);
  }

  private int compute(LineHashSequence seqA, LineHashSequence seqB, int ai, int bi) {
    return BlockRecognizer.lengthOfMaximalBlock(seqA, ai, seqB, bi);
  }

  private static LineHashSequence seq(String text) {
    String[] array = new String[text.length()];
    for (int i = 0; i < text.length(); i++) {
      array[i] = "" + text.charAt(i);
    }
    return new LineHashSequence(array);
  }

}
