/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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
package org.jadira.usertype.corejava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jadira.cdt.country.ISOCountryCode;
import org.jadira.usertype.corejava.testmodel.ISOCountryCodeWithPersistentEnumHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.junit.Test;

public class TestPersistentEnum extends AbstractDatabaseTest<ISOCountryCodeWithPersistentEnumHolder> {

    private static final ISOCountryCode[] isoCountryCodes = new ISOCountryCode[]{ISOCountryCode.AD, ISOCountryCode.GB, ISOCountryCode.US, ISOCountryCode.FR, null};

    public TestPersistentEnum() {
    	super(TestCoreJavaSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < isoCountryCodes.length; i++) {
            ISOCountryCodeWithPersistentEnumHolder item = new ISOCountryCodeWithPersistentEnumHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setISOCountryCode(isoCountryCodes[i]);

            persist(item);
        }

        for (int i = 0; i < isoCountryCodes.length; i++) {
            ISOCountryCodeWithPersistentEnumHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (isoCountryCodes[i] == null) {
                assertNull(item.getISOCountryCode());
            } else {
                assertEquals(isoCountryCodes[i].toString(), item.getISOCountryCode().toString());
            }
        }

        verifyDatabaseTable();
    }
}
