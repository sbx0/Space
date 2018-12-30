var page = 1, size = 10, totalPage = 0;
var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        prev: i18N.prev_article,
        next: i18N.next_article,
        prev_url: '#',
        next_url: '#',
        prev_full: '',
        next_full: '',
        comment: i18N.comment,
        prevC: false,
        nextC: true,
        prevA: false,
        nextA: false,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        comment_data: [],
        page_data: [1],
    },
    components: {
        'paeg_components': {
            props: ['page'],
            template: '<li class="page-item"><a class="page-link" :href="\'javascript:loadCommentsByPage(\' + page + \')\'">{{page}}</a></li>',
        },
        'nav_bar_components': {
            props: ['nav_bar'],
            template: i18N.nav_bar_components,
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: i18N.nav_scroller_components,
        },
        'comment_components': {
            props: ['comment'],
            template:
                '<div class="media text-muted pt-3">\n    ' +
                '<div class="media-body mb-0 small lh-125 border-bottom border-gray">\n        ' +
                '<div class="d-flex justify-content-between align-items-center w-100">\n            ' +
                '<strong class="text-gray-dark">\n                ' +
                '<span>#{{comment.floor}}&nbsp;\n                    ' +
                '<i class="fas fa-user"></i>\n                    ' +
                '<a v-if="comment.user_id != null" :href="\'../user/\' + comment.user_id">{{comment.user_name}}</a>\n                    ' +
                '<a v-else>{{comment.user_name}} : {{comment.user_ip}}</a>\n                </span>\n            ' +
                '</strong>\n            ' +
                '{{comment.time}}' +
                '<a :href="\'javascript:reply(\' + comment.id + \')\'">\n            ' +
                '<i class="fas fa-reply"></i>\n            ' +
                '回复\n        ' +
                '</a>\n        ' +
                '</div>\n        ' +
                '<p class="d-block mt-3">{{comment.content}}</p>\n    ' +
                '</div>\n' +
                '</div>',
        },
    },
    created: function () {
        $.ajax({
            url: '../article/prevAndNext?id=' + $("#id").val() + '&u_id=' + $("#u_id").val(),
            type: 'GET',
            /**
             * @param json 请求返回的json
             * @param json.prev_id 上一篇文章的id
             * @param json.prev_title 上一篇文章的标题
             * @param json.next_id 下一篇文章的id
             * @param json.next_title 下一篇文章的标题
             */
            success: function (json) {
                main.prevA = false;
                main.nextA = false;
                main.prev = '';
                main.prev_url = '';
                main.next = '';
                main.next_url = '';
                var prev_id = json.prev_id;
                var prev_title = json.prev_title;
                main.prev_full = prev_title;
                if (prev_id != null) {
                    main.prevA = true;
                    if (prev_title.length > 7)
                        prev_title = prev_title.substring(0, 7) + "..."
                    main.prev = prev_title;
                    main.prev_url = "../article/" + prev_id;
                }
                var next_id = json.next_id;
                var next_title = json.next_title;
                main.next_full = next_title;
                if (next_id != null) {
                    main.nextA = true;
                    if (next_title.length > 7)
                        next_title = next_title.substring(0, 7) + "..."
                    main.next = next_title;
                    main.next_url = "../article/" + next_id;
                }
            },
            error: function () {
                alert("网络异常")
            }
        });
        loadComments();
    },
});

// 回复
function reply(id) {
    $.ajax({
        url: '../comment/reply?id=' + id,
        type: 'GET',
        async: false,
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                $("#content").val("回复#" + json.reply_to_floor + "@" + json.reply_to_user_name + ":");
            } else {
                alert(statusCodeToAlert(status))
            }
        },
        error: function () {
            alert("网络异常")
        }
    });
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
var viewer = new Viewer(document.getElementById('markdown'), {
    movable: true,
});

// 评论
function comment() {
    page = 1;
    loadComments();
    $.ajax({
        url: '../comment/post',
        type: 'POST',
        data: $("#commentForm").serialize(),
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                $("#content").val("");
                loadComments();
                var comments = parseInt($("#comments").html());
                $("#comments").html(comments + 1);
            }
            alert(statusCodeToAlert(status));
        },
        error: function () {
            alert("网络异常")
        }
    });
    return false;
}

// 加载第几页的评论
function loadCommentsByPage(p) {
    if (p == page) return false;
    if (p == -1) {
        page = page + 1;
        if (page > totalPage) {
            page = totalPage;
            alert("已经是最后一页了");
            return false;
        }
    } else if (p == -2) {
        page = page - 1;
        if (page < 1) {
            page = 1;
            alert("已经是第一页了");
            return false;
        }
    } else {
        page = p;
    }
    if (page < 1) page = 1;
    if (page > totalPage) page = totalPage;
    window.location.href = "#c";
    loadComments();
}

// 加载评论
function loadComments() {
    $.ajax({
        url: '../comment/list?type=article&id=' + $("#id").val() + '&page=' + page + '&size=' + size,
        type: 'GET',
        success: function (json) {
            main.comment_data = formate(json.comments);
            totalPage = json.totalPage;
            var pageNum = [];
            if (totalPage > 5) {
                pageNum.push(1);
                pageNum.push(parseInt(totalPage / 2));
                pageNum.push(totalPage);
            } else {
                for (var i = 0; i < totalPage; i++) {
                    pageNum.push(i + 1);
                }
            }
            main.page_data = pageNum;
            page = json.currentPage;
        },
        error: function () {
            alert("网络异常")
        }
    });
}

// 赞 踩
function attitude(type) {
    var att = '';
    switch (type) {
        case 1:
            att = 'like';
            break;
        case 2:
            att = 'dislike';
            break;
        default:
            att = '';
    }
    $.ajax({
        url: '../article/' + att + '?id=' + $("#id").val(),
        type: 'GET',
        /**
         * @param json 请求返回的json
         * @param json.status 状态码
         * @param json.dislikes 文章踩数
         * @param json.likes 文章赞数
         */
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                if (json.dislikes != null)
                    $("#dislikes").html(json.dislikes)
                if (json.likes != null)
                    $("#likes").html(json.likes)
            }
            alert(statusCodeToAlert(status))
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 格式化评论列表的阅读链接与日期格式
function formate(json) {
    for (var i = 0; i < json.length; i++) {
        var d = new Date(json[i].time);
        var times = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
        json[i].time = Format(getDate(times.toString()), "yyyy-MM-dd HH:mm");
    }
    return json;
}

// 删除
function del() {
    $.ajax({
        url: '/article/hide?id=' + $("#id").val(),
        type: 'GET',
        success: function (json) {
            var status = json.status;
            alert(statusCodeToAlert(status))
            if (statusCodeToBool(status)) {
                location.replace("../index.html");
            }
            return false;
        },
        error: function () {
            alert("网络异常")
        }
    });
    return false;
}