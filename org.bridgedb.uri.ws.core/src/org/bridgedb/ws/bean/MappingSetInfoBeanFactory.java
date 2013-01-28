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
package org.bridgedb.ws.bean;

import org.bridgedb.statistics.MappingSetInfo;

/**
 *
 * @author Christian
 */
public class MappingSetInfoBeanFactory {
    
    public static MappingSetInfo asMappingSetInfo(MappingSetInfoBean bean){
        return new MappingSetInfo(bean.getId(), bean.getSourceSysCode(), bean.getPredicate(), bean.getTargetSysCode(), 
            bean.getNumberOfLinks(), bean.isIsTransitive());
    }

    public static MappingSetInfoBean asBean(MappingSetInfo info) {
        return new MappingSetInfoBean(info.getId(), info.getSourceSysCode(), info.getPredicate(), info.getTargetSysCode(), 
            info.getNumberOfLinks(), info.isTransitive());
    }
}