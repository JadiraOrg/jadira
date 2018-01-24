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
package org.jadira.usertype.unitsofmeaurement.indriya;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.measure.Quantity;

import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.jadira.usertype.unitsofmeaurement.indriya.testmodel.QuantityHolder;
import org.junit.Test;

import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

public class TestPersistentQuantity extends AbstractDatabaseTest<QuantityHolder> {

    private static final Quantity<?>[] quantities = new Quantity[]{Quantities.getQuantity(10, Units.METRE), null};

    public TestPersistentQuantity() {
    	super(TestIndriyaSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < quantities.length; i++) {
            QuantityHolder item = new QuantityHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setQuantity(quantities[i]);

            persist(item);
        }

        for (int i = 0; i < quantities.length; i++) {
            QuantityHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (quantities[i] == null) {
                assertNull(item.getQuantity());
            } else {
                assertEquals(quantities[i].toString(), item.getQuantity().toString());
            }
        }

        verifyDatabaseTable();
    }
}
