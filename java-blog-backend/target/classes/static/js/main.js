/**
 * 主页脚本
 */
$(document).ready(function() {
    // 初始化Bootstrap工具提示
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // 检查是否有未读通知
    checkNotifications();
    
    // 每60秒检查一次通知
    setInterval(checkNotifications, 60000);
    
    /**
     * 检查未读通知
     */
    function checkNotifications() {
        // 这里应该是一个AJAX请求，从服务器获取未读通知数量
        // 目前使用模拟数据进行演示
        $.ajax({
            url: '/api/notifications/unread/count',
            type: 'GET',
            success: function(response) {
                // 模拟有3条未读通知
                handleNotifications(3);
            },
            error: function() {
                // 出错时不显示通知
                handleNotifications(0);
            }
        });
    }
    
    /**
     * 处理通知显示
     * @param {number} count 未读通知数量
     */
    function handleNotifications(count) {
        const badge = $('#notification-badge');
        
        if (count > 0) {
            badge.text(count);
            badge.show();
        } else {
            badge.hide();
        }
    }
    
    // 处理登出表单提交
    $('form[action="/api/auth/logout"]').on('submit', function(e) {
        e.preventDefault();
        
        $.ajax({
            url: '/api/auth/logout',
            type: 'POST',
            success: function() {
                // 登出成功，重定向到登录页
                window.location.href = '/login';
            },
            error: function() {
                // 登出失败，显示错误消息
                alert('登出失败，请稍后重试');
            }
        });
    });
});
