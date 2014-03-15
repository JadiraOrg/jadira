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
package org.jadira.usertype.moneyandcurrency.joda.testmodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyMajorAmountAndCurrency;
import org.joda.money.Money;

@Entity
@Table(name = "moneyMajorAmountAndCurrency")
@TypeDef(name = "testjoda_MoneyMajorAmountWithCurrencyType", typeClass = PersistentMoneyMajorAmountAndCurrency.class)
public class MoneyMajorAmountAndCurrencyHolder implements Serializable {

    private static final long serialVersionUID = -1674416082110551506L;

    @Id
    private long id;

    @Column
    private String name;

    @Columns(columns = { @Column(name = "MY_CURRENCY"), @Column(name = "MY_AMOUNT") })
    @Type(type = "testjoda_MoneyMajorAmountWithCurrencyType")
    private Money money;

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

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }
}
