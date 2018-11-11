// 真删除
function real_delete(id) {
    if (confirm("该操作无法撤回！")) {
        $.ajax({
            url: '../article/delete?id=' + id + "&type=1",
            type: 'GET',
            success: function (json) {
                var status = json.status;
                if (statusCodeToBool(status)) {
                    location.replace(location.href)
                }
                alert(statusCodeToAlert(status))
                return false;
            },
            error: function () {
                alert("网络异常")
            }
        });
        return false;
    }
}

// 自动登陆
if (login()) {
    $.ajax({
        url: '../user/info',
        type: 'GET',
        success: function (json) {
            if (statusCodeToBool(json.status)) {
                blog_header.login = json.username;
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 恢复文章
function recover(id) {
    $.ajax({
        url: '../article/recover?id=' + id,
        type: 'GET',
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                location.replace("../article/" + id);
            }
            alert(statusCodeToAlert(status));
        },
        error: function () {
            alert("网络异常")
        }
    });
    return false;
}

// 跳页
function jump_page() {
    var page = $("#page").val()
    var size = $("#size").val()
    var url = "trash?page=" + page + "&size=" + size
    location.replace(url)
}

// 上一页 下一页
new Vue({
    el: '#list-article',
    data: {
        prev: i18N.prev,
        next: i18N.next,
    },
});