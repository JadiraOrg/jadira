package org.jadira.cloning.data;

import java.util.Calendar;

import javax.xml.datatype.XMLGregorianCalendar;

public class DeepCopyHolder {

    public IdHolder value;

    public java.sql.Timestamp timestamp;
    public Calendar calendar;
    public transient XMLGregorianCalendar xmlCalendar;
}