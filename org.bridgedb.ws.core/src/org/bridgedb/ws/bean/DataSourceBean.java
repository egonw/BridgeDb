// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="DataSource")
@XmlType(propOrder={"sysCode","urlPattern", "mainUrl", "fullName", "idExample", "isPrimary", "organism", "urnBase", "type"})
public class DataSourceBean {
  	String sysCode;
	String fullName;
    String urlPattern;
	String idExample;
	boolean isPrimary;
	String type;
    //I wonder how to do this?
	Object organism;
	String urnBase;    
	String mainUrl;

    //Webservice constructor
    public DataSourceBean(){
    }

    /**
     * @return the sysCode
     */
    public String getSysCode() {
        return sysCode;
    }

    /**
     * @param sysCode the sysCode to set
     */
    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the urlPattern
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * @param urlPattern the urlPattern to set
     */
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    /**
     * @return the idExample
     */
    public String getIdExample() {
        return idExample;
    }

    /**
     * @param idExample the idExample to set
     */
    public void setIdExample(String idExample) {
        this.idExample = idExample;
    }

    /**
     * @return the isPrimary
     */
    public boolean isIsPrimary() {
        return isPrimary;
    }

    /**
     * @param isPrimary the isPrimary to set
     */
    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the organism
     */
    public Object getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Object organism) {
        this.organism = organism;
    }

    /**
     * @return the urnBase
     */
    public String getUrnBase() {
        return urnBase;
    }

    /**
     * @param urnBase the urnBase to set
     */
    public void setUrnBase(String urnBase) {
        this.urnBase = urnBase;
    }

    /**
     * @return the mainUrl
     */
    public String getMainUrl() {
        return mainUrl;
    }

    /**
     * @param mainUrl the mainUrl to set
     */
    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }
     
    public String toString(){
        return sysCode;
    }
}
