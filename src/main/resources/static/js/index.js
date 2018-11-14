var index = new Vue({
    el: '#index',
    data: {
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        top_data: [],
        article_data: [],
        comment_data: [],
    },
    components: {
        'nav_bar_components': {
            props: ['nav_bar'],
            template: '<li class="nav-item"><a class="nav-link" :href="nav_bar.url" v-html="nav_bar.text"></a></li>',
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: '<a class="nav-link" :href="nav_scroller.url" v-html="nav_scroller.text"></a>',
        },
        'top_components': {
            props: ['top'],
            template:
                '<a :href="top.id">' +
                '<div class="d-flex align-items-center p-3 my-3 text-white-50 bg-dark rounded box-shadow">' +
                '   <p class="mt-2 mb-2 mr-3">置顶</p>' +
                '   <div class="lh-100">' +
                '       <h6 class="mb-0 text-white lh-100">{{top.title}}</h6>' +
                '       <small>{{top.time}}</small>' +
                '   </div>' +
                '</div>' +
                '</a>',
        },
        'article_components': {
            props: ['article'],
            template:
                '<div class="media text-muted pt-3">' +
                '   <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">' +
                '       <div class="align-items-center w-100">' +
                '           <h6>' +
                '               <strong class="text-gray-dark">' +
                '                   <a :href="article.id" class="text-dark">{{article.title}}</a>' +
                '               </strong>' +
                '           </h6>' +
                '           <p>' +
                '               <a :href ="article.author.id">{{article.author.name}}</a>' +
                '               &nbsp;{{article.time}}' +
                '           </p>' +
                '           <p v-html="article.content"></p>' +
                '           <a class="text-dark" :href="article.id">' +
                '               阅[<span>{{article.views}}</span>]&nbsp;&nbsp;&nbsp;' +
                '               评[<span>{{article.comments}}</span>]&nbsp;&nbsp;&nbsp;' +
                '               赞[<span>{{article.likes}}</span>]&nbsp;&nbsp;&nbsp;' +
                '               踩[<span>{{article.dislikes}}</span>]' +
                '           </a>' +
                '       </div>' +
                '   </div>' +
                '</div>',
        },
        'comment_components': {
            props: ['comment'],
            template:
                '<div class="media text-muted pt-3">' +
                '   <div class="media-body mb-0 small lh-125 border-bottom border-gray">' +
                '       <div class="d-flex justify-content-between align-items-center w-100">' +
                '           <strong class="text-gray-dark">' +
                '               <a :href="comment.user_id">{{comment.user_name}}</a>' +
                '           </strong>' +
                '           {{comment.time}}' +
                '           <a href="#">查看</a>' +
                '       </div>' +
                '       <p class="d-block mt-3">{{comment.content}}</p>' +
                '   </div>' +
                '</div>',
        },
    },
    created: function () {
        $.ajax({
            url: '../article/top',
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    index.top_data = formate(json);
                }
            },
            error: function () {
                alert("网络异常")
            }
        })
        $.ajax({
            url: '../article/index',
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    index.article_data = formate(json);
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
                    index.comment_data = json;
                } else {
                    alert(i18N.query + i18N.result + i18N.null);
                }
            },
            error: function () {
                alert("网络异常")
            }
        });
    },
});

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

// 打开菜单
$(function () {
    'use strict';
    $('[data-toggle="offcanvas"]').on('click', function () {
        $('.offcanvas-collapse').toggleClass('open')
    })
});