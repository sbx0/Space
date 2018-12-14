var login = new Vue({
    el: '#login',
    data: {
        space: i18N.space,
        not_login: true,
        info_username: i18N.loading,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
    },
    components: {
        'nav_bar_components': {
            props: ['nav_bar'],
            template: i18N.nav_bar_components,
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: i18N.nav_scroller_components,
        },
    },
    created: function () {
        get_user_info();
    },
});

// 如果当前已经登陆
function get_user_info() {
    $.ajax({
        url: '../user/info',
        type: 'GET',
        success: function (json) {
            if (statusCodeToBool(json.status)) {
                login.not_login = false;
                login.info_username = json.username;
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 登陆按钮点击
$("#login_btn").click(function () {
    var username = $("#username").val().trim();
    if (checkNullStr(username)) {
        alert("用户名不能为空")
        return false;
    }
    if (username.indexOf(" ") !== -1) {
        alert("用户名中不能有空格");
        return false;
    }
    if (checkSpecialStr(username)) {
        alert("用户名中不能有特殊字符");
        return false;
    }
    var password = $("#password").val();
    if (checkNullStr(password)) {
        alert("密码不能为空")
        return false;
    }
    $.ajax({
        type: "post",
        url: "../user/login",
        data: $("#login_form").serialize(),
        dataType: "json",
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                alert(i18N.login + i18N.success);
                // location.replace(location.href);
                get_user_info();
            } else {
                alert(i18N.please + i18N.check + i18N.username + i18N.and + i18N.password + i18N.is_or_not + i18N.right);
                alert(i18N.login + i18N.fail);
            }
        },
        error: function () {
            alert("网络异常")
        }
    });
    return false;
});

// 退出登陆
function logout() {
    $.ajax({
        type: "get",
        url: "../user/logout",
        success: function () {
            location.replace(location.href);
        },
        error: function () {
            alert("网络异常")
        }
    });
    return false;
}

// 打开菜单
$(function () {
    'use strict';
    $('[data-toggle="offcanvas"]').on('click', function () {
        $('.offcanvas-collapse').toggleClass('open')
    })
});