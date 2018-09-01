// 自动登陆
if (login()) {
    $.ajax({
        url: '/user/info',
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                blog_header.login = data.username;
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
                alert(i18N.network + i18N.error);
            }
            return false;
        }
    })
    return false;
}