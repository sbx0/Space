// 人机检测
function logout() {
    $.ajax({
        type: "get",
        url: "../user/logout",
        success: function (data) {
            location.replace(location.href);
        }
    })
    return false;
}

// 登陆信息提示
var login_div = new Vue({
    el: '#login_div',
    data: {
        info_seen: false,
        form_seen: true,
        info: '加载中',
        website: '空间站',
        username: '用户名',
        password: '密码',
        code: '邀请码（可不填）',
        logout: '退出登陆',
        post: '发布博文',
        home: '用户首页',
        user_home: '../user/1?page=1&size=10',
        info_username: i18N.loading,
        info_level: i18N.loading,
        info_exp: i18N.loading,
        info_integral: i18N.loading,
    },
})

// 如果当前已经登陆
if (login()) {
    $.ajax({
        url: '../user/info',
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                login_div.info_seen = true;
                login_div.form_seen = false;
                login_div.info_username = data.username;
                login_div.info_level = data.level;
                login_div.info_exp = data.exp;
                login_div.info_integral = data.integral;
                login_div.user_home = "../user/" + data.id;
            }
        }
    })
}

// 登陆按钮点击
$("#login").click(function () {
    var username = $("#username").val().trim();
    if (checkNullStr(username)) {
        alert("用户名不能为空")
        return false;
    }
    if (username.indexOf(" ") != -1) {
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
        data: $("#loginForm").serialize(),
        dataType: "json",
        success: function (data) {
            var status = data.status;
            if (status == 0) {
                alert(i18N.login + i18N.success);
                location.replace("index.html");
            } else {
                alert(i18N.please + i18N.check + i18N.username + i18N.and + i18N.password + i18N.is_or_not + i18N.right);
                alert(i18N.login + i18N.fail);
            }
        }
    })
    return false;
})