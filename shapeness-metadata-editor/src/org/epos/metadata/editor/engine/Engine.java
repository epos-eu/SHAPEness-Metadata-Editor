/*******************************************************************************
 * <one line to give the program's name and a brief idea of what it does.>
 *     
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.epos.metadata.editor.engine;

import java.io.IOException;

import org.epos.metadata.editor.engine.converters.Classes2Model;
import org.epos.metadata.editor.engine.converters.Model2Classes;
import org.epos.metadata.editor.engine.exceptions.EngineBuilderException;
import org.epos.metadata.editor.engine.io.ModelReader;
import org.epos.metadata.editor.engine.io.FileReader;
import org.epos.metadata.editor.engine.io.FileWriter;
import org.epos.metadata.editor.engine.model.ModelStored;

public class Engine {

	private static volatile Engine istance = null;

	private ModelReader reader;
	private Model2Classes m2c;
	private Classes2Model c2m;
	private ModelStored ms;
	private FileWriter writer;
	private FileReader freader;
	private VocabularyStore vocStore;

	public static Engine getIstance() {
		if(istance==null) new Exception("Impossible to find an Engine instance");
		return istance;
	}

	private Engine(EngineBuilder engineBuilder) throws IOException {
		this.reader = engineBuilder.reader;
		this.m2c = engineBuilder.m2c;
		this.c2m = engineBuilder.c2m;
		this.ms = engineBuilder.ms;
		this.writer = engineBuilder.writer;
		this.freader = engineBuilder.freader;
		this.vocStore = engineBuilder.vocStore;
		
		
		istance = this;

		
		this.reader.readSHACLModel(ms);
		

		this.m2c.convert(ms);
		this.c2m.setModel(ms);

		
		//this.vocStore.initializeStore(ms.getShaclModel().getNsPrefixMap());
	}

	/**
	 * @return the reader
	 */
	public ModelReader getReader() {
		return reader;
	}

	/**
	 * @param reader the reader to set
	 */
	public void setReader(ModelReader reader) {
		this.reader = reader;
	}

	/**
	 * @return the m2c
	 */
	public Model2Classes getM2c() {
		return m2c;
	}

	/**
	 * @param m2c the m2c to set
	 */
	public void setM2c(Model2Classes m2c) {
		this.m2c = m2c;
	}

	/**
	 * @return the ms
	 */
	public ModelStored getMs() {
		return ms;
	}

	/**
	 * @param ms the ms to set
	 */
	public void setMs(ModelStored ms) {
		this.ms = ms;
	}

	public FileWriter getWriter() {
		return writer;
	}

	public void setWriter(FileWriter writer) {
		this.writer = writer;
	}


	public FileReader getFreader() {
		return freader;
	}

	public void setFreader(FileReader freader) {
		this.freader = freader;
	}


	public Classes2Model getC2m() {
		return c2m;
	}

	public void setC2m(Classes2Model c2m) {
		this.c2m = c2m;
	}


	public VocabularyStore getVocStore() {
		return vocStore;
	}

	public void setVocStore(VocabularyStore vocStore) {
		this.vocStore = vocStore;
	}


	public static class EngineBuilder{

		private Classes2Model c2m;
		private ModelReader reader;
		private Model2Classes m2c;
		private ModelStored ms;
		private FileReader freader;
		private FileWriter writer;
		private VocabularyStore vocStore;

		public EngineBuilder setModelReader(ModelReader reader) {
			this.reader = reader;
			return this;
		}
		public EngineBuilder setFileWriter(FileWriter writer) {
			this.writer = writer;
			return this;
		}
		public EngineBuilder setModel2Classes(Model2Classes m2c) {
			this.m2c = m2c;
			return this;
		}
		public EngineBuilder setClasses2Model(Classes2Model c2m) {
			this.c2m = c2m;
			return this;
		}
		public EngineBuilder setModelStored(ModelStored ms) {
			this.ms = ms;
			return this;
		}
		public EngineBuilder setFileReader(FileReader freader) {
			this.freader=freader;
			return this;
		}
		public EngineBuilder setVocabularyStore(VocabularyStore vocStore) {
			this.vocStore=vocStore;
			return this;
		}

		public Engine build() throws IOException {
			if(this.reader==null) new Exception("Impossible to find a Reader instance");
			if(this.freader==null) new Exception("Impossible to find a FileReader instance");
			if(this.writer==null) new Exception("Impossible to find a Writer instance");
			if(this.m2c==null) new Exception("Impossible to find a Model2Classes instance");
			if(this.c2m==null) new Exception("Impossible to find a Classes2Model instance");
			if(this.ms==null) new Exception("Impossible to find a ModelStored instance");
			if(this.vocStore==null) new Exception("Impossible to find a VocabularyStore instance");
			return new Engine(this);
		}

	}
}
