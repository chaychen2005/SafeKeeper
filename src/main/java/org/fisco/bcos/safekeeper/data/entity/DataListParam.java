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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.fisco.bcos.safekeeper.base.entity.BaseQueryParam;

/** param of query data list. */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DataListParam extends BaseQueryParam {

    private String account;
    private String dataEntityId;
    private String dataFieldId;
    private Integer dataStatus;

    public DataListParam(
            Integer start,
            Integer pageSize,
            String account,
            String dataEntityId,
            String dataFieldId,
            Integer dataStatus,
            String flagSortedByTime) {
        super(start, pageSize, flagSortedByTime);
        this.account = account;
        this.dataEntityId = dataEntityId;
        this.dataFieldId = dataFieldId;
        this.dataStatus = dataStatus;
    }
}
