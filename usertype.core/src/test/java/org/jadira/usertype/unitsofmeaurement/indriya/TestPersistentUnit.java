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

import javax.measure.Unit;

import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.jadira.usertype.unitsofmeaurement.indriya.testmodel.UnitHolder;
import org.junit.Test;

import tec.units.indriya.unit.Units;

public class TestPersistentUnit extends AbstractDatabaseTest<UnitHolder> {

    private static final Unit<?>[] units = new Unit[]{Units.AMPERE, Units.BECQUEREL};

    public TestPersistentUnit() {
    	super(TestIndriyaSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < units.length; i++) {
            UnitHolder item = new UnitHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setUnit(units[i]);

            persist(item);
        }

        for (int i = 0; i < units.length; i++) {
            UnitHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (units[i] == null) {
                assertNull(item.getUnit());
            } else {
                assertEquals(units[i].getSymbol(), item.getUnit().getSymbol());
            }
        }

        verifyDatabaseTable();
    }
}
