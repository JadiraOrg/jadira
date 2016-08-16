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
package org.jadira.usertype.dateandtime.joda.testmodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.joda.time.TimeOfDay;
import org.joda.time.contrib.hibernate.PersistentTimeOfDay;

@Entity
@Table(name = "localTime")
@TypeDef(name = "joda_TimeOfDayAsTimeType", typeClass = PersistentTimeOfDay.class)
@SuppressWarnings("deprecation")
public class TimeOfDayAsTimeJoda implements Serializable {

    private static final long serialVersionUID = 220365788633752921L;

    @Id
    private long id;

    @Column
    private String name;

    @Column(name = "localTime")
    @Type(type = "joda_TimeOfDayAsTimeType")
    private TimeOfDay timeOfDay;

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

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
}
