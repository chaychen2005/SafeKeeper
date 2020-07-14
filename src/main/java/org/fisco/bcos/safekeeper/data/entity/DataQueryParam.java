/**
 * Copyright 2014-2020 the original author or authors.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.safekeeper.data.entity;

import lombok.Data;

/** Entity class of param to query data. */
@Data
public class DataQueryParam {

    private String account;
    private String dataEntityId;
    private String dataFieldId;

    public DataQueryParam() {
        super();
    }

    public DataQueryParam(String account) {
        super();
        this.account = account;
    }

    public DataQueryParam(String account, String dataEntityId) {
        super();
        this.account = account;
        this.dataEntityId = dataEntityId;
    }

    public DataQueryParam(String account, String dataEntityId, String dataFieldId) {
        super();
        this.account = account;
        this.dataEntityId = dataEntityId;
        this.dataFieldId = dataFieldId;
    }
}
