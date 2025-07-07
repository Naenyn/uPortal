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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/** Uses a regular expression to validate strings coming to/from the database. */
public class FunctionalNameType implements UserType<String> {
    private static final long serialVersionUID = 1L;

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<String> returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(String x, String y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(String x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public String nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String value = rs.getString(position);
        return rs.wasNull() ? null : value;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, String value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            validate(value);
            st.setString(index, value);
        }
    }

    @Override
    public String deepCopy(String value) {
        return value; // String is immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(String value) {
        return value;
    }

    @Override
    public String assemble(Serializable cached, Object owner) {
        return (String) cached;
    }

    @Override
    public String replace(String detached, String managed, Object owner) {
        return detached;
    }

    public static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[^\\w-]");
    public static final Pattern VALID_FNAME_PATTERN = Pattern.compile("^[\\w-]+$");

    public static void validate(String fname) {
        if (!isValid(fname)) {
            throw new IllegalArgumentException(
                    "'"
                            + fname
                            + "' does not validate against FunctionalName pattern: "
                            + VALID_FNAME_PATTERN.pattern());
        }
    }

    public static boolean isValid(String fname) {
        if (fname == null) {
            return false;
        }

        final Matcher matcher = VALID_FNAME_PATTERN.matcher(fname);
        return matcher.matches();
    }

    public static String makeValid(String fname) {
        if (fname == null) {
            return "_";
        }

        return INVALID_CHARS_PATTERN.matcher(fname).replaceAll("_");
    }
}
