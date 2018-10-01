// 真删除
function real_delete(id) {
    if (confirm("该操作无法撤回！")) {
        $.ajax({
            url: '../article/delete?id=' + id + "&type=1",
            type: 'GET',
            success: function (data) {
                if (data.status == 0) {
                    alert("删除成功");
                    location.replace(location.href)
                } else {
                    alert("无权限");
                }
                return false;
            }
        })
        return false;
    }
}

// 自动登陆
if (login()) {
    $.ajax({
        url: '../user/info',
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                blog_header.login = data.username;
            }
        }
    })
}

// 恢复文章
function recover(id) {
    $.ajax({
        url: '../article/recover?id=' + id,
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                alert("恢复成功");
                location.replace("../article/" + id);
            } else {
                alert("操作失败")
            }
        }
    })
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
})