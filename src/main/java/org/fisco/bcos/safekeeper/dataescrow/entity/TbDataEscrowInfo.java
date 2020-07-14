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
package org.fisco.bcos.safekeeper.dataescrow.entity;

import java.time.LocalDateTime;
import lombok.Data;

/** Entity class of table tb_data_escrow_info. */
@Data
public class TbDataEscrowInfo {

    private String account;
    private String dataEntityId;
    private Integer dataStatus;
    private String creatorCipherText;
    private String userCipherText;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String description;

    public TbDataEscrowInfo() {
        super();
    }

    public TbDataEscrowInfo(
            String account,
            String dataEntityId,
            String creatorCipherText,
            String userCipherText,
            String description) {
        super();
        this.account = account;
        this.dataEntityId = dataEntityId;
        this.creatorCipherText = creatorCipherText;
        this.userCipherText = userCipherText;
        this.description = description;
    }
}
