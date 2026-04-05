/**
 * 公共工具 JS 文件
 * 
 * 包含常用的工具函数和 AJAX 封装
 */

// 全局配置
const APP_CONFIG = {
    baseURL: '',
    timeout: 10000,
    loginUrl: '/login'
};

/**
 * 封装 AJAX 请求
 */
function ajaxRequest(options) {
    const defaults = {
        type: 'GET',
        dataType: 'json',
        timeout: APP_CONFIG.timeout,
        beforeSend: function(xhr) {
            // 可以在这里添加 CSRF Token 等
            console.log('发送请求:', options.url);
        },
        success: function(response) {
            console.log('请求成功:', response);
        },
        error: function(xhr, status, error) {
            console.error('请求失败:', status, error);
            
            // 处理 401 未授权
            if (xhr.status === 401) {
                alert('未授权，请登录');
                window.location.href = APP_CONFIG.loginUrl;
            }
            
            // 处理 403 禁止访问
            if (xhr.status === 403) {
                alert('无权访问');
            }
            
            // 处理 500 服务器错误
            if (xhr.status === 500) {
                alert('服务器错误，请稍后重试');
            }
        }
    };
    
    // 合并配置
    const settings = $.extend({}, defaults, options);
    
    // 发送请求
    return $.ajax(settings);
}

/**
 * GET 请求
 */
function get(url, data, successCallback) {
    return ajaxRequest({
        url: url,
        data: data,
        success: successCallback
    });
}

/**
 * POST 请求
 */
function post(url, data, successCallback) {
    return ajaxRequest({
        url: url,
        type: 'POST',
        data: data,
        success: successCallback
    });
}

/**
 * 统一的成功响应处理
 */
function handleSuccess(response, callback) {
    if (response && response.code === 200) {
        if (callback) callback(response.data);
    } else {
        const message = response.message || '操作失败';
        alert(message);
    }
}

/**
 * 统一的错误响应处理
 */
function handleError(xhr, status, error) {
    console.error('请求错误:', status, error);
    
    let message = '网络错误，请稍后重试';
    
    if (xhr.status === 401) {
        message = '未授权，请登录';
        window.location.href = APP_CONFIG.loginUrl;
    } else if (xhr.status === 403) {
        message = '无权访问';
    } else if (xhr.status === 404) {
        message = '请求的资源不存在';
    } else if (xhr.status === 500) {
        message = '服务器错误';
    }
    
    alert(message);
}

/**
 * 表单序列化
 */
function serializeForm(formSelector) {
    const formData = {};
    $(formSelector).serializeArray().map(function(x) {
        formData[x.name] = x.value;
    });
    return formData;
}

/**
 * 显示加载提示
 */
function showLoading(message) {
    message = message || '加载中...';
    // 可以集成 layer 等 UI 库
    console.log(message);
}

/**
 * 隐藏加载提示
 */
function hideLoading() {
    console.log('加载完成');
}

/**
 * 跳转到登录页
 */
function goToLogin() {
    window.location.href = APP_CONFIG.loginUrl;
}

/**
 * 退出登录
 */
function logout() {
    if (confirm('确定要退出登录吗？')) {
        post(APP_CONFIG.baseURL + '/api/auth/logout', {}, function() {
            alert('退出登录成功');
            window.location.href = '/';
        });
    }
}

// 页面加载完成后执行
$(document).ready(function() {
    console.log('公共 JS 已加载');
    
    // 可以在这里初始化一些全局功能
    // 例如：检查登录状态、初始化提示框等
});
