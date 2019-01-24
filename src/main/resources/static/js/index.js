var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        top_data: [{
            "id": 1,
            "title": "加载中",
            "time": "1997-11-19 04:52",
            "author": {"id": 1, "name": "加载中"}
        }],
        article_renew_data_show: false,
        article_renew_data: [],
        article_data: [{
            "id": 1,
            "title": "加载中",
            "time": "1997-11-19 04:52",
            "author": {"id": 1, "name": "加载中"}
        }],
        comment_data: [{
            "entity_id": 1,
            "entity_type": "article",
            "content": "加载中",
            "user_id": 1,
            "user_ip": "192.*.*.1",
            "user_name": "加载中",
            "time": "1997-11-19 04:52",
            "id": 5
        }]
    },
    components: {
        'nav_bar_components': {
            props: ['nav_bar'],
            template: i18N.nav_bar_components
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: i18N.nav_scroller_components
        },
        'top_components': {
            props: ['top'],
            template:
                '<a :href="\'../article/\' + top.id">\n    ' +
                '<div class="d-flex align-items-center p-3 my-3 text-white-50 bg-dark rounded box-shadow">\n        ' +
                '<p class="mt-2 mb-2 mr-3">\n            置顶\n        </p>\n     ' +
                '   <div class="lh-100">\n            ' +
                '<h6 class="mb-0 text-white lh-100">\n                {{top.title}}\n            </h6>\n     ' +
                '       <small>{{top.time}}</small>\n     ' +
                '   </div>\n    ' +
                '</div>\n' +
                '</a>'
        },
        'article_components': {
            props: ['article'],
            template:
                '<div class="media text-muted pt-3">\n ' +
                '   <div class="media-body pb-0 mb-0 lh-125 border-bottom border-gray">\n ' +
                '       <div class="align-items-center w-100">\n            ' +
                '<h6>\n                ' +
                '<strong class="text-gray-dark">\n                    ' +
                '<a :href="\'../article/\' + article.id" class="text-dark">{{article.title}}</a>' +
                '\n                </strong>\n            ' +
                '</h6>\n ' +
                '           <p>\n                ' +
                '<i class="fas fa-user"></i>\n                ' +
                '<a :href="\'../user/\' + article.author.id">{{article.author.name}}</a>\n ' +
                '               &nbsp;\n                <i class="far fa-clock"></i>\n                &nbsp;\n                {{article.time}}\n            ' +
                '</p>\n ' +
                '           <p v-html="article.content" class="mb-1"></p>\n ' +
                '           <ul class="nav float-left mb-2">\n ' +
                '               <li class="nav-item">\n                    ' +
                '<a class="nav-link text-success article-nav-link" :href="\'../article/\' + article.id">\n                        ' +
                '<i class="fas fa-book"></i>\n                        ' +
                '阅\n                        ' +
                '<span>{{article.views}}</span>' +
                '\n                    </a>\n                ' +
                '</li>\n ' +
                '               <li class="nav-item">\n                    ' +
                '<a class="nav-link text-primary article-nav-link" :href="\'../article/\' + article.id">\n                        ' +
                '<i class="far fa-comments"></i>\n                        ' +
                '评\n                        ' +
                '<span>{{article.comments}}</span>' +
                '\n                    </a>\n                ' +
                '</li>\n ' +
                '               <li class="nav-item">\n                    ' +
                '<a class="nav-link text-danger article-nav-link" :href="\'../article/\' + article.id">\n                        ' +
                '<i class="far fa-thumbs-up"></i>\n                        ' +
                '赞\n                        ' +
                '<span>{{article.likes}}</span>' +
                '\n                    </a>\n                ' +
                '</li>\n ' +
                '               <li class="nav-item">\n                    ' +
                '<a class="nav-link text-dark article-nav-link" :href="\'../article/\' + article.id">' +
                '\n                        <i class="far fa-thumbs-down"></i>\n                        ' +
                '踩\n                        ' +
                '<span>{{article.dislikes}}</span>\n                    ' +
                '</a>\n                ' +
                '</li>\n ' +
                '           </ul>\n ' +
                '       </div>\n ' +
                '   </div>\n' +
                '</div>'
        },
        'comment_components': {
            props: ['comment'],
            template:
                '<div class="media text-muted pt-3">\n ' +
                '   <div class="media-body mb-0 small lh-125 border-bottom border-gray">\n ' +
                '       <div class="d-flex justify-content-between align-items-center w-100">\n            ' +
                '<strong class="text-gray-dark">\n                ' +
                '<i class="fas fa-user"></i>\n                ' +
                '<a v-if="comment.user_id != null" :href="\'../user/\' + comment.user_id">\n                    {{comment.user_name}}\n                </a>' +
                '\n                <a v-else>\n                    {{comment.user_name}} : {{comment.user_ip}}\n                </a>\n            ' +
                '</strong>\n            ' +
                '{{comment.time}}\n            ' +
                '<a :href="\'../article/\' + comment.entity_id" :title="comment.entity_id">\n                ' +
                '<i class="fas fa-book"></i>\n                ' +
                '查看' +
                '\n            </a>\n        ' +
                '</div>\n ' +
                '       <p class="d-block mt-3">{{comment.content}}</p>\n    ' +
                '</div>\n' +
                '</div>'
        }
    },
    created: function () {
        $.ajax({
            url: '../article/top',
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    main.top_data = format(json);
                }
            },
            error: function () {
                alert("网络异常")
            }
        });
        $.ajax({
            url: '../article/index',
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    main.article_data = format(json);
                } else {
                    alert(i18N.query + i18N.result + i18N.null);
                }
            },
            error: function () {
                alert("网络异常")
            }
        });
        $.ajax({
            url: '../article/renew',
            type: 'GET',
            success: function (json) {
                if (json.length > 0) {
                    main.article_renew_data = format(json);
                    main.article_renew_data_show = true;
                } else {
                    main.article_renew_data_show = false;
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
                    main.comment_data = json;
                } else {
                    alert(i18N.query + i18N.result + i18N.null);
                }
            },
            error: function () {
                alert("网络异常")
            }
        });
    }
});

// 格式化文章列表的阅读链接与日期格式
function format(data) {
    for (var i = 0; i < data.length; i++) {
        var d = new Date(data[i].time);
        var times = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
        data[i].time = Format(getDate(times.toString()), "yyyy-MM-dd HH:mm");
    }
    return data;
}