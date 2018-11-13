// 自动登陆
if (login()) {
    $.ajax({
        url: 'user/info',
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

// 文章列表
var list_article = new Vue({
    el: '#list-article',
    data: {
        more: i18N.read + i18N.more,
        articles: [],
        comments: [],
    },
    components: {
        'article-list': {
            props: ['article'],
            template: '<transition name="fade"><div class="blog-post">' +
                '<h2 class="blog-post-title"><a :href="article.id" class="text-dark">{{article.title}}</a></h2>' +
                '<p class="blog-post-meta"><a :href ="article.author.id">{{article.author.name}}</a>&nbsp;{{article.time}}</p>' +
                '<div v-html="article.content"></div>' +
                '<a class="text-dark" :href="article.id">' +
                '阅[<span>{{article.views}}</span>]&nbsp;&nbsp;&nbsp;' +
                '评[<span>{{article.comments}}</span>]&nbsp;&nbsp;&nbsp;' +
                '赞[<span>{{article.likes}}</span>]&nbsp;&nbsp;&nbsp;' +
                '踩[<span>{{article.dislikes}}</span>]</a>' +
                '</transition>',
        },
        'comment-list': {
            props: ['comment'],
            template: '<div><transition name="fade"><div :id="comment.id">' +
                '<div class="media-left"></div><div class="media-body">' +
                '<a :href="comment.user_id">{{comment.user_name}}</a>&nbsp;{{comment.time}}</p>' +
                '<p>{{comment.content}}</p></div></div></transition><hr id="endLine"></div>',
        },
    },
    created: function () {
        $.ajax({
            url: i18N.json.article.list,
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    list_article.articles = formate(json);
                } else {
                    alert(i18N.query + i18N.result + i18N.null);
                }
            },
            error: function () {
                alert("网络异常")
            }
        });
        $.ajax({
            url: '../comment/index',
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    list_article.comments = json;
                } else {
                    alert(i18N.query + i18N.result + i18N.null);
                }
            },
            error: function () {
                alert("网络异常")
            }
        });
    },
})

// 置顶文章
var top_article = new Vue({
    el: '#top-article',
    data: {
        articles: [],
    },
    components: {
        'article-top': {
            props: ['article'],
            template: '<transition name="fade"><div class="col-md-6"><a :href="article.id">' +
                '<div class="card flex-md-row mb-2 box-shadow-lg h-md-250">' +
                '<div class="card-body d-flex flex-column align-items-start">' +
                '<strong class="d-inline-block mb-2 text-info">' + i18N.top + '</strong>' +
                '<h5 class="mb-0">' +
                '<a class="text-dark" :href="article.id">{{article.title}}</a>' +
                '</h5>' +
                '</div></a></div></transition>',
        },
    },
    created: function () {
        $.ajax({
            url: i18N.json.article.top,
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    top_article.articles = formate(json)
                }
            },
            error: function () {
                alert("网络异常")
            }
        })
    },
})

// 格式化文章列表的阅读链接与日期格式
function formate(data) {
    for (var i = 0; i < data.length; i++) {
        data[i].id = i18N.json.article.one + data[i].id;
        data[i].author.id = "../user/" + data[i].author.id;
        var d = new Date(data[i].time);
        var times = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
        var time = Format(getDate(times.toString()), "yyyy-MM-dd HH:mm");
        data[i].time = time;
    }
    return data;
}
