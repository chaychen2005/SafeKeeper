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
package org.fisco.bcos.safekeeper.dataescrow;

import org.apache.ibatis.annotations.Param;
import org.fisco.bcos.safekeeper.dataescrow.entity.TbDataEscrowInfo;
import org.fisco.bcos.safekeeper.dataescrow.entity.DataEscrowListParam;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * mapper about data escrow.
 */
@Repository
public interface DataEscrowMapper {

    Integer addDataRow(TbDataEscrowInfo tbDataEscrowInfo);

    TbDataEscrowInfo queryData(@Param("account") String account, @Param("dataID") String dataID);

    Integer deleteDataRow(@Param("account") String account, @Param("dataID") String dataID);

    Integer countOfData(@Param("account") String account, @Param("dataID") String dataID);

    Integer countOfDataOwnedByAccount(@Param("account") String account);

    List<TbDataEscrowInfo> listOfDataOwnedByAccount(@Param("param") DataEscrowListParam param);
}
