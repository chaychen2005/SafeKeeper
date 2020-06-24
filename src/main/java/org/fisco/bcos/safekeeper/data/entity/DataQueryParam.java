/**
 * Copyright 2014-2020  the original author or authors.
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
package org.fisco.bcos.safekeeper.data.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Entity class of param to query data.
 */
@Data
public class DataQueryParam {

    private String account;
    private String dataID;
    private String dataSubID;

    public DataQueryParam() {
        super();
    }

    public DataQueryParam(String account) {
        super();
        this.account = account;
    }

    public DataQueryParam(String account, String dataID) {
        super();
        this.account = account;
        this.dataID = dataID;
    }

    public DataQueryParam(String account, String dataID, String dataSubID) {
        super();
        this.account = account;
        this.dataID = dataID;
        this.dataSubID = dataSubID;
    }

    public DataQueryParam(String account, String dataID, String dataSubID, String dataStatus) {
        super();
        this.account = account;
        this.dataID = dataID;
        this.dataSubID = dataSubID;
    }
}
