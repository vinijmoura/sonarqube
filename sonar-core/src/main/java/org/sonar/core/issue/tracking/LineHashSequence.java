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

import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Sequence of hash of lines for a given file
 */
public class LineHashSequence {

  private static final int[] EMPTY_INTS = new int[0];

  /**
   * Hashes of lines. Line 1 is at index 0. No null elements.
   */
  private final String[] hashes;
  private final Map<String, int[]> linesByHash;

  public LineHashSequence(String[] hashes) {
    this.hashes = hashes;
    this.linesByHash = new HashMap<>(hashes.length);
    for (int line = 1; line <= hashes.length; line++) {
      String hash = hashes[line - 1];
      int[] lines = linesByHash.get(hash);
      linesByHash.put(hash, appendLineTo(line, lines));
    }
  }

  /**
   * Number of lines
   */
  public int length() {
    return hashes.length;
  }

  /**
   * Checks if the line, starting with 1, is defined.
   */
  public boolean hasLine(int line) {
    return 0 < line && line <= hashes.length;
  }

  /**
   * The lines, starting with 1, that matches the given hash.
   */
  public int[] getLinesForHash(String hash) {
    int[] lines = linesByHash.get(hash);
    return lines == null ? EMPTY_INTS : lines;
  }

  /**
   * Hash of the given line, which starts with 1. Return empty string
   * is the line does not exist.
   */
  public String getHashForLine(int line) {
    return Strings.nullToEmpty(hashes[line - 1]);
  }

  String[] getHashes() {
    return hashes;
  }

  private static int[] appendLineTo(int line, @Nullable int[] to) {
    int[] result;
    if (to == null) {
      result = new int[] {line};
    } else {
      result = new int[to.length + 1];
      System.arraycopy(to, 0, result, 0, to.length);
      result[result.length - 1] = line;
    }
    return result;
  }


}
