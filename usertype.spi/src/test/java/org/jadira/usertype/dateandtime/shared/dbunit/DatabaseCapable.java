/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.shared.dbunit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2Connection;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

public class DatabaseCapable {

    protected void verifyDatabaseTable(EntityManager manager, String tableName) throws RuntimeDatabaseUnitException {

        DBUnitWork work = new DBUnitWork();
        ((Session)(manager.getDelegate())).doWork(work);
        ITable actualTable;
        try {
            actualTable = work.getDatabaseDataSet().getTable(tableName);

            File placeholder = new File(DatabaseCapable.class.getResource("/expected/.dbunit-comparison-files").getFile());
            File comparisonFile = new File(placeholder.getParentFile().getPath() + System.getProperty("file.separator") + tableName + ".xml");

            // writeExpectedFile(work.getDbunitConnection(), comparisonFile, tableName);

            IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(comparisonFile);
            ITable expectedTable = expectedDataSet.getTable(tableName);
            
            Assertion.assertEquals(expectedTable, actualTable);
            
        } catch (DatabaseUnitException e) {
            throw new RuntimeDatabaseUnitException(e);
        } catch (IOException e) {
            throw new RuntimeDatabaseUnitException(e);
        }
    }

    protected void writeExpectedFile(IDatabaseConnection dbunitConnection, File outputFile, String tableName) throws AmbiguousTableNameException, IOException, DataSetException, FileNotFoundException {
        QueryDataSet partialDataSet = new QueryDataSet(dbunitConnection);
        partialDataSet.addTable(tableName);
        // IDataSet export = work.getDatabaseDataSet(); // Full dataset

        FlatXmlDataSet.write(partialDataSet, new FileOutputStream(outputFile));
    }
        
    protected static class DBUnitWork implements Work {

        IDatabaseConnection dbunitConnection;
        
        private IDataSet databaseDataSet;

        public DBUnitWork() {
        }

        public void execute(Connection connection) throws SQLException {
            try {
                dbunitConnection = new H2Connection(connection, null);
                databaseDataSet = dbunitConnection.createDataSet();
            } catch (DatabaseUnitException e) {
                throw new RuntimeException(e);
            }
        }
        
        public IDataSet getDatabaseDataSet() {
            return databaseDataSet;
        }
        
        public IDatabaseConnection getDbunitConnection() {
            return dbunitConnection;
        }
    }
}
