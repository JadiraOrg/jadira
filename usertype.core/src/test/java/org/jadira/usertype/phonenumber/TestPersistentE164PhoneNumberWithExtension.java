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
package org.jadira.usertype.phonenumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jadira.cdt.phonenumber.impl.E164PhoneNumberWithExtension;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.jadira.usertype.phonenumber.testmodel.E164PhoneNumberWithExtensionHolder;
import org.junit.Test;

public class TestPersistentE164PhoneNumberWithExtension extends AbstractDatabaseTest<E164PhoneNumberWithExtensionHolder> {

    private static final E164PhoneNumberWithExtension[] e164PhoneNumberWithExtensions = new E164PhoneNumberWithExtension[]{E164PhoneNumberWithExtension.ofE164PhoneNumberWithExtensionString("+441963350474"), E164PhoneNumberWithExtension.ofE164PhoneNumberWithExtensionString("+441963350474;ext=000032"), null};

    public TestPersistentE164PhoneNumberWithExtension() {
    	super(TestPhoneNumberSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < e164PhoneNumberWithExtensions.length; i++) {
            E164PhoneNumberWithExtensionHolder item = new E164PhoneNumberWithExtensionHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setE164PhoneNumberWithExtension(e164PhoneNumberWithExtensions[i]);

            persist(item);
        }

        for (int i = 0; i < e164PhoneNumberWithExtensions.length; i++) {
            E164PhoneNumberWithExtensionHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (e164PhoneNumberWithExtensions[i] == null) {
                assertNull(item.getE164PhoneNumberWithExtension());
            } else {
                assertEquals(e164PhoneNumberWithExtensions[i].toString(), item.getE164PhoneNumberWithExtension().toString());
            }
        }

        verifyDatabaseTable();
    }
}
