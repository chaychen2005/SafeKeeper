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
package org.fisco.bcos.safekeeper.base.code;

/**
 * A-BB-CCC <br>
 * A:error level. <br>
 * 1:system exception <br>
 * 2:business exception <br>
 * 3:auth exception <br>
 * 4:param exception <br>
 * B:project number <br>
 * SafeKeeper:00 <br>
 * C: error code <br>
 * 0XX:database exception <br>
 * 1XX:account exception <br>
 * 2XX:data escrow exception <br>
 * 3XX:token exception <br>
 * 4XX:data vault exception <br>
 * 9XX:other exception <br>
 */
public class ConstantCode {

    /** Return success. */
    public static final RetCode SUCCESS = RetCode.mark(0, "success");

    /** System exception. */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(100000, "system exception");

    /** Business exception. */
    public static final RetCode DB_EXCEPTION = RetCode.mark(200000, "database exception");

    public static final RetCode ACCOUNT_EXISTS =
            RetCode.mark(200100, "account info already exists");

    public static final RetCode ACCOUNT_NOT_EXISTS =
            RetCode.mark(200101, "account info not exists");

    public static final RetCode EMPTY_ACCOUNT_NAME =
            RetCode.mark(200102, "account name cannot be empty");

    public static final RetCode PASSWORD_ERROR = RetCode.mark(200103, "password error");

    public static final RetCode REUSED_PASSWORD =
            RetCode.mark(200104, "the new password cannot be same as old");

    public static final RetCode EMPTY_ROLE_ID = RetCode.mark(200105, "role id cannot be empty");

    public static final RetCode INVALID_ROLE_ID = RetCode.mark(200106, "invalid role id");

    public static final RetCode INVALID_ACCOUNT_FORMAT =
            RetCode.mark(
                    200107,
                    "invalid account format. account should begin with a letter, between 5 and 20 in length, and contain only characters, numbers, and underscores");

    public static final RetCode INVALID_PASSWORD_FORMAT =
            RetCode.mark(
                    200108,
                    "invalid password format. password should be between 6 and 20 in length, and contain only characters, numbers, and underscores");

    public static final RetCode ACCOUNT_ACCESS_DENIAL =
            RetCode.mark(200109, "lack of access to the account");

    public static final RetCode INVALID_PUBLIC_KEY_LENGTH =
            RetCode.mark(200110, "invalid public key length");

    public static final RetCode DATA_ESCROW_EXISTS =
            RetCode.mark(200200, "data info already exists");

    public static final RetCode DATA_ESCROW_NOT_EXISTS =
            RetCode.mark(200201, "data info not exists");

    public static final RetCode EMPTY_DATA_ESCROW_ID =
            RetCode.mark(200202, "data id cannot be empty");

    public static final RetCode DATA_ESCROW_ACCESS_DENIAL =
            RetCode.mark(200203, "lack of access to the escrow data");

    public static final RetCode INVALID_TOKEN = RetCode.mark(200300, "invalid token");

    public static final RetCode EXPIRED_TOKEN = RetCode.mark(200301, "expired token");

    // wedpr use 2004XX and 2009XX
    public static final RetCode INSERT_DATA_ERROR = RetCode.mark(200400, "insert data struct fail");

    public static final RetCode EMPTY_DATA_ENTITY_ID =
            RetCode.mark(200401, "data entity id cannot be empty");

    public static final RetCode EMPTY_DATA_FIELD_ID =
            RetCode.mark(200402, "data field id cannot be empty");

    public static final RetCode DATA_NOT_EXISTS = RetCode.mark(200403, "data not exists");

    public static final RetCode DATA_EXISTS = RetCode.mark(200404, "data already exists");

    public static final RetCode NOT_SUFFICIENT_CREDITS =
            RetCode.mark(200900, "not sufficient credit");

    /* auth */
    public static final RetCode ACCESS_DENIED = RetCode.mark(300000, "access denied");

    public static final RetCode ADMIN_ROLE_REQUIRED =
            RetCode.mark(300001, "the operation requires admin privileges");

    public static final RetCode USER_ROLE_REQUIRED =
            RetCode.mark(300002, "the operation requires user privileges");

    /* param exception */
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(400000, "param exception");
}
