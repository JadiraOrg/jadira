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
package org.jadira.usertype.phonenumber.testmodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jadira.cdt.phonenumber.impl.E164PhoneNumberWithExtension;
import org.jadira.usertype.phonenumber.PersistentE164PhoneNumberWithExtension;

@Entity
@Table(name = "e164PhoneNumberWithExtension")
@TypeDef(name = "E164PhoneNumberWithExtension", typeClass = PersistentE164PhoneNumberWithExtension.class)
public class E164PhoneNumberWithExtensionHolder implements Serializable {

    private static final long serialVersionUID = -1674416082110551506L;

    @Id
    private long id;

    @Column
    private String name;

    @Column
    @Type(type = "E164PhoneNumberWithExtension")
    private E164PhoneNumberWithExtension E164PhoneNumberWithExtension;

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

    public E164PhoneNumberWithExtension getE164PhoneNumberWithExtension() {
        return E164PhoneNumberWithExtension;
    }

    public void setE164PhoneNumberWithExtension(E164PhoneNumberWithExtension E164PhoneNumberWithExtension) {
        this.E164PhoneNumberWithExtension = E164PhoneNumberWithExtension;
    }
}
