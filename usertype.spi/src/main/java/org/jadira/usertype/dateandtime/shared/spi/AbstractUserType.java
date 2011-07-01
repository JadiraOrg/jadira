/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.dateandtime.shared.spi;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.type.SerializationException;

public abstract class AbstractUserType implements Serializable {

    private static final long serialVersionUID = -3503387360213242237L;

    public final boolean isMutable() {
        return false;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if ((x == null) || (y == null)) {
            return false;
        }
        return x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    public Object assemble(Serializable cachedValue, Object owner) throws HibernateException {
        return deepCopy(cachedValue);
    }

    public Serializable disassemble(Object value) throws HibernateException {

        final Serializable result;

        if (value == null) {
            result = null;
        } else {
            final Object deepCopy = deepCopy(value);
            if (!(deepCopy instanceof Serializable)) {
                throw new SerializationException(String.format("deepCopy of %s is not serializable", value), null);
            }
            result = (Serializable) deepCopy;
        }

        return result;
    }

    public Object replace(Object originalValue, Object target, Object owner) throws HibernateException {
        return deepCopy(originalValue);
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }
}
