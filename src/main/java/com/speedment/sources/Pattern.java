package com.speedment.sources;

import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;

/**
 * The basic methods required to generate a source file of a particular type.
 * 
 * @author Emil Forslund
 */
public interface Pattern {
    
    /**
     * The class name of the component to be generated. This should be the short
     * name of the class and should not include the package details.
     * 
     * @return  the (short) class name
     */
    String getClassName();
    
    /**
     * The full class name including the full package path separated by dots (.).
     * 
     * @return  the full absolute class name
     */
    String getFullClassName();
    
    /**
     * Generate a CodeGen model for the component. A file is included so that
     * any extra types can be imported correctly.
     * 
     * @param file  a reference to the file being created
     * @return      model of the component to be generated
     */
    ClassOrInterface<?> make(File file);
}
