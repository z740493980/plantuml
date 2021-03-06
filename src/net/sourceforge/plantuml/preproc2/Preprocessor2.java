/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.preproc2;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.AParentFolder;
import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.DefinitionsContainer;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.IfManagerFilter;
import net.sourceforge.plantuml.preproc.ImportedFiles;
import net.sourceforge.plantuml.preproc.ReadLine;
import net.sourceforge.plantuml.preproc.ReadLineNumbered;

public class Preprocessor2 implements ReadLineNumbered {

	private final ReadLine source;
	private final PreprocessorInclude3 include;

	// public Preprocessor2(ReadLine reader, String charset, Defines defines, AParentFolder newCurrentDir,
	// DefinitionsContainer definitionsContainer) throws IOException {
	// this(Collections.<String> emptyList(), reader, charset, defines, newCurrentDir, definitionsContainer);
	// }
	//
	// public Preprocessor2(List<String> config, ReadLine reader, String charset, Defines defines, File newCurrentDir,
	// DefinitionsContainer definitionsContainer) throws IOException {
	// this(config, reader, charset, defines, new AParentFolderRegular(newCurrentDir), definitionsContainer);
	// }

	public Preprocessor2(List<String> config, ReadLine reader, String charset, Defines defines,
			AParentFolder newCurrentDir, DefinitionsContainer definitionsContainer) throws IOException {
		this(config, reader, charset, defines, newCurrentDir, definitionsContainer, new HashSet<FileWithSuffix>());
	}

	public Preprocessor2(List<String> config, ReadLine reader, String charset, Defines defines,
			AParentFolder newCurrentDir, DefinitionsContainer definitionsContainer, Set<FileWithSuffix> filesUsedGlobal)
			throws IOException {

		final ImportedFiles importedFiles = new ImportedFiles();
		if (newCurrentDir != null) {
			importedFiles.setCurrentDir(newCurrentDir);
		}

		final ReadFilterAnd2 filters = new ReadFilterAnd2();

		filters.add(new ReadLineQuoteComment2());
		include = new PreprocessorInclude3(config, charset, defines, definitionsContainer, importedFiles,
				filesUsedGlobal);
		filters.add(new ReadLineAddConfig2(config));
		filters.add(new IfManagerFilter(defines));
		filters.add(new PreprocessorDefine4Apply(defines));
		filters.add(new SubPreprocessor2(charset, defines, definitionsContainer));
		filters.add(new PreprocessorDefine3Learner(defines));
		filters.add(include);

		this.source = filters.applyFilter(reader);
	}

	public CharSequence2 readLine() throws IOException {
		return source.readLine();
	}

	public void close() throws IOException {
		this.source.close();
	}

	public Set<FileWithSuffix> getFilesUsed() {
		// System.err.println("************************** WARNING **************************");
		// return Collections.emptySet();
		return Collections.unmodifiableSet(include.getFilesUsedGlobal());
	}
}