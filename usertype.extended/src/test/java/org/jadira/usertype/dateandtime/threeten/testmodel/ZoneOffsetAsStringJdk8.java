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
package org.jadira.usertype.dateandtime.threeten.testmodel;

import java.io.Serializable;
import java.time.ZoneOffset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jadira.usertype.dateandtime.threeten.PersistentZoneOffsetAsString;

@Entity
@Table(name = "zonedOffsetAsString")
@TypeDef(name = "test_ZoneOffsetAsStringJdk8Type", typeClass = PersistentZoneOffsetAsString.class)
public class ZoneOffsetAsStringJdk8 implements Serializable {

    private static final long serialVersionUID = 5888505180004123768L;

    @Id
    private long id;

    @Column
    private String name;

    @Column
    @Type(type = "test_ZoneOffsetAsStringJdk8Type")
    private ZoneOffset zoneOffset;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    public void setZoneOffset(ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
    }
}
