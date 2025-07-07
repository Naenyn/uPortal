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
import javax.xml.namespace.QName;
import org.hibernate.usertype.UserType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

/** */
public class QNameType implements UserType<QName> {
    private static final long serialVersionUID = 1L;
    
    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<QName> returnedClass() {
        return QName.class;
    }

    @Override
    public boolean equals(QName x, QName y) {
        return (x == y) || (x != null && x.equals(y));
    }

    @Override
    public int hashCode(QName x) {
        return x != null ? x.hashCode() : 0;
    }

    @Override
    public QName nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String str = rs.getString(position);
        return str == null ? null : QName.valueOf(str);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, QName value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, value.toString());
        }
    }

    @Override
    public QName deepCopy(QName value) {
        return value; // QName is immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(QName value) {
        return value;
    }

    @Override
    public QName assemble(Serializable cached, Object owner) {
        return (QName) cached;
    }

    @Override
    public QName replace(QName original, QName target, Object owner) {
        return original;
    }
}
