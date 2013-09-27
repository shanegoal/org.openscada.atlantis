/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 * 
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */
package org.eclipse.scada.da.exec.configuration;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Splitter Extractor Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.SplitterExtractorType#getSplitExpression <em>Split Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getSplitterExtractorType()
 * @model extendedMetaData="name='SplitterExtractorType' kind='elementOnly'"
 * @generated
 */
public interface SplitterExtractorType extends FieldExtractorType
{

    /**
     * Returns the value of the '<em><b>Split Expression</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Split Expression</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Split Expression</em>' attribute.
     * @see #setSplitExpression(String)
     * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getSplitterExtractorType_SplitExpression()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='splitExpression'"
     * @generated
     */
    String getSplitExpression ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.exec.configuration.SplitterExtractorType#getSplitExpression <em>Split Expression</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Split Expression</em>' attribute.
     * @see #getSplitExpression()
     * @generated
     */
    void setSplitExpression ( String value );

} // SplitterExtractorType