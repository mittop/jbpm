/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl.jpa;

import com.bmit.platform.soupe.data.core.model.AbstractBaseEntityWithDomainNoAuditing;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * The main entity that helps the runtime manager keep track of which context is bound to which <code>KieSession</code>.
 * It also provides the following two queries to fetch required information:
 * <ul>
 *  <li>FindContextMapingByContextId</li>
 *  <li>FindContextMapingByKSessionId</li>
 * </ul>
 * This entity must be included in the persistence.xml when the "Per Process Instance" strategy is used.
 */
@Entity
@Table(name = "SOUPE_WF_CONTEXT_MAPPING")
@NamedQueries(value=
    {@NamedQuery(name="FindContextMapingByContextId", 
                query="from ContextMappingInfo where contextId = :contextId"
                		+ " and ownerId = :ownerId"),
                @NamedQuery(name="FindContextMapingByKSessionId", 
                query="from ContextMappingInfo where ksessionId = :ksessionId"
                		+ " and ownerId = :ownerId"),
                @NamedQuery(name="FindKSessionToInit",
                query="select cmInfo.ksessionId from ContextMappingInfo cmInfo, "
                		+ "ProcessInstanceInfo processInstanceInfo join processInstanceInfo.eventTypes eventTypes"
                		+ " where eventTypes = 'timer' and cmInfo.contextId = cast(processInstanceInfo.processInstanceId as string)"
                		+ " and cmInfo.ownerId = :ownerId"),
        		@NamedQuery(name="FindProcessInstanceWaitingForEvent",
                query="select cmInfo.contextId from ContextMappingInfo cmInfo, "
                        + "ProcessInstanceInfo processInstanceInfo join processInstanceInfo.eventTypes eventTypes"
                        + " where eventTypes = :eventType and cmInfo.contextId = cast(processInstanceInfo.processInstanceId as string)"
                        + " and cmInfo.ownerId = :ownerId")})
public class ContextMappingInfo implements Serializable {

    private static final long serialVersionUID = 533985957655465840L;

    @Id
    @GeneratedValue(generator = "sequenceStyleGenerator")
    @GenericGenerator(
            name = "sequenceStyleGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "S_SOUPE_WF_CONTEXT_MAPPING")
            }
    )
    @Column(name = "ID")
    private Long mappingId;

    @Version
    @Column(name = "VERSION")
    private int version;
    
    @Column(name="CONTEXT_ID", nullable=false)
    private String contextId;
    @Column(name="KSESSION_ID", nullable=false)
    private Long ksessionId;
    @Column(name="OWNER_ID")
    private String ownerId;

	public ContextMappingInfo() {
        
    }

    public ContextMappingInfo(String contextId, Long ksessionId, String ownerId) {
        this.contextId = contextId;
        this.ksessionId = ksessionId;
        this.ownerId = ownerId;
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Long getKsessionId() {
        return ksessionId;
    }

    public void setKsessionId(Long ksessionId) {
        this.ksessionId = ksessionId;
    }
        
    public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

}
