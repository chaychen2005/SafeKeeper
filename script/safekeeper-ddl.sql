
-- ----------------------------
-- Table structure for tb_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_role (
  role_id int(11) NOT NULL AUTO_INCREMENT COMMENT '角色编号',
  role_name varchar(32) DEFAULT NULL COMMENT '角色名称（英文）',
  description text COMMENT '备注',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (role_id),
  UNIQUE KEY (role_name)
) ENGINE=InnoDB AUTO_INCREMENT=100000 COMMENT='角色信息表';

-- ----------------------------
-- Table structure for tb_account_info
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_account_info (
  account varchar(32) binary NOT NULL COMMENT '系统账号',
  account_pwd varchar(250) NOT NULL COMMENT '登录密码',
  role_id int(11) NOT NULL COMMENT '所属角色编号',
  account_status int(1) NOT NULL DEFAULT '1' COMMENT '状态（1-未更新密码 2-正常 3-冻结） 默认1',
  description text COMMENT '备注',
  email varchar(40) DEFAULT NULL COMMENT '用户邮箱',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  modify_time datetime DEFAULT NULL COMMENT '修改时间',
  public_key varchar(250) COMMENT '该账号使用的公钥',
  creator varchar(50) binary COMMENT '创建该账号的账号',
  PRIMARY KEY (account),
  FOREIGN KEY(role_id) REFERENCES tb_role(role_id)
) ENGINE=InnoDB COMMENT='系统账号信息表';

-- ----------------------------
-- Table structure for tb_token
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_token (
  token varchar(120) NOT NULL COMMENT '会话标识',
  account varchar(50) NOT NULL COMMENT '用户编号',
  expire_time datetime DEFAULT NULL COMMENT '失效时间',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (token,account)
) ENGINE=InnoDB COMMENT='token信息表';

-- ----------------------------
-- Table structure for tb_data_escrow_info
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_data_escrow_info (
  account varchar(32) NOT NULL COMMENT '系统账号，数据归属标识',
  data_entity_id varchar(128) NOT NULL COMMENT '数据标识',
  data_status int(1) NOT NULL DEFAULT '1' COMMENT '状态（1-正常 2-不可用） 默认1',
  creator_cipher_text text NOT NULL COMMENT '用户托管的数据密文（可为经账号创建者公钥加密的数据密文）',
  user_cipher_text text NOT NULL COMMENT '用户托管的数据密文（可为经账号自身加密密码加密的数据密文）',
  create_time datetime DEFAULT NULL COMMENT '托管数据的时间',
  modify_time datetime DEFAULT NULL COMMENT '数据修改时间',
  description text COMMENT '备注',
  PRIMARY KEY (account,data_entity_id)
) ENGINE=InnoDB COMMENT='托管数据信息表';

-- ----------------------------
-- Table structure for tb_data_info
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_data_info (
  account varchar(32) NOT NULL COMMENT '系统账号，数据归属标识',
  data_entity_id varchar(128) NOT NULL COMMENT '数据实体标识',
  data_field_id varchar(64) NOT NULL COMMENT '数据字段标识',
  data_status int(1) NOT NULL DEFAULT '1' COMMENT '状态（1-正常 2-不可用） 默认1',
  data_field_value text NOT NULL COMMENT '数据字段值',
  create_time datetime DEFAULT NULL COMMENT '上传数据的时间',
  modify_time datetime DEFAULT NULL COMMENT '数据修改时间',
  PRIMARY KEY (account,data_entity_id,data_field_id)
) ENGINE=InnoDB COMMENT='数据信息表';


