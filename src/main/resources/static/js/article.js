// 自动登陆
if (login()) {
    $.ajax({
        url: '/user/info',
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                blog_about.isAuthority = true;
                blog_header.login = data.username;
            } else {
                blog_header.login = i18N.login;
            }
        }
    })
}

// 文章列表
var article = new Vue({
    el: '#article',
    data: {
        prev: i18N.prev_article,
        next: i18N.next_article,
        comment: i18N.comment,
    },
})

// 删除
function del() {
    $.ajax({
        url: '/article/delete?id=' + $("#id").val() + "&type=0",
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                alert("删除成功");
                location.replace("../index.html")
            } else {
                alert("无权限");
            }
            return false;
        }
    })
    return false;
}

// 真删除
function real_delete() {
    if (confirm("该操作无法撤回！")) {
        $.ajax({
            url: '/article/delete?id=' + $("#id").val() + "&type=1",
            type: 'GET',
            success: function (data) {
                if (data.status == 0) {
                    alert("删除成功");
                    location.replace("../index.html")
                } else {
                    alert("无权限");
                }
                return false;
            }
        })
        return false;
    }
}