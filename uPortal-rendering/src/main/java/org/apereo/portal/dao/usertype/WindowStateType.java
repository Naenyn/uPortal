/**
 * Licensed to Apereo under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. Apereo
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at the
 * following location:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apereo.portal.dao.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.portlet.WindowState;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/** Converts portlet WindowState objects to/from strings. */
public class WindowStateType implements UserType<WindowState> {
    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<WindowState> returnedClass() {
        return WindowState.class;
    }

    @Override
    public boolean equals(WindowState x, WindowState y) {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(WindowState x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public WindowState nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String value = rs.getString(position);
        if (rs.wasNull() || value == null) {
            return null;
        }
        return new WindowState(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, WindowState value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, value.toString());
        }
    }

    @Override
    public WindowState deepCopy(WindowState value) {
        if (value == null) {
            return null;
        }
        return new WindowState(value.toString());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public java.io.Serializable disassemble(WindowState value) {
        return (java.io.Serializable) deepCopy(value);
    }

    @Override
    public WindowState assemble(java.io.Serializable cached, Object owner) {
        return deepCopy((WindowState) cached);
    }

    @Override
    public WindowState replace(WindowState detached, WindowState managed, Object owner) {
        return deepCopy(detached);
    }
}
