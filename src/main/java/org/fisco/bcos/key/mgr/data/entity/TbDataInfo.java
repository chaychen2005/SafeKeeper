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
package org.fisco.bcos.key.mgr.data.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Entity class of table tb_data_info.
 */
@Data
public class TbDataInfo {

    private String account;
    private String dataID;
    private String dataSubID;
    private Integer dataStatus;
    private String plainText;
    private String cipherText1;
    private String cipherText2;
    private LocalDateTime createTime;
    private String description;


    public TbDataInfo() {
        super();
    }

    public TbDataInfo(String account, String dataID, String dataSubID, String plainText,
                      String cipherText1, String cipherText2, String description) {
        super();
        this.account = account;
        this.dataID = dataID;
        this.dataSubID = dataSubID;
        this.plainText = plainText;
        this.cipherText1 = cipherText1;
        this.cipherText2 = cipherText2;
        this.description = description;
    }
}
