var page = 1, size = 10;

function prevAndNext() {
    $.ajax({
        url: '../article/prevAndNext?id=' + $("#id").val() + '&u_id=' + $("#u_id").val(),
        type: 'GET',
        success: function (data) {
            article.prevA = false;
            article.nextA = false;
            article.prev = '';
            article.prev_url = '';
            article.next = '';
            article.next_url = '';
            var prev_id = data.prev_id;
            var prev_title = data.prev_title;
            article.prev_full = prev_title;
            if (prev_id != null) {
                article.prevA = true;
                if (prev_title.length > 7)
                    prev_title = prev_title.substring(0, 7) + "..."
                article.prev = prev_title;
                article.prev_url = "../article/" + prev_id;
            }
            var next_id = data.next_id;
            var next_title = data.next_title;
            article.next_full = next_title;
            if (next_id != null) {
                article.nextA = true;
                if (next_title.length > 7)
                    next_title = next_title.substring(0, 7) + "..."
                article.next = next_title;
                article.next_url = "../article/" + next_id;
            }
        }
    })
}

// Markdown
var markdown = editormd.markdownToHTML("markdown", {
    htmlDecode: "style,script,iframe",  // you can filter tags decode
    emoji: true,
    taskList: true,
    tex: true,  // 默认不解析
    flowChart: true,  // 默认不解析
    sequenceDiagram: true,  // 默认不解析
    path: "../lib/",
});

// 配置图片浏览器
var viewer = new Viewer(document.getElementById('article'));

// 评论
function comment() {
    $.ajax({
        url: '../comment/post',
        type: 'POST',
        data: $("#commentForm").serialize(),
        success: function (data) {
            var status = data.status;
            if (status == 0) {
                alert("发布成功！");
                $("#content").val("");
                loadComment('0');
            } else {
                alert("发布失败！");
            }
        }
    })
    return false;
}

// 上一页评论
function prevComment() {
    page--;
    if (page < 1) {
        page = 1;
    }
    loadComment();
    location.href = "#comment";
}

// 下一页评论
function nextComment() {
    page++;
    if (page > 1000) page = 1000;
    loadComment();
    location.href = "#comment";
}

// 加载评论
function loadComment() {
    $.ajax({
        url: '../comment/list?type=article&id=' + $("#id").val() + '&page=' + page + '&size=' + size,
        type: 'GET',
        success: function (data) {
            if (data.length == 0) {
                article.prevC = false;
                article.nextC = false;
                if (page > 1)
                    loadComment(page--);
            } else if (data.length == 10) {
                article.nextC = true;
                article.prevC = false;
                if (page > 1)
                    article.prevC = true;
            } else if (page == 1 && data.length > 0 && data.length < size) {
                article.prevC = false;
                article.nextC = true;
                if (data.length < size)
                    article.nextC = false;
            } else if (page > 1 && data.length > 0 && data.length == size) {
                article.nextC = true;
                article.prevC = true;
            } else {
                page--;
                article.nextC = false;
                article.prevC = true;
            }
            article.comments = formate(data);
        }
    })
    return false;
}


// 文章列表
var article = new Vue({
    el: '#article',
    data: {
        prev: i18N.prev_article,
        next: i18N.next_article,
        prev_url: '#',
        next_url: '#',
        prev_full: '',
        next_full: '',
        comment: i18N.comment,
        comments: [],
        prevC: false,
        nextC: true,
        prevA: false,
        nextA: false,
    },
    components: {
        'comment-list': {
            props: ['comment'],
            template: '<div><hr id="endLine"><transition name="fade"><div :id="comment.id">' +
                '<div class="media-left"></div><div class="media-body">' +
                '<p class="media-heading">#{{comment.floor}}&nbsp;<a :href="comment.user_id">{{comment.user_name}}</a>&nbsp;{{comment.time}}</p>' +
                '<p>{{comment.content}}</p></div></div></transition></div>',
        },
    },
    created: function () {
        loadComment();
        prevAndNext();
    },
})

// 格式化评论列表的阅读链接与日期格式
function formate(data) {
    for (var i = 0; i < data.length; i++) {
        data[i].id = data[i].id;
        data[i].user_id = "../user/" + data[i].user_id;
        if (data[i].user_id == "../user/null")
            data[i].user_id = "#";
        var d = new Date(data[i].time);
        var times = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
        var time = Format(getDate(times.toString()), "yyyy-MM-dd HH:mm")
        data[i].time = time;
    }
    return data;
}

// 自动登陆
if (login()) {
    $.ajax({
        url: '../user/info',
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

// 删除
function del() {
    $.ajax({
        url: '/article/delete?id=' + $("#id").val() + '&type=0',
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                alert("删除成功");
                location.replace("../index.html");
            } else {
                alert("无权限");
            }
            return false;
        }
    })
    return false;
}