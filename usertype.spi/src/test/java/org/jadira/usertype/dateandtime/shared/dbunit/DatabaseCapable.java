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
package org.jadira.usertype.dateandtime.shared.dbunit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.ext.oracle.OracleConnection;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.spi.JdbcWrapper;
import org.hibernate.jdbc.Work;

public class DatabaseCapable {

	protected void verifyDatabaseTable(final EntityManager manager, final String tableName) throws RuntimeDatabaseUnitException {

		Work work = new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				final Connection wrappedConnection;
				if (connection instanceof JdbcWrapper) {
				        wrappedConnection = (Connection) ((JdbcWrapper<?>) connection).getWrappedObject();
				} else {
				        wrappedConnection = connection;
				}
				
				IDataSet databaseDataSet;
				DatabaseConnection dbunitConnection;
				try { 
					if (wrappedConnection.getClass().getName().equals("oracle.jdbc.driver.T4CConnection")) {
						dbunitConnection = new OracleConnection(wrappedConnection, "chris");
					} else {
						dbunitConnection = new H2Connection(wrappedConnection, null);
					}
					databaseDataSet = dbunitConnection.createDataSet();
				} catch (DatabaseUnitException ex) {
					throw new RuntimeException(ex);
				}

				ITable actualTable;
				try {
					actualTable = databaseDataSet.getTable(tableName);

					File placeholder = new File(DatabaseCapable.class
							.getResource("/expected/.dbunit-comparison-files")
							.getFile());
					File comparisonFile = new File(placeholder.getParentFile()
							.getPath()
							+ System.getProperty("file.separator")
							+ tableName + ".xml");

					
					// writeDtd(dbunitConnection, tableName);
					// writeExpectedFile(dbunitConnection, comparisonFile, tableName);
					
					IDataSet expectedDataSet = new FlatXmlDataSetBuilder()
							.build(comparisonFile);
					ITable expectedTable = expectedDataSet.getTable(tableName);

					Assertion.assertEquals(expectedTable, actualTable);

				} catch (DatabaseUnitException ex) {
					throw new RuntimeDatabaseUnitException(ex);
				} catch (IOException ex) {
					throw new RuntimeDatabaseUnitException(ex);
				}
			}
		};

		((Session) (manager.getDelegate())).doWork(work);

	}

	protected void writeExpectedFile(IDatabaseConnection dbunitConnection,
			File outputFile, String tableName) throws IOException,
			DataSetException {

		QueryDataSet partialDataSet = new QueryDataSet(dbunitConnection);
		partialDataSet.addTable(tableName);

		FlatXmlDataSet.write(partialDataSet, new FileOutputStream(outputFile));
	}
	
	protected void writeDtd(IDatabaseConnection dbunitConnection,String tableName) throws IOException,
			DataSetException, SQLException {
		
		File placeholder = new File(DatabaseCapable.class
				.getResource("/expected/.dbunit-comparison-files")
				.getFile());
		
		File dtdFile = new File(placeholder.getParentFile()
				.getPath()
				+ System.getProperty("file.separator")
				+ tableName + ".dtd");
		
        FlatDtdDataSet.write(dbunitConnection.createDataSet(new String[] { tableName } ), new FileOutputStream(dtdFile));
	}

}
