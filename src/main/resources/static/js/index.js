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

// 文章列表组件
Vue.component('article-list', {
    props: ['article'],
    template: '<transition name="fade"><div class="blog-post">' +
        '<h2 class="blog-post-title"><a :href="article.id" class="text-dark">{{article.title}}</a></h2>' +
        '<p class="blog-post-meta"><a :href ="article.author.id">{{article.author.name}}</a>&nbsp;{{article.time}}</p>' +
        '<div v-html="article.content"></div>' +
        '<a class="continue-read btn btn-light" :href ="article.id">' +
        i18N.continue + i18N.read + '</a></transition>',
})

// 文章列表
var list_article = new Vue({
    el: '#list-article',
    data: {
        more: i18N.read + i18N.more,
        articles: [],
    },
    created: function () {
        $.ajax({
            url: i18N.json.article.list + '?page=1&size=10',
            type: 'GET',
            success: function (data) {
                if (data.length > 0) {
                    list_article.articles = formate(data)
                } else {
                    alert(i18N.query + i18N.result + i18N.null)
                }
            }
        })
    },
})

// 置顶文章组件
Vue.component('article-top', {
    props: ['article'],
    template: '<transition name="fade"><div class="col-md-6"><a :href="article.id">' +
        '<div class="card flex-md-row mb-2 box-shadow-lg h-md-250">' +
        '<div class="card-body d-flex flex-column align-items-start">' +
        '<strong class="d-inline-block mb-2 text-info">' + i18N.top + '</strong>' +
        '<h5 class="mb-0">' +
        '<a class="text-dark" :href="article.id">{{article.title}}</a>' +
        '</h5>' +
        '</div></a></div></transition>',
})

// 置顶文章
var top_article = new Vue({
    el: '#top-article',
    data: {
        articles: [],
    },
    created: function () {
        $.ajax({
            url: i18N.json.article.top + "?size=2",
            type: 'GET',
            success: function (data) {
                if (data.length > 0) {
                    top_article.articles = formate(data)
                }
            }
        })
    },
})

// nav-bar组件
Vue.component('nav-bar', {
    props: ['nav'],
    template: '<a class="p-2 text-muted" href="{{nav.url}}">{{nav.text}}</a>',
})

// 给nav-bar赋值
new Vue({
    el: '#nav',
    data: {
        groceryList: [
            {id: 1, text: i18N.tools, url: ''},
            {id: 2, text: i18N.market, url: ''},
            {id: 3, text: i18N.message, url: ''},
            {id: 4, text: i18N.search, url: ''},
            {id: 5, text: i18N.feedback, url: ''},
            {id: 6, text: i18N.more, url: ''},
        ]
    }
})

// 格式化文章列表的阅读链接与日期格式
function formate(data) {
    for (var i = 0; i < data.length; i++) {
        data[i].id = i18N.json.article.one + data[i].id;
        data[i].author.id = "../user/" + data[i].author.id + "?page=1&size=10";
        var d = new Date(data[i].time);
        var times = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
        data[i].time = times;
    }
    return data
}
