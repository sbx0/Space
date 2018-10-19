// 自动登陆
if (login()) {
    $.ajax({
        url: 'user/info',
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                blog_header.login = data.username;
            }
        }
    })
}

// 文章列表
var list_article = new Vue({
    el: '#list-article',
    data: {
        more: i18N.read + i18N.more,
        articles: [],
    },
    components: {
        'article-list': {
            props: ['article'],
            template: '<transition name="fade"><div class="blog-post">' +
                '<h2 class="blog-post-title"><a :href="article.id" class="text-dark">{{article.title}}</a></h2>' +
                '<p class="blog-post-meta"><a :href ="article.author.id">{{article.author.name}}</a>&nbsp;{{article.time}}</p>' +
                '<div v-html="article.content"></div>' +
                '<a class="btn btn-sm btn-light continue-read" :href ="article.id">' +
                i18N.read + i18N.detail+ '</a></transition>',
        },
    },
    created: function () {
        $.ajax({
            url: i18N.json.article.list,
            type: 'GET',
            success: function (data) {
                if (data.length > 0) {
                    list_article.articles = formate(data);
                } else {
                    alert(i18N.query + i18N.result + i18N.null);
                }
            }
        })
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
            success: function (data) {
                if (data.length > 0) {
                    top_article.articles = formate(data)
                }
            }
        })
    },
})

// 给nav-bar赋值
new Vue({
    el: '#nav',
    data: {
        groceryList: [
            {id: 1, text: i18N.search, url: 'article/search'},
            {id: 2, text: i18N.market, url: 'http://blog.sbx0.cn/market.jsp'},
            {id: 3, text: i18N.message, url: 'http://blog.sbx0.cn/msg.jsp'},
            {id: 4, text: i18N.tools, url: 'http://blog.sbx0.cn/tools.jsp'},
            {id: 5, text: i18N.feedback, url: 'http://blog.sbx0.cn/bugs.jsp'},
            {id: 6, text: i18N.more, url: 'http://blog.sbx0.cn/index'},
        ]
    },
    components: {
        'nav-bar': {
            props: ['nav'],
            template: '<a class="p-2 text-muted" :href="nav.url">{{nav.text}}</a>',
        },
    },
})

// 格式化文章列表的阅读链接与日期格式
function formate(data) {
    for (var i = 0; i < data.length; i++) {
        data[i].id = i18N.json.article.one + data[i].id;
        data[i].author.id = "../user/" + data[i].author.id;
        var d = new Date(data[i].time);
        var times = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
        var time = Format(getDate(times.toString()), "yyyy-MM-dd HH:mm")
        data[i].time = time;
    }
    return data
}
