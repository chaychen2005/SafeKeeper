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
package org.fisco.bcos.safekeeper.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.fisco.bcos.safekeeper.data.entity.CreditInfo;
import org.fisco.bcos.safekeeper.data.entity.DataListParam;
import org.fisco.bcos.safekeeper.data.entity.DataQueryParam;
import org.fisco.bcos.safekeeper.data.entity.TbDataInfo;
import org.springframework.stereotype.Repository;

/** mapper about data. */
@Repository
public interface DataMapper {

    Integer addDataRow(TbDataInfo tbDataInfo);

    Integer updateDataRow(TbDataInfo tbDataInfo);

    List<TbDataInfo> queryData(@Param("param") DataQueryParam queryParams);

    Integer deleteDataRow(@Param("param") DataQueryParam queryParams);

    Integer existOfData(@Param("param") DataQueryParam queryParams);

    Integer countOfData(
            @Param("account") String account,
            @Param("dataEntityId") String dataEntityId,
            @Param("dataFieldId") String dataFieldId,
            @Param("dataStatus") int dataStatus);

    List<TbDataInfo> listOfData(@Param("param") DataListParam param);

    List<String> listOfDataId(
            @Param("account") String account, @Param("dataStatus") int dataStatus);

    List<String> listOfDataIdByCreditStatus(
            @Param("account") String account, @Param("dataFieldValue") String dataFieldValue);

    List<CreditInfo> listOfCreditWithCreditStatus(
            @Param("account") String account, @Param("status") String status);

    List<String> listOfValueWithCreditStatus(
            @Param("account") String account, @Param("status") String status);

    Integer updateCreditStatus(
            @Param("account") String account,
            @Param("dataEntityId") String dataEntityId,
            @Param("dataFieldId") String dataFieldId,
            @Param("srcCreditStatus") String srcCreditStatus,
            @Param("desCreditStatus") String desCreditStatus);
}
