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

var id = getQueryVariable("id").toString()

// 文章列表
var article = new Vue({
    el: '#article',
    data: {
        prev: i18N.prev_article,
        next: i18N.next_article,
        comment: i18N.comment,
    },
})