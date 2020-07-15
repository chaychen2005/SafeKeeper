# FISCO BCOS 数据管理服务（SafeKeeper Service）接口说明

##  <span id="catalog_top">目录</span>
- [1.帐号管理模块](#1)
  - [1.1.账号登录](#1.1)
  - [1.2.新增子帐号](#1.2)
  - [1.3.删除子帐号](#1.3)
  - [1.4.查询子帐号列表](#1.4)
  - [1.5.更改当前密码](#1.5)
  - [1.6.获取用于加密子账号托管核心数据的公钥](#1.6)
- [2.核心数据托管模块](#2)
  - [2.1.新增数据](#2.1)
  - [2.2.删除数据](#2.2)
  - [2.3.查询数据列表](#2.3)
  - [2.4.查询指定数据](#2.4)
- [3.数据存储模块](#3)
  - [3.1.新增数据](#3.1)
  - [3.2.修改数据](#3.2)
  - [3.3.查询指定数据](#3.3)
  - [3.4.查询数据列表](#3.4)
  - [3.5.删除数据](#3.5)
  - [3.6.wedpr根据目标金额凑齐可用数据](#3.6)
  - [3.7.wedpr查询尚未花费的金额总和](#3.7)
  - [3.8.wedpr查询已花费的金额总和](#3.8)
  - [3.9.wedpr批量修改数据](#3.9)
  - [3.10 wedpr查询特定状态的数据](#3.10)
- [4.错误码说明](#4)

以下除登录`/accounts/v1/login`之外的请求均带有header信息。

| 序号 | 请求header         | 类型   | 可为空 | 备注     |
| ---- | ------------------ | ------ | ------ | -------- |
| 1    | AuthorizationToken | String | 否     | 会话标识 |

## <span id="1">1 帐号管理模块</span>  [top](#catalog_top)

### <span id="1.1">1.1 账号登录</span>  [top](#catalog_top)

#### 1.1.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/accounts/v1/login`
* 请求方式：POST
* 请求头：Content-type: application/x-www-form-urlencoded
* 返回格式：JSON

#### 1.1.2 参数信息详情

| 序号 | 请求body   | 类型   | 可为空 | 备注                                                 |
| ---- | ---------- | ------ | ------ | ---------------------------------------------------- |
| 1    | account    | String | 否     | 帐号名称，要求5-20位字母数字下划线组成，且以字符开头 |
| 2    | accountPwd | String | 否     | 登录密码，要求6-20位字母数字下划线组成               |

| 序号 | 返回body      | 类型    | 可为空 | 备注                       |
| ---- | ------------- | ------- | ------ | -------------------------- |
| 1    | code          | Int     | 否     | 返回码，0：成功 其它：失败 |
| 2    | message       | String  | 否     | 描述                       |
| 3    | data          | object  | 否     | 返回信息实体               |
| 3.1  | accountStatus | Integer | 否     | 帐号状态                   |
| 3.2  | account       | String  | 否     | 帐号名称                   |
| 3.3  | roleName      | String  | 否     | 角色名称                   |
| 3.4  | token         | String  | 否     | 登录标识                   |

### 1.1.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/accounts/v1/login`

```
{
    "account": "admin",
    "accountPwd": "Abcd1234"
}
```

#### 1.1.4 出参示例

* 成功：
```
{
  "code": 0,
  "message": "success",
  "data": {
    "accountStatus": 1,
    "roleName": "admin",
    "account": "admin",
    "token": "c3348a56159739dde190a622830e892bea05e9d2206c43fc1bdcdd07fdc79eb4"
  }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```


### <span id="1.2">1.2 新增子帐号</span>  [top](#catalog_top)

#### 1.2.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/accounts/v1`
* 请求方式：POST
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 1.2.2 参数信息详情

| 序号 | 请求body   | 类型   | 可为空 | 备注                                                 |
| ---- | ---------- | ------ | ------ | ---------------------------------------------------- |
| 1    | account    | String | 否     | 帐号名称，要求5-20位字母数字下划线组成，且以字符开头 |
| 2    | accountPwd | String | 否     | 登录密码，要求6-20位字母数字下划线组成               |
| 3    | roleId     | int    | 否     | 所属角色                                             |
| 4    | publicKey  | String | 是     | 用于加密子账号托管的核心数据                         |

| 序号 | 返回body      | 类型          | 可为空 | 备注                         |
| ---- | ------------- | ------------- | ------ | ---------------------------- |
| 1    | code          | Int           | 否     | 返回码，0：成功 其它：失败   |
| 2    | message       | String        | 否     | 描述                         |
| 3    | data          | object        | 否     | 返回信息实体                 |
| 3.1  | account       | String        | 否     | 帐号名称                     |
| 3.2  | accountPwd    | String        | 是     | 登录密码                     |
| 3.3  | roleId        | Integer       | 否     | 所属角色                     |
| 3.4  | roleName      | String        | 否     | 角色名称                     |
| 3.5  | accountStatus | Integer       | 否     | 帐号状态                     |
| 3.6  | description   | String        | 是     | 备注                         |
| 3.7  | createTime    | LocalDateTime | 否     | 创建时间                     |
| 3.8  | modifyTime    | LocalDateTime | 否     | 修改时间                     |
| 3.9  | email         | String        | 是     | 用户邮箱                     |
| 3.10 | publicKey     | String        | 否     | 用于加密子账号托管的核心数据 |
| 3.11 | creator       | String        | 否     | 创建者账号                   |

### 1.2.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/accounts/v1`

```
{
    "account": "user1",
    "accountPwd": "Abcd1234",
    "roleId": 100001,
    "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49"
}
```

#### 1.2.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "account": "user1",
        "accountPwd": null,
        "roleId": 100001,
        "roleName": "visitor",
        "accountStatus": 1,
        "description": null,
        "createTime": "2020-07-12T22:08:34",
        "modifyTime": "2020-07-12T22:08:34",
        "email": null,
        "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49",
        "creator": "admin"
    }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

### <span id="1.3">1.3 删除子帐号</span>  [top](#catalog_top)

#### 1.3.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/accounts/v1/{account}`
* 请求方式：DELETE
* 返回格式：JSON

#### 1.3.2 参数信息详情

| 序号 | 请求body | 类型   | 可为空 | 备注     |
| ---- | -------- | ------ | ------ | -------- |
| 1    | account  | String | 否     | 帐号名称 |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

#### 1.3.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/accounts/v1/user1`

#### 1.3.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": null
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

###  <span id="1.4">1.4 查询子帐号列表</span>  [top](#catalog_top)

#### 1.4.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/accounts/v1`
* 请求方式：GET
* 返回格式：JSON

#### 1.4.2 参数信息详情

| 序号 | 请求body   | 类型 | 可为空 | 备注       |
| ---- | ---------- | ---- | ------ | ---------- |
| 1    | pageSize   | Int  | 否     | 每页记录数 |
| 2    | pageNumber | Int  | 否     | 当前页码   |

| 序号   | 返回body      | 类型          | 可为空 | 备注                       |
| ------ | ------------- | ------------- | ------ | -------------------------- |
| 1      | code          | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2      | message       | String        | 否     | 描述                       |
| 3      | totalCount    | Int           | 否     | 总记录数                   |
| 4      | data          | List          | 是     | 信息列表                   |
| 4.1    |               | Object        |        | 信息对象                   |
| 4.1.1  | account       | String        | 否     | 帐号名称                   |
| 4.1.2  | accountPwd    | String        | 是     | 登录密码                   |
| 4.1.3  | roleId        | Integer       | 否     | 所属角色                   |
| 4.1.4  | roleName      | String        | 否     | 角色名称                   |
| 4.1.5  | accountStatus | Integer       | 否     | 帐号状态                   |
| 4.1.6  | description   | String        | 是     | 备注                       |
| 4.1.7  | createTime    | LocalDateTime | 否     | 创建时间                   |
| 4.1.8  | modifyTime    | LocalDateTime | 否     | 修改时间                   |
| 4.1.9  | email         | String        | 是     | 用户邮箱                   |
| 4.1.10 | publicKey     | String        | 否     | 用于加密子账号托管的数据   |
| 4.1.11 | creator       | String        | 否     | 创建者账号                 |

#### 1.4.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/accounts/v1?pageNumber=1&pageSize=10`

#### 1.4.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "account": "user1",
            "accountPwd": null,
            "roleId": 100001,
            "roleName": "visitor",
            "accountStatus": 1,
            "description": null,
            "createTime": "2020-07-12T22:08:34",
            "modifyTime": "2020-07-12T22:08:34",
            "email": null,
            "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49",
            "creator": "admin"
        },
        {
            "account": "admin",
            "accountPwd": null,
            "roleId": 100000,
            "roleName": "admin",
            "accountStatus": 1,
            "description": null,
            "createTime": "2020-07-12T21:58:16",
            "modifyTime": "2020-07-12T21:58:16",
            "email": null,
            "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49",
            "creator": null
        }
    ],
    "totalCount": 2
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

### <span id="1.5">1.5 更新当前密码</span>  [top](#catalog_top)

#### 1.5.1 传输协议规范
* 网络传输协议：使用HTTPS协议
* 请求地址：`/accounts/v1/password`
* 请求方式：PATCH
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 1.5.2 参数信息详情

| 序号 | 请求body      | 类型   | 可为空 | 备注                                 |
| ---- | ------------- | ------ | ------ | ------------------------------------ |
| 1    | oldAccountPwd | String | 否     | 旧密码                               |
| 2    | newAccountPwd | String | 否     | 新密码，要求6-20位字母数字下划线组成 |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |


### 1.5.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/accounts/v1/password`

```
{
    "oldAccountPwd": "Abcd1234",
    "newAccountPwd": "12345678"
}
```

#### 1.5.4 出参示例
* 成功：
```
{
    "code": 0,
    "message": "success"
    "data": null
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

### <span id="1.6">1.6 获取用于加密子账号托管核心数据的公钥</span>  [top](#catalog_top)

#### 1.6.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/accounts/v1/publicKey`
* 请求方式：GET
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 1.6.2 参数信息详情

| 序号 | 返回body  | 类型   | 可为空 | 备注                       |
| ---- | --------- | ------ | ------ | -------------------------- |
| 1    | code      | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message   | String | 否     | 描述                       |
| 3    | data      | object | 否     | 返回信息实体               |
| 3.1  | account   | String | 否     | 加密帐号（管理员）名称     |
| 3.2  | publicKey | String | 否     | 用于加密子账号托管的数据   |

### 1.6.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/accounts/v1/publicKey`

#### 1.6.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "creatorAccount": "admin",
        "creatorPublicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49"
    }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

## <span id="2">2 核心数据托管模块</span>  [top](#catalog_top)

### <span id="2.1">2.1 新增数据</span>  [top](#catalog_top)

#### 2.1.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/escrow/v1/vaults`
* 请求方式：POST
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 2.1.2 参数信息详情

| 序号 | 请求body          | 类型   | 可为空 | 备注                       |
| ---- | ----------------- | ------ | ------ | -------------------------- |
| 1    | dataEntityId      | String | 否     | 数据标识                   |
| 2    | creatorCipherText | String | 否     | 经账号创建者公钥加密的内容 |
| 3    | userCipherText    | String | 否     | 经账号提供的密码加密的内容 |

| 序号 | 返回body          | 类型          | 可为空 | 备注                       |
| ---- | ----------------- | ------------- | ------ | -------------------------- |
| 1    | code              | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2    | message           | String        | 否     | 描述                       |
| 3    | data              | object        | 否     | 返回信息实体               |
| 3.1  | account           | String        | 否     | 帐号名称                   |
| 3.2  | dataEntityId      | String        | 否     | 数据标识                   |
| 3.3  | dataStatus        | String        | 否     | 数据状态                   |
| 3.4  | creatorCipherText | String        | 否     | 经账号创建者公钥加密的内容 |
| 3.5  | userCipherText    | String        | 否     | 经账号提供的密码加密的内容 |
| 3.6  | createTime        | LocalDateTime | 否     | 数据托管时间               |
| 3.7  | modifyTime        | LocalDateTime | 否     | 数据修改时间               |
| 3.8  | description       | String        | 是     | 数据备注信息               |

### 1.1.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/escrow/v1/vaults`
```
{
    "dataEntityId":"data1",
    "creatorCipherText":"048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
    "userCipherText":"F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7"
}
```

#### 1.1.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "account": "user1",
        "dataEntityId": "data1",
        "dataStatus": 1,
        "creatorCipherText": "048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
        "userCipherText": "F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7",
        "createTime": "2020-07-12T22:16:23",
        "modifyTime": "2020-07-12T22:16:23",
        "description": null
    }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

### <span id="2.2">2.2 删除数据</span>  [top](#catalog_top)

#### 2.2.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/escrow/v1/vaults/{vaultId}`
* 请求方式：DELETE
* 返回格式：JSON

#### 2.2.2 参数信息详情

| 序号 | 请求body | 类型   | 可为空 | 备注     |
| ---- | -------- | ------ | ------ | -------- |
| 1    | vaultId  | String | 否     | 数据标识 |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

#### 2.2.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/escrow/v1/vaults/data1`

#### 2.2.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": null
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

###  <span id="2.3">2.3 查询数据列表</span>  [top](#catalog_top)

#### 2.3.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/escrow/v1/vaults`
* 请求方式：GET
* 返回格式：JSON

#### 2.3.2 参数信息详情

| 序号 | 输入body   | 类型 | 可为空 | 备注       |
| ---- | ---------- | ---- | ------ | ---------- |
| 1    | pageSize   | Int  | 否     | 每页记录数 |
| 2    | pageNumber | Int  | 否     | 当前页码   |

| 序号  | 返回body          | 类型          | 可为空 | 备注                       |
| ----- | ----------------- | ------------- | ------ | -------------------------- |
| 1     | code              | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2     | message           | String        | 否     | 描述                       |
| 3     | totalCount        | Int           | 否     | 总记录数                   |
| 4     | data              | List          | 是     | 信息列表                   |
| 4.1   |                   | Object        |        | 信息对象                   |
| 4.1.1 | account           | String        | 否     | 帐号名称                   |
| 4.1.2 | dataEntityId      | String        | 否     | 数据标识                   |
| 4.1.3 | dataStatus        | String        | 否     | 数据状态                   |
| 4.1.4 | creatorCipherText | String        | 否     | 经账号创建者公钥加密的内容 |
| 4.1.5 | userCipherText    | String        | 否     | 经账号提供的密码加密的内容 |
| 4.1.6 | createTime        | LocalDateTime | 否     | 数据托管时间               |
| 4.1.7 | modifyTime        | LocalDateTime | 否     | 数据修改时间               |
| 4.1.8 | description       | String        | 是     | 数据备注信息               |

#### 2.3.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/escrow/v1/vaults?pageNumber=1&pageSize=10`

#### 2.3.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "account": "user1",
            "dataEntityId": "data1",
            "dataStatus": 1,
            "creatorCipherText": "048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
            "userCipherText": "F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7",
            "createTime": "2020-07-12T22:16:23",
            "modifyTime": "2020-07-12T22:16:23",
            "description": null
        }
    ],
    "totalCount": 1
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

###  <span id="2.4">2.4 查询指定数据</span>  [top](#catalog_top)

#### 2.4.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/escrow/v1/vaults/{account}/{vaultId}`
* 请求方式：GET
* 返回格式：JSON

#### 2.4.2 参数信息详情

| 序号 | 输入参数          | 类型          | 可为空 | 备注                       |
| ---- | ----------------- | ------------- | ------ | -------------------------- |
| 1    | account           | String        | 否     | 帐号名称                   |
| 2    | vaultId           | String        | 否     | 数据标识                   |
|      | 输出参数          | 类型          |        | 备注                       |
| 1    | code              | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2    | message           | String        | 否     | 描述                       |
| 3    | data              | object        | 否     | 返回信息实体               |
| 3.1  | account           | String        | 否     | 帐号名称                   |
| 3.2  | dataEntityId      | String        | 否     | 数据标识                   |
| 3.3  | dataStatus        | String        | 否     | 数据状态                   |
| 3.4  | creatorCipherText | String        | 否     | 经账号创建者公钥加密       |
| 3.5  | userCipherText    | String        | 否     | 经账号提供的密码加密       |
| 3.6  | createTime        | LocalDateTime | 否     | 核心数据托管时间           |
| 3.7  | modifyTime        | LocalDateTime | 否     | 数据修改时间               |
| 3.8  | description       | String        | 是     | 数据备注信息               |


#### 2.4.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/escrow/v1/vaults/user1/data1`

#### 2.4.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "account": "user1",
        "dataEntityId": "data1",
        "dataStatus": 1,
        "creatorCipherText": "048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
        "userCipherText": "F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7",
        "createTime": "2020-07-12T22:16:23",
        "modifyTime": "2020-07-12T22:16:23",
        "description": null
    }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

## <span id="3">3 数据存储模块</span>  [top](#catalog_top)

### <span id="3.1">3.1 新增数据</span>  [top](#catalog_top)

#### 3.1.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/data/v1`
* 请求方式：POST
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 3.1.2 参数信息详情

| 序号 | 请求body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | key      | String | 否     | 新增的数据主键             |
| 2    | value    | object | 否     | 新增的数据内容（Json格式） |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

### 3.1.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/v1`

```json
{
    "key":"key1",
    "value": {
        "value":"100",
        "status":"0",
        "orderID":"order_1",
        "creditCredential":"credit_1"
  	}
}
```

#### 3.1.4 出参示例

* 成功：
```json
{
    "code": 0,
    "message": "success",
    "data": null
}
```

* 失败：
```json
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

### <span id="3.2">3.2 修改数据</span>  [top](#catalog_top)

#### 3.2.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/data/v1`
* 请求方式：PATCH
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 3.2.2 参数信息详情

| 序号 | 请求body | 类型   | 可为空 | 备注                             |
| ---- | -------- | ------ | ------ | -------------------------------- |
| 1    | key      | String | 否     | 修改的数据主键                   |
| 2    | value    | object | 否     | 修改的数据内容（Json格式，增量） |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

### 3.2.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/v1`

```
{
    "key":"key1",
    "value": {
        "status":"1"
  	}
}
```

#### 3.2.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": null
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

###  <span id="3.3">3.3 查询指定数据</span>  [top](#catalog_top)

#### 3.3.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/data/v1/{dataEntityId}`
* 请求方式：GET
* 返回格式：JSON

#### 3.3.2 参数信息详情

| 序号 | 请求body       | 类型   | 可为空 | 备注     |
| ---- | -------------- | ------ | ------ | -------- |
| 1    | dataEntityId   | String | 否     | 数据标识 |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体               |

#### 3.3.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/v1/key1`

#### 3.3.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "key": "key1",
        "creditCredential": "credit_1",
        "orderID": "order_1",
        "status": "0",
        "value": "100",
        "lastModifyTime": "2020-07-15T06:39:46Z"
    }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

###  <span id="3.4">3.4 查询数据列表</span>  [top](#catalog_top)

#### 3.4.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/data/v1`
* 请求方式：GET
* 返回格式：JSON

#### 3.4.2 参数信息详情

| 序号 | 请求body   | 类型 | 可为空 | 备注       |
| ---- | ---------- | ---- | ------ | ---------- |
| 1    | pageNumber | Int  | 否     | 每页记录数 |
| 2    | pageSize   | Int  | 否     | 当前页码   |

| 序号 | 返回body   | 类型   | 可为空 | 备注                       |
| ---- | ---------- | ------ | ------ | -------------------------- |
| 1    | code       | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message    | String | 否     | 描述                       |
| 3    | data       | List   | 是     | 信息列表                   |
| 3.1  |            | Object |        | 信息对象                   |
| 4    | totalCount | Int    | 否     | 总记录数                   |

#### 3.4.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/v1?pageNumber=1&pageSize=10`

#### 3.4.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "key": "key1",
            "creditCredential": "credit_1",
            "orderID": "order_1",
            "status": "0",
            "value": "100",
            "lastModifyTime": "2020-07-15T06:39:46Z"
        },
        {
            "key": "key2",
            "creditCredential": "credit_2",
            "orderID": "order_2",
            "status": "0",
            "value": "200",
            "lastModifyTime": "2020-07-15T06:39:46Z"
        }
    ],
    "totalCount": 2
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

### <span id="3.5">3.5 删除数据</span>  [top](#catalog_top)

#### 3.5.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/data/v1/{dataEntityId}`
* 请求方式：DELETE
* 返回格式：JSON

#### 3.5.2 参数信息详情

| 序号 | 请求body       | 类型   | 可为空 | 备注     |
| ---- | -------------- | ------ | ------ | -------- |
| 1    | dataEntityId   | String | 否     | 数据标识 |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

#### 3.5.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/v1/key1`

#### 3.5.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "Success",
    "data": null
}
```

###  <span id="3.6">3.6 wedpr根据目标金额凑齐可用数据</span>  [top](#catalog_top)

#### 3.6.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/data/wedpr/vcl/v1/credentials/approve`
* 请求方式：PATCH
* 返回格式：JSON

#### 3.6.2 参数信息详情

| 序号 | 请求body | 类型 | 可为空 | 备注     |
| ---- | -------- | ---- | ------ | -------- |
| 1    | value    | long | 否     | 目标金额 |

| 序号 | 返回body    | 类型   | 可为空 | 备注                       |
| ---- | ----------- | ------ | ------ | -------------------------- |
| 1    | code        | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message     | String | 否     | 描述                       |
| 3    | data        | Object | 否     | 信息列表                   |
| 3.1  | creditList  | Array  | 否     | 列表                       |
| 3.2  | creditValue | long   | 否     | 总额                       |

#### 3.6.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/wedpr/vcl/v1/credentials/approve?value=240`

#### 3.6.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "creditList": [
            {
                "key": "key2",
                "creditCredential": "credit_2",
                "orderID": "order_2",
                "status": "2",
                "value": "200",
                "lastModifyTime": "2020-07-15T06:39:46Z"
            },
            {
                "key": "key1",
                "creditCredential": "credit_1",
                "orderID": "order_1",
                "status": "2",
                "value": "100",
                "lastModifyTime": "2020-07-15T06:39:46Z"
            }
        ],
        "creditValue": 300
    }
}
```

* 失败：
```
// 系统异常
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
// 余额不足
{
    "code": 200406,
    "message": "not sufficient tokens",
    "data": null
}
```

###  <span id="3.7">3.7 wedpr查询尚未花费的金额总和</span>  [top](#catalog_top)

#### 3.7.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/data/wedpr/vcl/v1/credentials/balance`
* 请求方式：GET
* 返回格式：JSON

#### 3.7.2 参数信息详情

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | Object | 否     | 返回信息实体               |
| 3.1  | unspent  | long   | 否     | 尚未花费金额总和           |

#### 3.7.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/wedpr/vcl/v1/credentials/balance`

#### 3.7.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "balance": 2000
    }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

###  <span id="3.8">3.8 wedpr查询已花费的金额总和</span>  [top](#catalog_top)

#### 3.8.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/data/wedpr/vcl/v1/credentials/expenditure`
* 请求方式：GET
* 返回格式：JSON

#### 3.8.2 参数信息详情

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | Object | 否     | 返回信息实体               |
| 3.1  | spent    | long   | 否     | 已花费金额总和             |

#### 3.8.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/wedpr/vcl/v1/credentials/expenditure`

#### 3.8.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "expenditure": 1000
    }
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

### <span id="3.9">3.9 wedpr批量修改数据</span>  [top](#catalog_top)

#### 3.9.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/data/wedpr/vcl/v1/credentials/spend`
* 请求方式：PATCH
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 3.9.2 参数信息详情

| 序号 | 请求body | 类型   | 可为空 | 备注                             |
| ---- | -------- | ------ | ------ | -------------------------------- |
| 1    |          | object | 是     | 修改的数据实体列表               |
| 1.1  | key      | String | 否     | 修改的数据主键                   |
| 1.2  | value    | object | 否     | 修改的数据内容（Json格式，增量） |

| 序号 | 返回body | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

### 3.9.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/wedpr/vcl/v1/credentials/spend`

```
[
    {
        "key":"key1",
        "value": {
            "status":"1"
        }
    },
    {
        "key":"key2",
        "value": {
            "status":"1"
        }
    }
]
```

#### 3.9.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": null
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

###  <span id="3.10">3.10 wedpr查询特定状态的数据</span>  [top](#catalog_top)

#### 3.10.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/data/wedpr/vcl/v1/credentials`
* 请求方式：GET
* 返回格式：JSON

#### 3.10.2 参数信息详情

| 序号 | 请求body         | 类型   | 可为空 | 备注       |
| ---- | ---------------- | ------ | ------ | ---------- |
| 1    | pageNumber       | Int    | 否     | 每页记录数 |
| 2    | pageSize         | Int    | 否     | 当前页码   |
| 3    | credentialStatus | String | 否     | 数据状态   |

| 序号 | 返回body   | 类型   | 可为空 | 备注                       |
| ---- | ---------- | ------ | ------ | -------------------------- |
| 1    | code       | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message    | String | 否     | 描述                       |
| 3    | data       | List   | 是     | 信息列表                   |
| 3.1  |            | Object |        | 信息对象                   |
| 4    | totalCount | Int    | 否     | 总记录数                   |

#### 3.10.3 入参示例

`https://127.0.0.1:9501/SafeKeeper/data/wedpr/vcl/v1/credentials?pageNumber=1&pageSize=10&credentialStatus=1`

#### 3.10.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "key": "key1",
            "creditCredential": "credit_1",
            "orderID": "order_1",
            "status": "1",
            "value": "100",
            "lastModifyTime": "2020-07-15T06:39:46Z"
        },
        {
            "key": "key2",
            "creditCredential": "credit_2",
            "orderID": "order_2",
            "status": "1",
            "value": "200",
            "lastModifyTime": "2020-07-15T06:39:46Z"
        }
    ],
    "totalCount": 2
}
```

* 失败：
```
{
    "code": 100000,
    "message": "system exception",
    "data": null
}
```

## <span id="4">4 错误码说明</span>  [top](#catalog_top)

| code   | message                                | type                             | description           |
| ------ | -------------------------------------- | -------------------------------- | --------------------- |
| 0      | success                                | return success                   | 成功                  |
| 100000 | system exception                       | system exception                 | 系统错误              |
| 200000 | database exception                     | business exception - database    | 数据库操作异常        |
| 200100 | account info already exists            | business exception - account     | 该账号已注册          |
| 200101 | account info not exists                | business exception - account     | 该账号未注册          |
| 200102 | account name cannot be empty           | business exception - account     | 账号名称为空          |
| 200103 | password error                         | business exception - account     | 登录密码错误          |
| 200104 | the new password cannot be same as old | business exception - account     | 新旧密码不能一致      |
| 200105 | role id cannot be empty                | business exception - account     | 角色标识不能为空      |
| 200106 | invalid role id                        | business exception - account     | 无效的角色标识        |
| 200107 | invalid account format                 | business exception - account     | 账号名称格式错误      |
| 200108 | invalid password format                | business exception - account     | 登录密码格式错误      |
| 200119 | lack of access to the account          | business exception - account     | 无访问该账号权限      |
| 200111 | invalid public key length              | business exception - account     | 无效的公钥长度        |
| 200200 | data info already exists               | business exception - data escrow | 该数据已托管          |
| 200201 | data info not exists                   | business exception - data escrow | 该数据未托管          |
| 200202 | data id cannot be empty                | business exception - data escrow | 数据标识不能为空      |
| 200203 | lack of access to the escrow data      | business exception - data escrow | 无访问该数据权限      |
| 200300 | invalid token                          | business exception - token       | 该Token不存在         |
| 200301 | expired token                          | business exception - token       | 该Token超时           |
| 200400 | insert data struct fail                | business exception - data        | 插入数据失败          |
| 200401 | lack of access to the data             | business exception - data        | 无访问该数据权限      |
| 200402 | data entity id cannot be empty         | business exception - data        | 数据实体标识不能为空  |
| 200403 | data field id cannot be empty          | business exception - data        | 数据字段标识不能为空  |
| 200404 | data not exists                        | business exception - data        | 该数据已存在          |
| 200405 | data already exists                    | business exception - data        | 该数据尚未存在        |
| 200900 | not sufficient credit                  | business exception - credit      | 余额不足              |
| 300000 | access denied                          | auth exception                   | 无操作权限            |
| 300001 | the operation requires admin privileges| auth exception                   | 该操作需管理员权限    |
| 300002 | the operation requires user privileges | auth exception                   | 该操作需业务员权限    |
| 400000 | param exception                        | param exception                  | 参数校验错误          |