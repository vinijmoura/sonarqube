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

package org.sonar.core.user;

import java.util.List;
import javax.annotation.Nullable;
import org.apache.ibatis.session.SqlSession;
import org.sonar.api.batch.BatchSide;
import org.sonar.api.security.DefaultGroups;
import org.sonar.api.server.ServerSide;
import org.sonar.core.persistence.DaoComponent;
import org.sonar.core.persistence.DbSession;

@ServerSide
@BatchSide
public class RoleDao implements DaoComponent {

  public List<String> selectUserPermissions(DbSession session, String userLogin, @Nullable Long resourceId) {
    return session.getMapper(RoleMapper.class).selectUserPermissions(userLogin, resourceId);
  }

  public List<String> selectGroupPermissions(DbSession session, String groupName, @Nullable Long resourceId) {
    return session.getMapper(RoleMapper.class).selectGroupPermissions(groupName, resourceId, DefaultGroups.isAnyone(groupName));
  }

  public void insertGroupRole(GroupRoleDto groupRole, SqlSession session) {
    mapper(session).insertGroupRole(groupRole);
  }

  public void insertUserRole(UserRoleDto userRole, SqlSession session) {
    mapper(session).insertUserRole(userRole);
  }

  public void deleteUserRole(UserRoleDto userRole, SqlSession session) {
    mapper(session).deleteUserRole(userRole);
  }

  public void deleteGroupRole(GroupRoleDto groupRole, SqlSession session) {
    mapper(session).deleteGroupRole(groupRole);
  }

  public void deleteGroupRolesByResourceId(Long resourceId, SqlSession session) {
    mapper(session).deleteGroupRolesByResourceId(resourceId);
  }

  public void deleteUserRolesByResourceId(Long resourceId, SqlSession session) {
    mapper(session).deleteUserRolesByResourceId(resourceId);
  }

  public int countResourceGroupRoles(DbSession session, Long resourceId) {
    return mapper(session).countResourceGroupRoles(resourceId);
  }

  public int countResourceUserRoles(DbSession session, Long resourceId) {
    return mapper(session).countResourceUserRoles(resourceId);
  }

  public void deleteGroupRolesByGroupId(DbSession session, long groupId) {
    mapper(session).deleteGroupRolesByGroupId(groupId);
  }

  private static RoleMapper mapper(SqlSession session) {
    return session.getMapper(RoleMapper.class);
  }
}
