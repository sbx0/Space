var main = new Vue({
    el: '#main',
    data: {
        main_show: true,
        bug_one_show: false,
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        bug_my_data: [],
        bug_list_data: [],
        bug_solved_data: [],
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
                '   <span class="badge badge-primary badge-pill">{{bug.status}}</span>' +
                '</a>',
            methods: {
                // 查询一个反馈详情
                getOne: function (id) {
                    $.ajax({
                        type: "get",
                        url: "bug/" + id,
                        success: function (json) {
                            main.bug_one_data = json;
                            main.bug_one_show = true;
                            main.main_show = false;
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

// 解决按钮点击
$("#bug_solved_btn").click(function () {
    var id = $("#bug_show_id").val();
    $.ajax({
        type: "get",
        url: "bug/solve?id=" + id,
        success: function (json) {
            if (statusCodeToBool(json.status)) {
                alert("操作成功！");
                goBack();
            } else {
                alert(statusCodeToAlert(json.status));
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
});

// 点击标签栏 提交
$("#nav-submit-tab").click(function () {
    main.bug_one_show = false;
});

// 点击标签栏 我的
$("#nav-my-tab").click(function () {
    main.bug_one_show = false;
    $.ajax({
        type: "get",
        url: "bug/my",
        success: function (json) {
            main.bug_my_data = json;
        },
        error: function () {
            alert("网络异常")
        }
    })
});

// 点击标签栏 解决
$("#nav-solve-tab").click(function () {
    main.bug_one_show = false;
    $.ajax({
        type: "get",
        url: "bug/list",
        success: function (json) {
            main.bug_list_data = json;
        },
        error: function () {
            alert("网络异常")
        }
    })
});

// 点击标签栏 已解决
$("#nav-solved-tab").click(function () {
    main.bug_one_show = false;
    $.ajax({
        type: "get",
        url: "bug/solved",
        success: function (json) {
            main.bug_solved_data = json;
        },
        error: function () {
            alert("网络异常")
        }
    })
});

// 反馈详情返回到原来的页面
function goBack() {
    main.main_show = true;
    main.bug_one_show = false;
}

// 填充反馈环境
var ua = navigator.userAgent.toLowerCase();
$("#bug_environment").val(ua);