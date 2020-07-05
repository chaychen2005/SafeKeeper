# FISCO BCOS 数据管理服务（SafeKeeper）介绍

SafeKeeper作为数据管理服务，提供基于身份认证的对核心数据的托管、数据找回、密码重置等一系列数据管理功能。

核心数据的种类包括但不限于区块链节点证书、节点ID、节点私钥等内容。核心数据的长度建议小于4KB。

本文档用于数据管理服务的介绍性说明，数据管理服务的使用请参考[部署文档](./safekeeper_deployment.md)及[接口设计](./interfaces.md)，同时也提供[控制台](./console_manual.md)的方式访问数据管理服务。

## 1. 使用群体

- 数据管理服务的使用者分两类：`管理员`和`访客`。
- `管理员`负责系统的用户管理，可新增其他管理员及访客。系统内置管理员`admin`。
- `访客`进行数据的相关操作。

## 2. 功能列表

1. 访客可将核心数据上传数据管理服务进行托管；
2. 数据管理服务提供访客数据的找回及加密密码重置功能；
3. 上述操作的身份认证实现。

数据管理服务通过控制台进行了封装，控制台提供以下命令，面向不同使用群体。

| 控制台命令        | 描述           | 管理员  | 访客 | 调用的数据管理服务接口     |
| ----------------- | -------------- | :-----: |:---: | -------------------------- |
| addAdminAccount   | 新增管理员账号 | √      |      | 新增子帐号                 |
| addVisitorAccount | 新增访客账号   | √      |      | 新增子帐号                 |
| deleteAccount     | 删除子账号     | √      |      | 删除子帐号                 |
| listAccount       | 查询子账号列表 | √      |      | 查询子帐号列表             |
| updatePassword    | 更改当前密码   | √      | √   | 更改当前密码               |
| uploadData        | 上传数据       |         | √   | 获取公钥/新增数据          |
| listData          | 查询数据列表   |         | √   | 查询数据列表               |
| exportData        | 导出数据       |         | √   | 查询指定数据               |
| deleteData        | 删除数据       |         | √   | 删除数据                   |
| restoreData       | 恢复数据       | √      |      | 查询指定数据               |

## 3. 部署架构

在业务应用场景中，我们基于安全考虑，建议将存有核心数据信息的数据管理服务部署在企业内网，并通过Nginx反向代理实现网络隔离、多活配置及负载均衡。

![](https://fisco-bcos-doc-chaychen.readthedocs.io/en/feature-kms/_images/recommend_deployment.png)

为实现快速体验，可如下部署：

![](https://fisco-bcos-doc-chaychen.readthedocs.io/en/feature-kms/_images/simple_depolyment.png)

## 4. 数据管理核心操作介绍

### 4.1 用于存储托管数据信息的表结构

```text
-- ----------------------------
-- Table structure for tb_data_escrow_info
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_data_escrow_info (
  account varchar(32) NOT NULL COMMENT '系统账号，数据归属标识',
  data_id varchar(128) NOT NULL COMMENT '数据标识',
  data_status int(1) NOT NULL DEFAULT '1' COMMENT '状态（1-正常 2-不可用） 默认1',
  cipher_text1 text NOT NULL COMMENT '用户托管的数据密文（可为经账号创建者公钥加密的数据密文）',
  cipher_text2 text NOT NULL COMMENT '用户托管的数据密文（可为经账号自身加密密码加密的数据密文）',
  create_time datetime DEFAULT NULL COMMENT '托管数据的时间',
  modify_time datetime DEFAULT NULL COMMENT '数据修改时间',
  description text COMMENT '备注',
  PRIMARY KEY (account,data_id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT='托管数据信息表';
```

### 4.2 上传数据

在业务应用场景中，用户的核心数据信息以文件形式存储于本地。这种存储方式存在核心数据丢失或泄露、数据管理不便等问题，因此数据管理服务面向访客提供数据托管功能，保管访客上传的数据。

访客通过控制台上传数据管理服务的核心数据信息包括：

- 数据标识：用于唯一标识该访客下的核心数据，由访客指定
- 密文1：使用该访客指定的密码进行加密的密文，可用于访客导出数据
- 密文2：使用创建该访客的管理员的公钥进行加密的密文，可用于访客遗失加密密码后由管理员恢复数据

![](https://fisco-bcos-doc-chaychen.readthedocs.io/en/feature-kms/_images/upload_data.png)

### 4.3 导出数据

访客在妥善保管加密密码的情况下，可自行通过控制台恢复数据。

![](https://fisco-bcos-doc-chaychen.readthedocs.io/en/feature-kms/_images/export_data.png)

### 4.4 恢复数据

访客如遗失加密数据的密码，可通过管理员恢复该数据。

![](https://fisco-bcos-doc-chaychen.readthedocs.io/en/feature-kms/_images/restore_data.png)

管理员恢复数据的过程中将涉及与访客的交互及对访客信息的验证，上述的交互及验证流程在控制台外进行。

### 4.5 访客重置加密密码

访客可先删除数据管理服务中已有的核心数据信息，使用新密码加密核心数据信息后再上传数据管理服务，实现重置加密密码的操作。