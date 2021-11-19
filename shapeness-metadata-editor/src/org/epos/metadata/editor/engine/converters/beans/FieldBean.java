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
package org.epos.metadata.editor.engine.converters.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.epos.metadata.editor.engine.Engine;
import org.epos.metadata.editor.engine.converters.methods.ClassTypeIdentifier;
import org.epos.metadata.editor.engine.converters.methods.GenericSignature;
import org.epos.metadata.editor.engine.converters.methods.Serializer;
import org.epos.metadata.editor.engine.utils.Shacl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class FieldBean {

	private Logger logger = Logger.getGlobal();

	private ArrayList<String> type;
	private String name;
	private String orginalName;
	private String fieldName;
	private Integer minCount;
	private Integer maxCount;
	private Boolean warning;
	private Boolean isList;
	private ArrayList<String> namespace;
	private String path_namespace;


	private boolean mandatory;
	private boolean recommended;
	private boolean optional;

	public FieldBean() {
		this.type = new ArrayList<String>();
		this.namespace = new ArrayList<String>();
		this.isList = false;
	}

	/**
	 * @return the type
	 */
	public ArrayList<String> getType(){
		type = new ArrayList<String>(new HashSet<String>(type));
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ArrayList<String> type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the minCount
	 */
	public Integer getMinCount() {
		return minCount;
	}

	/**
	 * @param minCount the minCount to set
	 */
	public void setMinCount(Integer minCount) {
		this.minCount = minCount;
	}

	/**
	 * @return the maxCount
	 */
	public Integer getMaxCount() {
		return maxCount;
	}

	/**
	 * @param maxCount the maxCount to set
	 */
	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}

	/**
	 * @return the warning
	 */
	public Boolean isWarning() {
		return warning;
	}

	/**
	 * @param warning the warning to set
	 */
	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	/**
	 * @return the isList
	 */
	public Boolean isList() {
		return isList;
	}

	/**
	 * @param isList the isList to set
	 */
	public void setList(boolean isList) {
		this.isList = isList;
	}

	/**
	 * @return the namespace
	 */
	public ArrayList<String> getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(ArrayList<String> namespace) {
		this.namespace = namespace;
	}


	public void generateCtField(CtClass cc, ArrayList<CtClass> typef, Serializer serializer, HashMap<String,ArrayList<String>> ns, ArrayList<CtClass> subClasses){

		ClassPool pool = ClassPool.getDefault();

		String level = "Optional";

		if(typef!=null && !typef.isEmpty()) {
			// BUILD FIELD
			CtField field = null;

			//if(this.isWarning()!=null && this.isWarning()) //System.out.println(this.toString());

			/*****
			 * 
			 * 
			 * UPDATE MANU TODO:
			 * 
			 * Mandatory: mincount>=1, maxcount >=mincount || maxcount==null
			 * 
			 * Optional: maxcount==null || maxcount!=null && mincount ==null
			 * 
			 * Recommended: sh:Warning, minCount==1
			 * 
			 * 
			 */
			// MINCOUNT=NULL MAXCOUNT = NULL WARNING = FALSE --> LIST OPTIONAL V
			// MINCOUNT=NULL MAXCOUNT = NULL WARNING = TRUE --> LIST RECOMMENDED V

			// MINCOUNT=1 MAXCOUNT = NULL WARNING = FALSE --> LIST MANDATORY
			// MINCOUNT=1 MAXCOUNT = NULL WARNING = TRUE --> LIST RECOMMENDED

			// MINCOUNT=1 MAXCOUNT = MINCOUNT WARNING = FALSE --> SINGLE MANDATORY
			// MINCOUNT=1 MAXCOUNT = MINCOUNT WARNING = TRUE --> SINGLE RECOMMENDED

			// MINCOUNT=NULL MAXCOUNT = 1 WARNING = FALSE --> SINGLE OPTIONAL V
			// MINCOUNT=NULL MAXCOUNT = 1 WARNING = TRUE --> SINGLE RECOMMENDED V

			//LIST OR NOT LIST

			if((this.getMinCount()==null && this.getMaxCount()==null) 
					|| (this.getMaxCount()!=null && this.getMaxCount()>1) 
					|| (this.getMinCount()!=null && this.getMaxCount()!=null && this.getMaxCount()>this.getMinCount())
					|| (this.getMinCount()!=null && this.getMaxCount()==null)) {
				isList = true;
			}


			//check mandatory or optional or recommended
			if(this.isWarning()!=null && this.isWarning()) {
				this.recommended=true;
				this.mandatory = false;
				this.optional = false;
			}
			else {
				if(this.getMinCount()==null || (this.getMinCount()!=null && this.getMinCount()<1)) this.optional = true;
				if(this.getMinCount()!=null && this.getMinCount()>0) this.mandatory = true;
			}

			CtClass fieldClass = null;
			try {
				fieldClass = typef.size()>1? pool.get("java.lang.Object") : typef.get(0);
			} catch (NotFoundException e4) {
				e4.printStackTrace();
			}

			try {
				if(this.isList) {
					field = new CtField(pool.get(List.class.getCanonicalName()), this.getFieldName(), cc);
					field.setGenericSignature(GenericSignature.getGenericSignature(fieldClass));
				}
				else field = new CtField(fieldClass, this.getFieldName(), cc);

				if(recommended) level = "Recommended";
				if(mandatory) level = "Mandatory";


			} catch (CannotCompileException | NotFoundException | BadBytecode e3) {
				logger.severe(e3.getLocalizedMessage()+" "+e3.getCause());
			}

			if(this.isList()) {
				try {
					field = new CtField(pool.get(List.class.getCanonicalName()), this.getFieldName(), cc);
				} catch (CannotCompileException | NotFoundException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}
				try {
					field.setGenericSignature(GenericSignature.getGenericSignature(fieldClass));
				} catch (BadBytecode e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}
			} else {
				try {
					field = new CtField(fieldClass, this.getFieldName(), cc);
				} catch (CannotCompileException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}
			}

			/* ADD SHACL ATTRIBUTE PROPERTY */
			ArrayList<String> schema = new ArrayList<String>();
			if(this.getPath_namespace()==null) schema = this.getNamespace();
			else schema.add(this.getPath_namespace());

			//System.out.println("FIELD BEAN AKA: "+orginalName+"   "+schema);

			field.getFieldInfo().addAttribute(createFieldAttribute(cc, orginalName, schema, level, this));

			handleFieldAdd(field, cc);


			//if(type.size()==1) {
			if(typef.size()==1 && !this.isList()) {
				try {
					handleMethodAdd(CtNewMethod.getter("get"+this.getFieldName(), field),cc);
					handleMethodAdd(CtNewMethod.setter("set"+this.getFieldName(), field),cc);
				} catch (CannotCompileException e2) {
					logger.severe(e2.getLocalizedMessage()+" "+e2.getCause());
				}
				/*try {
					if(path_namespace!=null) serializer.addToSerializer(field, 1, ns, path_namespace+orginalName);
					else if(namespace.size()==1) serializer.addToSerializer(field, 1, ns, namespace.get(0)+orginalName);
					else serializer.addToSerializer(field, 1, ns, namespace.get(0)+orginalName);
				} catch (NotFoundException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}*/
			}
			else if(typef.size()==1 && this.isList()){
				try {
					handleMethodAdd(CtNewMethod.getter("get"+this.getFieldName(), field),cc);
				} catch (CannotCompileException e2) {
					e2.printStackTrace();
				}
				CtMethod m = null;
				try {
					m = CtNewMethod.make("public void add"+this.getFieldName()+"("+typef.get(0).getName()+" arg0){\n" + 
							"		((java.util.ArrayList) this.get"+this.getFieldName()+"()).add(arg0); \n" + 
							"	}", cc);
				} catch (CannotCompileException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}
				handleMethodAdd(m,cc);
				//try {
					/*if(namespace.size()==1) serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);
					else serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);*/
					/*if(path_namespace!=null) serializer.addToSerializer(field, 2, ns, path_namespace+orginalName);
					else if(namespace.size()==1) serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);
					else serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);
				} catch (NotFoundException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}*/
			}
			else if(typef.size()>1 && !this.isList) {
				try {
					handleMethodAdd(CtNewMethod.getter("get"+this.getFieldName(), field),cc);
				} catch (CannotCompileException e2) {
					e2.printStackTrace();
				}
				for(CtClass type_sub : typef) {
					if(this.isList()) {
						CtMethod m = null;
						try {
							m = CtNewMethod.make("public void add"+this.getFieldName()+"("+type_sub.getName()+" arg0){\n" + 
									//"		if("+cc.getField(this.getFieldName()).getName()+"==null) { \n" + 
									//"			this.get"+this.getFieldName()+"().add(new java.util.ArrayList());\n" + 
									//"		} \n" + 
									"		this.get"+this.getFieldName()+"().add(arg0); \n" + 
									"	}", cc);
						} catch (CannotCompileException e) {
							logger.severe(e.getLocalizedMessage()+" "+e.getCause());
						}
						handleMethodAdd(m,cc);
					}
					else{
						CtMethod m = null;
						try {
							m = CtNewMethod.make("public void set"+this.getFieldName()+"("+type_sub.getName()+" arg0){\n" + 
									"			this."+cc.getField(this.getFieldName()).getName()+" = arg0; \n" + 
									"	}", cc);
						} catch (CannotCompileException | NotFoundException e) {
							logger.severe(e.getLocalizedMessage()+" "+e.getCause());
						}
						handleMethodAdd(m,cc);
					}

				}
				//try {
					/*if(namespace.size()==1) serializer.addToSerializer(field, 3, ns, namespace.get(0)+orginalName);
					else serializer.addToSerializer(field, 3, ns, namespace.get(0)+orginalName);*/
					
				/*	if(path_namespace!=null) serializer.addToSerializer(field, 3, ns, path_namespace+orginalName);
					else if(namespace.size()==1) serializer.addToSerializer(field, 3, ns, namespace.get(0)+orginalName);
					else serializer.addToSerializer(field, 3, ns, namespace.get(0)+orginalName);
				} catch (NotFoundException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}*/
			}
			else if(typef.size()>1 && this.isList) {
				try {
					handleMethodAdd(CtNewMethod.getter("get"+this.getFieldName(), field),cc);
				} catch (CannotCompileException e1) {
					logger.severe(e1.getLocalizedMessage()+" "+e1.getCause());
				}

				for(CtClass type_sub : typef) {
					if(this.isList()) {
						////System.out.println(type_sub);
						CtMethod m = null;
						try {
							m = CtNewMethod.make("public void add"+this.getFieldName()+"("+type_sub.getName()+" arg0){\n" + 
									"		((java.util.ArrayList) this.get"+this.getFieldName()+"()).add(arg0); \n" + 
									"	}", cc);
						} catch (CannotCompileException e) {
							logger.severe("FUKK SLEEP "+e.getLocalizedMessage()+" "+e.getCause());
							e.printStackTrace();
						}

						handleMethodAdd(m,cc);

					}
					else{
						CtMethod m = null;
						try {
							m = CtNewMethod.make("public void set"+this.getFieldName()+"("+type_sub.getName()+" arg0){\n" + 
									"			this."+cc.getField(this.getFieldName()).getName()+" = arg0; \n" + 
									"	}", cc);
						} catch (CannotCompileException | NotFoundException e) {
							logger.severe(e.getLocalizedMessage()+" "+e.getCause());
						}
						handleMethodAdd(m,cc);
					}

				}
				//try {
					/*if(namespace.size()==1) serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);
					else serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);*/
					/*if(path_namespace!=null) serializer.addToSerializer(field, 2, ns, path_namespace+orginalName);
					else if(namespace.size()==1) serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);
					else serializer.addToSerializer(field, 2, ns, namespace.get(0)+orginalName);
				} catch (NotFoundException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}*/
			}   

		}
		
		////System.out.println(serializer);

	}


	private void handleFieldAdd(CtField f, CtClass cc) {
		for(CtField ctf : cc.getFields()) {
			if(ctf.getName().equals(f.getName())) {
				try {
					logger.info("Remove old field "+f.getName());
					cc.removeField(ctf);
				} catch (NotFoundException e) {
					logger.severe(e.getLocalizedMessage()+" "+e.getCause());
				}
			}
		}
		try {
			cc.addField(f);
		} catch (CannotCompileException e2) {
			e2.printStackTrace();
		}
	}

	private void handleMethodAdd(CtMethod m, CtClass cc) {
		for(CtMethod ctm : cc.getMethods()) {
			try {
				if(ctm.getName().equals(m.getName()) && ctm.getParameterTypes().equals(m.getParameterTypes())) {
					try {
						logger.info("Remove old method "+m.getName());
						cc.removeMethod(ctm);
					} catch (NotFoundException e) {
						logger.severe(e.getLocalizedMessage()+" "+e.getCause());
					}
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
				logger.severe(e.getLocalizedMessage()+" "+e.getCause());
			}
		}
		try {
			cc.addMethod(m);
		} catch (CannotCompileException e2) {
			logger.severe(e2.getLocalizedMessage()+" "+e2.getCause());
		}
	}

	public static AnnotationsAttribute createFieldAttribute(CtClass cc, String name, ArrayList<String> schema, String level, FieldBean obj) {

		ClassFile cfile = cc.getClassFile();
		ConstPool cpool = cfile.getConstPool();
		Annotation annot = new Annotation(Shacl.class.getName(),cpool);
		annot.addMemberValue("term", new StringMemberValue(name, cpool));
		annot.addMemberValue("vocabulary", new StringMemberValue(schema.get(0), cpool));
		annot.addMemberValue("shortVocabulary", new StringMemberValue(Engine.getIstance().getMs().getShortNS(schema.get(0)), cpool));
		if(schema.size()>1) annot.addMemberValue("altVocabulary", new StringMemberValue(schema.get(1), cpool));
		else annot.addMemberValue("altVocabulary", new StringMemberValue("null", cpool));
		annot.addMemberValue("constraint", new StringMemberValue(level, cpool));

		Annotation annotHib = null;

		if(level.equals("Mandatory")) {
			if(obj.isList()) {
				annotHib = new Annotation(javax.validation.constraints.NotEmpty.class.getName(),cpool);
			}else {
				annotHib = new Annotation(javax.validation.constraints.NotNull.class.getName(),cpool);
			}
		}

		AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		if(annotHib!=null) attr.setAnnotations(new Annotation[]{annot, annotHib});
		else attr.setAnnotations(new Annotation[]{annot});

		return attr;
	}

	public static AnnotationsAttribute addHibernateValidatorAttributes(CtClass cc, boolean isList) {
		ClassFile cfile = cc.getClassFile();
		ConstPool cpool = cfile.getConstPool();

		if(isList) {
			Annotation annot = new Annotation(javax.validation.constraints.NotEmpty.class.getName(),cpool);
			AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
			//attr.addAnnotation(annot);
			attr.setAnnotations(new Annotation[]{annot});
			return attr;
		}else {
			Annotation annot = new Annotation(javax.validation.constraints.NotNull.class.getName(),cpool);
			AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
			attr.setAnnotations(new Annotation[]{annot});
			return attr;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + (isList ? 1231 : 1237);
		result = prime * result + ((logger == null) ? 0 : logger.hashCode());
		result = prime * result + ((maxCount == null) ? 0 : maxCount.hashCode());
		result = prime * result + ((minCount == null) ? 0 : minCount.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (warning ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldBean other = (FieldBean) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (isList != other.isList)
			return false;
		if (logger == null) {
			if (other.logger != null)
				return false;
		} else if (!logger.equals(other.logger))
			return false;
		if (maxCount == null) {
			if (other.maxCount != null)
				return false;
		} else if (!maxCount.equals(other.maxCount))
			return false;
		if (minCount == null) {
			if (other.minCount != null)
				return false;
		} else if (!minCount.equals(other.minCount))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (warning != other.warning)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FieldBean [logger=" + logger + ", type=" + type + ", name=" + name + ", fieldName=" + fieldName
				+ ", minCount=" + minCount + ", maxCount=" + maxCount + ", warning=" + warning + ", isList=" + isList
				+ ", namespace=" + namespace + "]";
	}

	public String getOrginalName() {
		return orginalName;
	}

	public void setOrginalName(String orginalName) {
		this.orginalName = orginalName;
	}

	public String getPath_namespace() {
		return path_namespace;
	}

	public void setPath_namespace(String path_namespace) {
		this.path_namespace = path_namespace;
	}

}
