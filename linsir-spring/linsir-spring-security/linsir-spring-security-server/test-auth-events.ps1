# Spring Security 认证事件测试脚本
# 用于测试各种认证场景和事件监听器

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Spring Security 认证事件测试" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api/auth/login"

# 测试函数
function Test-Login {
    param(
        [string]$Username,
        [string]$Password,
        [string]$Description,
        [string]$ExpectedCode
    )
    
    Write-Host "`n----------------------------------------" -ForegroundColor Gray
    Write-Host "测试：$Description" -ForegroundColor Yellow
    Write-Host "用户名：$Username" -ForegroundColor Gray
    Write-Host "密码：$Password" -ForegroundColor Gray
    Write-Host "----------------------------------------" -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri $baseUrl -Method POST -Body "username=$Username&password=$Password" -ContentType "application/x-www-form-urlencoded" -UseBasicParsing
        Write-Host "✅ 状态码：$($response.StatusCode)" -ForegroundColor Green
        Write-Host "响应：$($response.Content)" -ForegroundColor White
        
        if ($response.StatusCode -eq $ExpectedCode) {
            Write-Host "✓ 符合预期" -ForegroundColor Green
        } else {
            Write-Host "✗ 不符合预期，期望状态码：$ExpectedCode" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ 状态码：$($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        Write-Host "响应：$($_.ErrorDetails.Message)" -ForegroundColor Red
        
        if ($_.Exception.Response.StatusCode.value__ -eq $ExpectedCode) {
            Write-Host "✓ 符合预期" -ForegroundColor Green
        } else {
            Write-Host "✗ 不符合预期，期望状态码：$ExpectedCode" -ForegroundColor Red
        }
    }
}

# 测试场景 1：正常用户登录成功
Test-Login -Username "admin" -Password "admin123" -Description "正常用户登录（admin）" -ExpectedCode 200

# 测试场景 2：普通用户登录成功
Test-Login -Username "user" -Password "user123" -Description "普通用户登录（user）" -ExpectedCode 200

# 测试场景 3：密码错误
Test-Login -Username "admin" -Password "wrong" -Description "密码错误" -ExpectedCode 401

# 测试场景 4：用户不存在
Test-Login -Username "notexist" -Password "any" -Description "用户不存在" -ExpectedCode 401

# 测试场景 5：账户被锁定
Test-Login -Username "locked" -Password "locked123" -Description "账户被锁定（locked）" -ExpectedCode 401

# 测试场景 6：账户被禁用
Test-Login -Username "disabled" -Password "disabled123" -Description "账户被禁用（disabled）" -ExpectedCode 401

# 测试场景 7：账户已过期
Test-Login -Username "expired" -Password "expired123" -Description "账户已过期（expired）" -ExpectedCode 401

# 测试场景 8：密码已过期
Test-Login -Username "credentials_expired" -Password "creds123" -Description "密码已过期（credentials_expired）" -ExpectedCode 401

# 测试场景 9：空用户名
Test-Login -Username "" -Password "password" -Description "空用户名" -ExpectedCode 401

# 测试场景 10：空密码
Test-Login -Username "admin" -Password "" -Description "空密码" -ExpectedCode 401

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  测试完成！" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "提示：请查看服务器日志以查看认证事件的详细输出" -ForegroundColor Yellow
Write-Host "日志中应该包含以下事件：" -ForegroundColor Yellow
Write-Host "  - ✅ 认证成功事件" -ForegroundColor Yellow
Write-Host "  - 🔐 用户名或密码错误事件" -ForegroundColor Yellow
Write-Host "  - 🔒 账户锁定事件" -ForegroundColor Yellow
Write-Host "  - 🚫 账户禁用事件" -ForegroundColor Yellow
Write-Host "  - ⏰ 账户过期事件" -ForegroundColor Yellow
Write-Host "  - 🔑 密码过期事件" -ForegroundColor Yellow
Write-Host ""
