Usertype
================================================

User Types and classes for use with Hibernate. Includes user types for use with the javax.time 
classes included in the early review class of JSR 310. Where possible these types are compatible 
with the equivalent Joda Time - Hibernate user types. 

User Types also includes user types for Joda Time. These are designed for interoperability
wherever possible with the provided JSR 310 user types. These can be used as an alternative to Joda
Time Contrib's persistent types. The motivation for creating these types is the original types are
affected by an issue whereby the written time is offset by the user's offset from the database zone.
