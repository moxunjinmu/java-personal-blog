/**
 * 登录页面脚本
 */
$(document).ready(function() {
    // 登录表单提交
    $('#login-form').on('submit', function(e) {
        e.preventDefault();
        
        // 获取表单数据
        const username = $('#username').val().trim();
        const password = $('#password').val().trim();
        const captchaCode = $('#captchaCode').val().trim();
        
        // 表单验证
        if (!username) {
            showError('请输入用户名');
            return;
        }
        
        if (!password) {
            showError('请输入密码');
            return;
        }
        
        if (!captchaCode) {
            showError('请输入验证码');
            return;
        }
        
        // 构建请求数据
        const loginData = {
            username: username,
            password: password,
            captchaCode: captchaCode
        };
        
        // 发送登录请求
        $.ajax({
            url: '/api/auth/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(loginData),
            success: function(response) {
                if (response.code === 200) {
                    // 登录成功，跳转到首页
                    window.location.href = '/';
                } else {
                    // 登录失败，显示错误信息
                    showError(response.message || '登录失败，请稍后重试');
                    // 刷新验证码
                    refreshCaptcha();
                }
            },
            error: function(xhr) {
                let errorMsg = '登录失败，请稍后重试';
                
                // 尝试解析错误响应
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {
                    console.error('解析错误响应失败', e);
                }
                
                showError(errorMsg);
                // 刷新验证码
                refreshCaptcha();
            }
        });
    });
    
    // 点击验证码图片刷新
    $('#captcha-image').on('click', function() {
        refreshCaptcha();
    });
    
    // 显示错误信息
    function showError(message) {
        const alertEl = $('#login-alert');
        alertEl.text(message);
        alertEl.removeClass('d-none');
        
        // 3秒后自动隐藏
        setTimeout(function() {
            alertEl.addClass('d-none');
        }, 3000);
    }
    
    // 刷新验证码
    function refreshCaptcha() {
        const captchaImg = $('#captcha-image');
        const timestamp = new Date().getTime();
        captchaImg.attr('src', '/api/auth/captcha?' + timestamp);
        $('#captchaCode').val('').focus();
    }
});
