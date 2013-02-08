// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.uri.Mapping;
import org.bridgedb.uri.UriMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.bean.DataSourceUriPatternBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.bean.ProfileBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;

/**
 *
 * @author Christian
 */
public class WSUriMapper extends WSCoreMapper implements UriMapper{
    
    WSUriInterface uriService;
    private static final ArrayList<String> NO_SYSCODES = null;
    private static final ArrayList<String> NO_URI_PATTERNS = null;
    
    
    public WSUriMapper(WSUriInterface uriService){
        super(uriService);
        this.uriService = uriService;
    }

    @Override
    public Set<Xref> mapID(Xref sourceXref, String profileUri, DataSource... tgtDataSources) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceXref, profileUri, tgtDataSources);
        return extractXref(beans);
    }
    
    private Set<Xref> extractXref(Collection<Mapping> beans){
        HashSet<Xref> results = new HashSet<Xref>();
        for (Mapping bean:beans){
           DataSource targetDataSource = DataSource.getBySystemCode(bean.getTargetSysCode());
           Xref targetXref = new Xref(bean.getTargetId(), targetDataSource);
           results.add(targetXref);
        }
        return results;        
    }
    
    @Override
    public Set<Xref> mapID(Xref sourceXref, String profileUri, DataSource tgtDataSource) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceXref, profileUri);
        return extractXref(beans);
    }

    @Override
    public Set<Xref> mapID(Xref sourceXref, String profileUri) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceXref, profileUri);
        return extractXref(beans);
    }

    @Override
    public Set<String> mapUri(String sourceUri, String profileUri, UriPattern... tgtUriPatterns) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceUri, profileUri, tgtUriPatterns);
        return extractUris(beans);
     }

    private Set<String> extractUris(Collection<Mapping> beans){
        HashSet<String> results = new HashSet<String>();
        for (Mapping bean:beans){
            results.addAll(bean.getTargetUri());
        }
        return results;          
    }
    
    @Override
    public Set<String> mapUri(Xref sourceXref, String profileUri, UriPattern... tgtUriPatterns) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceXref, profileUri, tgtUriPatterns);
        return extractUris(beans);
    }

    @Override
    public Set<String> mapUri(Xref sourceXref, String profileUri, UriPattern tgtUriPattern) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceXref, profileUri, tgtUriPattern);
        return extractUris(beans);
    }

    @Override
    public Set<String> mapUri(Xref sourceXref, String profileUri) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceXref, profileUri);
        return extractUris(beans);
    }

    @Override
    public Set<String> mapUri(String sourceUri, String profileUri, UriPattern tgtUriPattern) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceUri, profileUri, tgtUriPattern);
        return extractUris(beans);
    }

    @Override
    public Set<String> mapUri(String sourceUri, String profileUri) throws BridgeDBException {
        Collection<Mapping> beans = mapFull(sourceUri, profileUri);
        return extractUris(beans);
    }

    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String profileUri, DataSource... tgtDataSources) 
            throws BridgeDBException {
        if (tgtDataSources == null){
            return mapFull(sourceXref, profileUri);
        }
        if (sourceXref == null){
            return new HashSet<Mapping>();
        }
        ArrayList<String> tgtSysCodes = new ArrayList<String>();
        for (int i = 0 ; i < tgtDataSources.length; i++){
            if (tgtDataSources[i] != null){
                tgtSysCodes.add(tgtDataSources[i].getSystemCode());
            }
        }
        List<Mapping> beans = uriService.map(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                profileUri, tgtSysCodes, NO_URI_PATTERNS);
        return new HashSet<Mapping>(beans); 
    }
 
    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String profileUri, UriPattern... tgtUriPatterns)
            throws BridgeDBException {
        if (tgtUriPatterns == null){
            return mapFull(sourceXref, profileUri);
        }
        ArrayList<String> tgtUriPatternStrings = new ArrayList<String>();
        for (UriPattern tgtUriPattern:tgtUriPatterns){
            if (tgtUriPattern != null){
                tgtUriPatternStrings.add(tgtUriPattern.getUriPattern());
            }
        }
        List<Mapping> beans = uriService.map(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                profileUri, NO_SYSCODES, tgtUriPatternStrings);
        return new HashSet<Mapping>(beans); 
    }
 
    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String profileUri, DataSource tgtDataSource) throws BridgeDBException {
        DataSource[] tgtDataSources = new DataSource[1];
        tgtDataSources[0] = tgtDataSource;
        return mapFull(sourceXref, profileUri, tgtDataSource);
    }

    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String profileUri) throws BridgeDBException {
        if (sourceXref == null){
            return new HashSet<Mapping>();
        }
        List<Mapping> beans = uriService.map(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                profileUri, NO_SYSCODES, NO_URI_PATTERNS);
        return new HashSet<Mapping>(beans); 
    }

    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String profileUri, UriPattern tgtUriPattern) throws BridgeDBException {
        UriPattern[] tgtUriPatterns = new UriPattern[1];
        tgtUriPatterns[0] = tgtUriPattern;
        return mapFull(sourceXref, profileUri, tgtUriPatterns);
    }

    @Override
    public Set<Mapping> mapFull(String sourceUri, String profileUri, DataSource... tgtDataSources) throws BridgeDBException {
        if (tgtDataSources == null){
            return mapFull(sourceUri, profileUri);
        }
        ArrayList<String> tgtSysCodes = new ArrayList<String>();
        for (int i = 0 ; i < tgtDataSources.length; i++){
            if (tgtDataSources[i] != null){
                tgtSysCodes.add(tgtDataSources[i].getSystemCode());
            }
        }
        List<Mapping> beans = uriService.map(sourceUri, profileUri, tgtSysCodes, NO_URI_PATTERNS);
        return new HashSet<Mapping>(beans); 
    }

    @Override
    public Set<Mapping> mapFull(String sourceUri, String profileUri, DataSource tgtDataSource) throws BridgeDBException {
        DataSource[] tgtDataSources = new DataSource[1];
        tgtDataSources[0] = tgtDataSource;
        return mapFull(sourceUri, profileUri, tgtDataSource);
    }

    @Override
    public Set<Mapping> mapFull(String sourceUri, String profileUri) throws BridgeDBException {
        if (sourceUri == null){
            return new HashSet<Mapping>();
        }
        List<Mapping> beans = uriService.map(sourceUri, profileUri, NO_SYSCODES, NO_URI_PATTERNS);
        return new HashSet<Mapping>(beans); 
    }

    @Override
    public Set<Mapping> mapFull(String sourceUri, String profileUri, UriPattern tgtUriPattern) throws BridgeDBException {
        UriPattern[] tgtUriPatterns = new UriPattern[1];
        tgtUriPatterns[0] = tgtUriPattern;
        return mapFull(sourceUri, profileUri, tgtUriPatterns);
    }

    @Override
    public Set<Mapping> mapFull(String sourceUri, String profileUri, UriPattern... tgtUriPatterns) throws BridgeDBException {
        if (tgtUriPatterns == null){
            return mapFull(sourceUri, profileUri);
        }
        ArrayList<String> tgtUriPatternStrings = new ArrayList<String>();
        for (UriPattern tgtUriPattern:tgtUriPatterns){
            if (tgtUriPattern != null){
                tgtUriPatternStrings.add(tgtUriPattern.getUriPattern());
            }
        }
        List<Mapping> beans = uriService.map(sourceUri, profileUri, NO_SYSCODES, tgtUriPatternStrings);
        return new HashSet<Mapping>(beans); 
    }
    
    @Override
    public boolean uriExists(String URL) throws BridgeDBException {
        return uriService.URLExists(URL).exists();
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws BridgeDBException {
        URLSearchBean  bean = uriService.URLSearch(text, "" + limit);
        return bean.getURLSet();
    }

    @Override
    public Xref toXref(String URL) throws BridgeDBException {
        XrefBean bean = uriService.toXref(URL);
        return XrefBeanFactory.asXref(bean);
    }

    @Override
    public Mapping getMapping(int id) throws BridgeDBException {
        return uriService.getMapping("" + id);
    }

    @Override
    public List<Mapping> getSampleMapping() throws BridgeDBException {
        return uriService.getSampleMappings();
    }
    
    @Override
    public OverallStatistics getOverallStatistics() throws BridgeDBException {
        OverallStatisticsBean bean = uriService.getOverallStatistics();
        return OverallStatisticsBeanFactory.asOverallStatistics(bean);
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws BridgeDBException {
        MappingSetInfoBean bean = uriService.getMappingSetInfo("" + mappingSetId);
        return MappingSetInfoBeanFactory.asMappingSetInfo(bean);
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws BridgeDBException {
        List<MappingSetInfoBean> beans = uriService.getMappingSetInfos(sourceSysCode, targetSysCode);
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>(); 
        for (MappingSetInfoBean bean:beans){
            results.add(MappingSetInfoBeanFactory.asMappingSetInfo(bean));
        }
        return results;  
    }
   
    @Override
    public Set<String> getUriPatterns(String dataSource) throws BridgeDBException {
        DataSourceUriPatternBean bean = uriService.getDataSource(dataSource);
        return new HashSet<String>(bean.getUriPattern());
    }

	@Override
	public List<ProfileInfo> getProfiles() throws BridgeDBException {
		List<ProfileBean> beans = uriService.getProfiles();
		List<ProfileInfo> results = new ArrayList<ProfileInfo>();
		for (ProfileBean bean:beans) {
			results.add(ProfileBeanFactory.asProfileInfo(bean));
		}
		return results;
	}

	@Override
	public ProfileInfo getProfile(String profileURI)
			throws BridgeDBException {
		ProfileBean profile = uriService.getProfile(profileURI);
		ProfileInfo result = ProfileBeanFactory.asProfileInfo(profile);
		return result;
	}
    
    @Override
    public int getSqlCompatVersion() throws BridgeDBException {
        return Integer.parseInt(uriService.getSqlCompatVersion());
    }


  }
