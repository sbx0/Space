var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        top_data: [],
        article_data: [],
        comment_data: [],
    },
    components: {
        'nav_bar_components': {
            props: ['nav_bar'],
            template: i18N.nav_bar_components,
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: i18N.nav_scroller_components,
        },
        'top_components': {
            props: ['top'],
            template:
                '<a :href="\'../article/\' + top.id">' +
                '<div class="d-flex align-items-center p-3 mt-3 text-white-50 bg-dark rounded box-shadow">' +
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
                '   <div class="media-body pb-0 mb-0 lh-125 border-bottom border-gray">' +
                '       <div class="align-items-center w-100">' +
                '           <h6>' +
                '               <strong class="text-gray-dark">' +
                '                   <a :href="\'../article/\' + article.id" class="text-dark">{{article.title}}</a>' +
                '               </strong>' +
                '           </h6>' +
                '           <p>' +
                '               <i class="fas fa-user"></i>' +
                '               <a :href ="\'../user/\' + article.author.id">{{article.author.name}}</a>' +
                '               &nbsp;<i class="far fa-clock"></i>&nbsp;{{article.time}}' +
                '           </p>' +
                '           <p v-html="article.content" class="mb-1"></p>' +
                '           <ul class="nav float-left mb-2">' +
                '               <li class="nav-item">' +
                '                   <a class="nav-link text-success article-nav-link" :href="\'../article/\' + article.id">' +
                '                       <i class="fas fa-book"></i>' +
                '                       阅' +
                '                       <span>{{article.views}}</span>' +
                '                   </a>' +
                '               </li>' +
                '               <li class="nav-item">' +
                '                   <a class="nav-link text-primary article-nav-link" :href="\'../article/\' + article.id">' +
                '                       <i class="far fa-comments"></i>' +
                '                       评' +
                '                       <span>{{article.comments}}</span>' +
                '                   </a>' +
                '               </li>' +
                '               <li class="nav-item">' +
                '                   <a class="nav-link text-danger article-nav-link" :href="\'../article/\' + article.id">' +
                '                       <i class="far fa-thumbs-up"></i>' +
                '                       赞' +
                '                       <span>{{article.likes}}</span>' +
                '                   </a>' +
                '               </li>' +
                '               <li class="nav-item">' +
                '                   <a class="nav-link text-dark article-nav-link" :href="\'../article/\' + article.id">' +
                '                       <i class="far fa-thumbs-down"></i>' +
                '                       踩' +
                '                       <span>{{article.dislikes}}</span>' +
                '                   </a>' +
                '               </li>' +
                '           </ul>' +
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
                '               <i class="fas fa-user"></i>' +
                '               <a v-if="comment.user_id != null" :href="\'../user/\' + comment.user_id">{{comment.user_name}}</a>' +
                '               <a v-else>{{comment.user_name}} : {{comment.user_ip}}</a>' +
                '           </strong>' +
                '           {{comment.time}}' +
                '           <a :href="\'../article/\' + comment.entity_id" :title="comment.entity_id">' +
                '               <i class="fas fa-book"></i>' +
                '               查看' +
                '           </a>' +
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
                    main.top_data = formate(json);
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
                    main.article_data = formate(json);
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
                    main.comment_data = json;
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
        var d = new Date(data[i].time);
        var times = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
        var time = Format(getDate(times.toString()), "yyyy-MM-dd HH:mm");
        data[i].time = time;
    }
    return data;
}