-- OAuth2 认证服务器数据库表结构

-- 客户端注册信息表
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
    id varchar(100) NOT NULL COMMENT '主键ID',
    client_id varchar(100) NOT NULL COMMENT '客户端ID',
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '客户端ID签发时间',
    client_secret varchar(200) DEFAULT NULL COMMENT '客户端密钥',
    client_secret_expires_at timestamp DEFAULT NULL COMMENT '客户端密钥过期时间',
    client_name varchar(200) NOT NULL COMMENT '客户端名称',
    client_authentication_methods varchar(1000) NOT NULL COMMENT '客户端认证方法',
    authorization_grant_types varchar(1000) NOT NULL COMMENT '授权类型',
    redirect_uris varchar(1000) DEFAULT NULL COMMENT '重定向URI',
    post_logout_redirect_uris varchar(1000) DEFAULT NULL COMMENT '登出后重定向URI',
    scopes varchar(1000) NOT NULL COMMENT '授权范围',
    client_settings varchar(2000) NOT NULL COMMENT '客户端设置(JSON)',
    token_settings varchar(2000) NOT NULL COMMENT '令牌设置(JSON)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_client_id (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2客户端注册信息表';

-- 授权信息表（存储token）
CREATE TABLE IF NOT EXISTS oauth2_authorization (
    id varchar(100) NOT NULL COMMENT '主键ID',
    registered_client_id varchar(100) NOT NULL COMMENT '客户端ID',
    principal_name varchar(200) NOT NULL COMMENT '用户主体名称',
    authorization_grant_type varchar(100) NOT NULL COMMENT '授权类型',
    authorized_scopes varchar(1000) DEFAULT NULL COMMENT '已授权范围',
    attributes blob DEFAULT NULL COMMENT '属性',
    state varchar(500) DEFAULT NULL COMMENT '状态',
    authorization_code_value blob DEFAULT NULL COMMENT '授权码值',
    authorization_code_issued_at timestamp DEFAULT NULL COMMENT '授权码签发时间',
    authorization_code_expires_at timestamp DEFAULT NULL COMMENT '授权码过期时间',
    authorization_code_metadata blob DEFAULT NULL COMMENT '授权码元数据',
    access_token_value blob DEFAULT NULL COMMENT '访问令牌值',
    access_token_issued_at timestamp DEFAULT NULL COMMENT '访问令牌签发时间',
    access_token_expires_at timestamp DEFAULT NULL COMMENT '访问令牌过期时间',
    access_token_metadata blob DEFAULT NULL COMMENT '访问令牌元数据',
    access_token_type varchar(100) DEFAULT NULL COMMENT '访问令牌类型',
    access_token_scopes varchar(1000) DEFAULT NULL COMMENT '访问令牌范围',
    oidc_id_token_value blob DEFAULT NULL COMMENT 'OIDC ID令牌值',
    oidc_id_token_issued_at timestamp DEFAULT NULL COMMENT 'OIDC ID令牌签发时间',
    oidc_id_token_expires_at timestamp DEFAULT NULL COMMENT 'OIDC ID令牌过期时间',
    oidc_id_token_metadata blob DEFAULT NULL COMMENT 'OIDC ID令牌元数据',
    refresh_token_value blob DEFAULT NULL COMMENT '刷新令牌值',
    refresh_token_issued_at timestamp DEFAULT NULL COMMENT '刷新令牌签发时间',
    refresh_token_expires_at timestamp DEFAULT NULL COMMENT '刷新令牌过期时间',
    refresh_token_metadata blob DEFAULT NULL COMMENT '刷新令牌元数据',
    user_code_value blob DEFAULT NULL COMMENT '用户码值（设备码授权）',
    user_code_issued_at timestamp DEFAULT NULL COMMENT '用户码签发时间',
    user_code_expires_at timestamp DEFAULT NULL COMMENT '用户码过期时间',
    user_code_metadata blob DEFAULT NULL COMMENT '用户码元数据',
    device_code_value blob DEFAULT NULL COMMENT '设备码值（设备码授权）',
    device_code_issued_at timestamp DEFAULT NULL COMMENT '设备码签发时间',
    device_code_expires_at timestamp DEFAULT NULL COMMENT '设备码过期时间',
    device_code_metadata blob DEFAULT NULL COMMENT '设备码元数据',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2授权信息表';

-- 授权确认表（用户确认授权）
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL COMMENT '客户端ID',
    principal_name varchar(200) NOT NULL COMMENT '用户主体名称',
    authorities varchar(1000) NOT NULL COMMENT '权限',
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2授权确认表';
