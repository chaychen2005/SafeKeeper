# 数据管理服务控制台使用手册

[控制台](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/console.html)在原有连接区块链节点的基础上增加访问数据管理服务的功能，实现对核心数据的托管及找回操作。控制台原有连接区块链的命令可继续使用，本文档以托管区块链根证书`ca.crt`为例，单独给出用于数据管理服务的命令的使用说明。

用户在启动控制台时，可选地连接区块链或者访问数据管理服务。控制台目前尚未提供同时连接区块链与访问数据管理服务的功能。

## 前置条件

- 搭建数据管理服务
- 搭建Nginx（可选）

## 控制台直连数据管理服务

![](https://fisco-bcos-doc-chaychen.readthedocs.io/en/feature-kms/_images/restore_data.png)

### 1. 获取源码 

```text
git clone https://github.com/FISCO-BCOS/console.git && cd console && git checkout feature-safekeeper
```

### 2. 使用gradlew编译

```text
./gradlew build
```

构建完成后，在根目录console下生成目录dist。

### 3. 修改配置

在dist/conf目录，根据配置模板生成一份实际配置文件applicationContext.xml。

```text
cd dist/conf && cp applicationContext-sample.xml applicationContext.xml
```

修改数据管理服务的IP和Port

```text
sed -i "s/serviceIP:servicePort/${your_server_ip}:${your_server_port}/g" applicationContext.xml
```

例如：

```text
sed -i "s/serviceIP:servicePort/127.0.0.1:9501/g" applicationContext.xml
```

### 4. 启动控制台

在运行数据管理服务的情况下，用户在dist目录以内置管理员身份（admin）启动控制台，并查询管理员可使用的命令：

```text
[app@VM_0_1_centos dist]$ ./start.sh -safekeeper admin Abcd1234
=============================================================================================
Welcome to safekeeper Service console(1.0.9)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
=============================================================================================
[admin:admin]> help
The following commands can be called by the admin
---------------------------------------------------------------------------------------------
addAdminAccount                          Add an admin account.
addVisitorAccount                        Add a visitor account.
deleteAccount                            Delete admin or visitor account.
listAccount                              Display a list of accounts created by yourself.
updatePassword                           Update the password of your own account.
restoreData                              Restore account's escrow data by dataID.
quit(q)                                  Quit console.

---------------------------------------------------------------------------------------------
```

管理员新建访客后，用户也可以访客身份启动控制台，并查询访客可使用的命令：

```text
[app@VM_0_1_centos dist]$ ./start.sh -safekeeper user3 12345678
=============================================================================================
Welcome to safekeeper Service console(1.0.9)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
=============================================================================================
[user3:visitor]> help
The following commands can be called by the visitor
---------------------------------------------------------------------------------------------
updatePassword                           Update the password of your own account.
uploadData                               Upload the escrow data to safekeeper service.
listData                                 Display a list of private keys owned by yourself.
exportData                               Export your own escrow data by dataID.
deleteData                               Delete your own escrow data by dataID.
quit(q)                                  Quit console.

---------------------------------------------------------------------------------------------
```

## 控制台命令

### **addAdminAccount**

管理员运行addAdminAccount，新增一个管理员子账号。参数：

- 账号名称：新增的管理员的账号名称，5-20位字母数字下划线组成，且以字符开头
- 登录密码：新增的管理员的登录密码，6-20位字母数字下划线组成
- 管理员公钥：新增的管理员（非当前管理员）用于加密其子账号托管的核心数据，长度128，不区分大小写

```text
[admin:admin]> addAdminAccount testAdmin 123456 8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49
Add an admin account "testAdmin" successfully.
```

### **addVisitorAccount**

管理员运行addVisitorAccount，新增一个访客子账号。参数：

- 账号名称：新增的访客的账号名称，5-20位字母数字下划线组成，且以字符开头
- 登录密码：新增的访客的登录密码，6-20位字母数字下划线组成

```text
[admin:admin]> addVisitorAccount user1 123456
Add a visitor account "user1" successfully.
```

### **deleteAccount**

管理员运行deleteAccount，删除其创建的子账号，不可删除自身账号。参数：

- 账号名称：删除的子账号名称

```text
[admin:admin]> deleteAccount testUser
Delete an account "testUser" successfully.
```

### **listAccount**

管理员运行listAccount，显示所创建的子账号列表（含自身账号）。

```text
[admin:admin]> listAccount 
The count of account created by "admin" is 3.
---------------------------------------------------------------------------------------------
|             name             |             role             |          createTime          |
|            user1             |           visitor            |     2020-05-20 16:28:10      |
|          testAdmin           |            admin             |     2020-05-20 16:27:51      |
|            admin             |            admin             |     2020-05-19 10:57:24      |
---------------------------------------------------------------------------------------------
```

### **updatePassword**

管理员运行updatePassword，修改自身账号的登录密码。参数：

- 旧登录密码：修改前自身账号的登录密码
- 新登录密码：修改后自身账号的登录密码，不能与旧登录密码重复，6-20位字母数字下划线组成

```text
[admin:admin]> updatePassword Abcd1234 123456
Update password successfully.
```

### **restoreData**

管理员运行restoreData，恢复其生成的访客托管的核心数据。参数：

- 账号名称：需恢复的数据所有者的账号名称
- 数据标识：需恢复的数据标识
- 管理员私钥：管理员自身妥善保管的私钥，用于解密托管的密码密文，长度64，不区分大小写
  
```text
[admin:admin]> restoreData user1 ca_crt 56edd4d56db20ad3b7b387fb8963a639c020282c6a4195f7a699b1b487fb6567
The escrow data "ca_crt" of account "user1" has been recorded in data/ca_crt.txt.
```

### **uploadData**

访客运行uploadData，上传需托管的核心数据。参数：

- 数据文件：存储核心数据信息的文件，存储路径为dist/data/目录
- 加密密码：由访客提供用于对托管的核心数据进行对称加密的密码
- 数据标识：用于唯一标识该访客下的核心数据
  
```text
[user1:visitor]> uploadData ca.crt 12345678 ca_crt
Upload a escrow data "ca_crt" successfully.
```

注：如果在运行uploadData过程中，控制台提示以下错误信息，并且日志中输出以下内容，请参考[解决方案](https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size)进行处理。

```text
# 控制台提示
[user1:visitor]> uploadData ca.crt 12345678 ca_crt
encrypt the escrow data by public key of creator fail.

# 日志输出
[ERROR] [2020-05-28 21:44:13] ECC.encrypt(26) | ECC.encrypt error message: Illegal key size, e: {}
```

### **listData**

访客运行listData，显示其托管的数据列表。
  
```text
[user1:visitor]> listData
The count of escrow data uploaded by "user1" is 2.
---------------------------------------------------------------------------------------------
|                   dataID                    |                 createTime                  |
|                   ca_crt                    |             2020-07-05T19:10:04             |
|                   nodeID1                   |             2020-07-05T19:00:19             |
---------------------------------------------------------------------------------------------
```

### **exportData**

访客运行exportData，找回其托管的核心数据。参数：

- 数据标识：用于唯一标识该访客下的核心数据
- 加密密码：由访客提供用于对托管的核心数据进行对称加密的密码
  
```text
[user1:visitor]> exportData ca_crt 12345678
The escrow data "ca_crt" has been recorded in data/ca_crt.txt.
```

### **deleteData**

访客运行deleteData，删除其托管的核心数据。参数：

- 数据标识：用于唯一标识该访客下的核心数据
  
```text
[user1:visitor]> deleteData ca_crt
Delete the escrow data "ca_crt" successfully.
```

### 控制台通过 Nginx 访问数据管理服务

注：假设当前用户安装nginx的目录为`/data/home/app/`。

#### 1. 源码编译

##### 1.1 获取源码

```text
wget -c https://nginx.org/download/nginx-1.18.0.tar.gz
```

##### 1.2 解压并进入源码目录

```text
tar -zxvf nginx-1.18.0.tar.gz && cd nginx-1.18.0
```

##### 1.3 配置指定安装目录及启用SSL支持

```text
./configure --prefix=/data/home/app/nginx/ --with-http_ssl_module
```

注：安装目录不能为当前目录

##### 1.4 编译安装

```text
make && make install
```

##### 1.5 进入安装目录，启动、检查、停止Nginx

```text
cd /data/home/app/nginx/ 
/data/home/app/nginx/sbin/nginx
ps aux | grep nginx | grep app
./sbin/nginx -s stop
```

#### 2. 证书生成

##### 2.1 创建证书目录

```text
cd /data/home/app/nginx/ && mkdir ssl && cd ssl
```

##### 2.2 创建密钥

```text
openssl genrsa -des3 -out nginx_sk.key 1024
```

过程中输入密码

##### 2.3 生成证书签名请求

```text
openssl req -new -key nginx_sk.key -out nginx_sk.csr
```

过程中输入需密钥密码及按提示操作，可一路回车。

##### 2.4 生成字签名证书

```text
openssl x509 -req -days 365 -in nginx_sk.csr -signkey nginx_sk.key -out nginx_sk.crt
```

过程中输入需密钥密码，执行完毕后检查当前目录是否存在以下三个文件。

```text
nginx_sk.key nginx_sk.csr nginx_sk.crt
```

#### 3. 修改Nginx配置

修改配置文件`conf/nginx.conf`，文件中http部分增加以下内容，指定后端服务的IP与Port，以及负载均衡策略。

```text
    upstream tomcats{
        server 192.168.0.1:9501 weight=1;
        server 192.168.0.2:9501 weight=1;
    }
```

文件中server部分增加以下内容，指定启用SSL服务及所用证书

```text
    server {
        #add/modify begin
        listen       5080 ssl;
        server_name  localhost;

        ssl_certificate     /data/home/app/nginx/ssl/nginx_sk.crt;
        ssl_certificate_key /data/home/app/nginx/ssl/nginx_sk.key;
        ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;
        ssl_ciphers  HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers  on;

        #find this location to modify
        location / {
            proxy_pass https://tomcats;
        }
        #add/modify end

        #charset koi8-r;

        #access_log  logs/host.access.log  main;
```

重新加载Nginx配置文件，过程中输入需密钥密码。

```text
./sbin/nginx -s stop
/data/home/app/nginx/sbin/nginx
```

#### 4. 修改控制台配置

```text
sed -i "s/serviceIP:servicePort/127.0.0.1:5080/g" applicationContext.xml
```