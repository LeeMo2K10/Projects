//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.28 at 08:04:28 PM IST 
//


package com.biz.rbundle.xsdFirstEExample;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Emp_Father" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Emp_Mother" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "empFather",
    "empMother"
})
public class EmpParent
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Emp_Father", required = true)
    protected String empFather;
    @XmlElement(name = "Emp_Mother", required = true)
    protected String empMother;

    /**
     * Gets the value of the empFather property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpFather() {
        return empFather;
    }

    /**
     * Sets the value of the empFather property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpFather(String value) {
        this.empFather = value;
    }

    /**
     * Gets the value of the empMother property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpMother() {
        return empMother;
    }

    /**
     * Sets the value of the empMother property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpMother(String value) {
        this.empMother = value;
    }

}
