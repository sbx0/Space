var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        bug_my_data: [],
        bug_one_data: {
            id: '_',
            name: '未选择',
            grade: '未选择',
            status: '未选择',
            content: '未选择',
            submit_time: '未选择',
        },

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
        'bug_components': {
            props: ['bug'],
            template:
                '<a href="javascript:void(0)" v-on:click="getOne(bug.id)"' +
                'class="list-group-item d-flex justify-content-between align-items-center">' +
                '   {{bug.name}}' +
                '   <span class="badge badge-primary badge-pill">{{bug.grade}}</span>' +
                '</a>',
            methods: {
                // 查询一个反馈详情
                getOne: function (id) {
                    $.ajax({
                        type: "get",
                        url: "bug/" + id,
                        success: function (json) {
                            main.bug_one_data = json;
                        },
                        error: function () {
                            alert("网络异常")
                        }
                    })
                }
            },
        },
    },
    created: function () {

    },
});

// 反馈提交按钮点击提交
$("#bug_post_btn").click(function () {
    $.ajax({
        type: "POST",
        url: "bug/post",
        data: $("#bug_post_form").serialize(),
        success: function (json) {
            if (statusCodeToBool(json.status)) {
                alert("提交成功！");
                $("#bug_name").val("");
                $("#bug_content").val("");
                $("#nav-my-tab").click();
            } else {
                alert(statusCodeToAlert(json.status));
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
});

// 点击标签栏 我的
$("#nav-my-tab").click(function () {
    $.ajax({
        type: "get",
        url: "bug/list",
        success: function (json) {
            main.bug_my_data = json;
        },
        error: function () {
            alert("网络异常")
        }
    })
});

// 填充反馈环境
var ua = navigator.userAgent.toLowerCase();
$("#bug_environment").val(ua);