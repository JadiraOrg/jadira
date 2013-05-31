/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.maven.plugins.fmpp;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import fmpp.ProcessingException;
import fmpp.progresslisteners.ConsoleProgressListener;
import fmpp.setting.SettingException;
import fmpp.setting.Settings;
import fmpp.util.MiscUtil;

/**
 * A Maven front-end for FMPP. Inspired by Faisal Feroz' FMPP-Maven-Plugin, this implementation
 * gives the same general behaviour, but also allows configuration of whether the generated sources are
 * added to src or test. The default configuration adds the generated-sources to compile scope (not test)
 * @goal generate
 * @phase generate-sources
 */
public class FmppMojo extends AbstractMojo {

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 **/
	private MavenProject project;

	/**
	 * If true, the generated sources are added as a source compilation root.
	 * @parameter property="compileSourceRoot" default-value="true"
	 */
	private boolean compileSourceRoot;

	/**
	 * If true, the generated sources are added as a test-source compilation root.
	 * @parameter property="testCompileSourceRoot" default-value="false"
	 */
	private boolean testCompileSourceRoot;

	/**
	 * The location of the FreeMarker configuration file to use.
	 * @parameter property="cfgFile" default-value="src/main/resources/fmpp/config.fmpp"
	 * @required
	 */
	private File cfgFile;

	/**
	 * The location where the FreeMarker template files to be processed are stored.
	 * 
	 * @parameter property="templateDirectory" default-value="src/main/resources/fmpp/templates/"
	 * @required
	 */
	private File templateDirectory;

	/**
	 * The directory where generated sources should be output
	 * @parameter property="outputDirectory"
	 *            default-value="${project.build.directory}/generated-sources/fmpp/"
	 * @required
	 */
	private File outputDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {

		checkParameters();

		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		getLog().info("Beginning FMPP Processing... ");

		try {
			Settings settings = new Settings(new File("."));

			settings.set("sourceRoot", templateDirectory.getAbsolutePath());
			settings.set("outputRoot", outputDirectory.getAbsolutePath());

			settings.load(cfgFile);

			settings.addProgressListener(new ConsoleProgressListener());
			settings.execute();
		} catch (SettingException e) {
			handleError(e);
		} catch (ProcessingException e) {
			handleError(e);
		}

		getLog().info("Successfully completed FMPP Processing... ");

		if (compileSourceRoot) {
			project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
		}
		if (testCompileSourceRoot) {
			project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
		}
	}

	private void handleError(Exception e) throws MojoFailureException {

		getLog().error(MiscUtil.causeMessages(e));
		throw new MojoFailureException(MiscUtil.causeMessages(e), e);
	}

	private void checkParameters() throws MojoExecutionException {

		if (project == null) {
			throw new MojoExecutionException("This plugin can only be used as part of a Maven project.");
		}
		if (cfgFile == null) {
			throw new MojoExecutionException("configFile is a required parameter");
		}
		if (templateDirectory == null) {
			throw new MojoExecutionException("templateDirectory is a required parameter");
		}
		if (outputDirectory == null) {
			throw new MojoExecutionException("outputDirectory is a required parameter");
		}
	}
}
