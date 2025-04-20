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
            showError('请输入用户名', 'login');
            return;
        }
        
        if (!password) {
            showError('请输入密码', 'login');
            return;
        }
        
        if (!captchaCode) {
            showError('请输入验证码', 'login');
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
                    // 登录成功，存储用户信息和token到本地存储
                    const userData = response.data.user;
                    const token = response.data.token;
                    
                    // 存储到localStorage
                    localStorage.setItem('user', JSON.stringify(userData));
                    localStorage.setItem('token', token);
                    
                    // 跳转到首页
                    window.location.href = '/';
                } else {
                    // 登录失败，显示错误信息
                    showError(response.message || '登录失败，请稍后重试', 'login');
                    // 刷新验证码
                    refreshCaptcha('login');
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
                
                showError(errorMsg, 'login');
                // 刷新验证码
                refreshCaptcha('login');
            }
        });
    });
    
    // 注册表单提交
    $('#register-form').on('submit', function(e) {
        e.preventDefault();
        
        // 获取表单数据
        const username = $('#register-username').val().trim();
        const email = $('#register-email').val().trim();
        const password = $('#register-password').val().trim();
        const confirmPassword = $('#register-confirm-password').val().trim();
        const captchaCode = $('#register-captchaCode').val().trim();
        
        // 表单验证
        if (!username) {
            showError('请输入用户名', 'register');
            return;
        }
        
        if (!email) {
            showError('请输入邮箱', 'register');
            return;
        }
        
        if (!password) {
            showError('请输入密码', 'register');
            return;
        }
        
        if (password !== confirmPassword) {
            showError('两次输入的密码不一致', 'register');
            return;
        }
        
        if (!captchaCode) {
            showError('请输入验证码', 'register');
            return;
        }
        
        // 构建请求数据
        const registerData = {
            username: username,
            email: email,
            password: password,
            confirmPassword: confirmPassword,
            captchaCode: captchaCode
        };
        
        // 发送注册请求
        $.ajax({
            url: '/api/auth/register',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(registerData),
            success: function(response) {
                if (response.code === 200) {
                    // 注册成功，显示成功消息并切换到登录页
                    alert('注册成功，请登录');
                    $('#show-login').click();
                } else {
                    // 注册失败，显示错误信息
                    showError(response.message || '注册失败，请稍后重试', 'register');
                    // 刷新验证码
                    refreshCaptcha('register');
                }
            },
            error: function(xhr) {
                let errorMsg = '注册失败，请稍后重试';
                
                // 尝试解析错误响应
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {
                    console.error('解析错误响应失败', e);
                }
                
                showError(errorMsg, 'register');
                // 刷新验证码
                refreshCaptcha('register');
            }
        });
    });
    
    // 点击验证码图片刷新
    $('#captcha-image').on('click', function() {
        refreshCaptcha('login');
    });
    
    // 点击注册页验证码图片刷新
    $('#register-captcha-image').on('click', function() {
        refreshCaptcha('register');
    });
    
    // 显示注册卡片
    $('#show-register').on('click', function(e) {
        e.preventDefault();
        $('#card-flip').addClass('flipped');
        refreshCaptcha('register');
    });
    
    // 显示登录卡片
    $('#show-login').on('click', function(e) {
        e.preventDefault();
        $('#card-flip').removeClass('flipped');
        refreshCaptcha('login');
    });
    
    // 显示错误信息
    function showError(message, formType) {
        const alertEl = formType === 'login' ? $('#login-alert') : $('#register-alert');
        alertEl.text(message);
        alertEl.removeClass('d-none');
        
        // 3秒后自动隐藏
        setTimeout(function() {
            alertEl.addClass('d-none');
        }, 3000);
    }
    
    // 刷新验证码
    function refreshCaptcha(formType) {
        const timestamp = new Date().getTime();
        if (formType === 'login') {
            const captchaImg = $('#captcha-image');
            captchaImg.attr('src', '/api/auth/captcha?' + timestamp);
            $('#captchaCode').val('');
        } else {
            const captchaImg = $('#register-captcha-image');
            captchaImg.attr('src', '/api/auth/captcha?' + timestamp);
            $('#register-captchaCode').val('');
        }
    }
    
    // 初始化时刷新登录页验证码
    refreshCaptcha('login');
});
