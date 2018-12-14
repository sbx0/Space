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
            template: i18N.nav_bar_components,
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: i18N.nav_scroller_components
        },
        'bug_components': {
            props: ['bug'],
            template:
                '<a href="javascript:void(0)" v-on:click="getOne(bug.id)"' +
                'class="list-group-item d-flex justify-content-between align-items-center">' +
                '   {{bug.name}}' +
                '   <div v-html="bug.status"></div>' +
                '</a>',
            methods: {
                // 查询一个反馈详情
                getOne: function (id) {
                    $.ajax({
                        type: "get",
                        url: "bug/" + id,
                        success: function (json) {
                            main.bug_one_data = formateOne(json);
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
                $("#nav-solved-tab").click();
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
        url: "bug/list?type=my",
        success: function (json) {
            main.bug_my_data = formate(json);
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
            main.bug_list_data = formate(json);
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
        url: "bug/list?type=solved",
        success: function (json) {
            main.bug_solved_data = formate(json);
        },
        error: function () {
            alert("网络异常")
        }
    })
});

// 自动登陆
if (login()) {
    $.ajax({
        url: '../user/info',
        type: 'GET',
        success: function (json) {
            if (!statusCodeToBool(json.status)) {
                $("#user_ip").html("未登录，以下是IP:" + json.ip + "提交的反馈");
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 反馈详情返回到原来的页面
function goBack() {
    main.main_show = true;
    main.bug_one_show = false;
}

// 填充反馈环境
var ua = navigator.userAgent.toLowerCase();
$("#bug_environment").val(ua);

// 格式化反馈列表
function formate(json) {
    for (var i = 0; i < json.length; i++) {
        formateOne(json[i]);
    }
    return json;
}

// 格式化一个反馈
function formateOne(bug) {
    bug.status = statusToHtml(bug.status);
    bug.grade = gradeToHtml(bug.grade);
    return bug;
}

// 状态码转状态
// 状态 -1 已修复 0 新提交 1 解决中 2 退回
function statusToHtml(status) {
    switch (status) {
        case 0:
            return "<span class='badge badge-secondary badge-pill'>新提交</span>";
            break;
        case 1:
            return "<span class='badge badge-primary badge-pill'>解决中</span>";
            break;
        case 2:
            return "<span class='badge badge-dark badge-pill'>已关闭</span>";
            break;
        case -1:
            return "<span class='badge badge-success badge-pill'>已修复</span>";
            break;
        default:
            return "<span class='badge badge-dark badge-pill'>未知</span>";
    }
}

// 分级码转分级
// 分级 0 1 2 3 4 建议 低级 一般 严重 致命
function gradeToHtml(grade) {
    switch (grade) {
        case 0:
            return "<span class='badge badge-secondary badge-pill'>建议</span>";
            break;
        case 1:
            return "<span class='badge badge-dark badge-pill'>低级</span>";
            break;
        case 2:
            return "<span class='badge badge-primary badge-pill'>一般</span>";
            break;
        case 3:
            return "<span class='badge badge-warning badge-pill'>严重</span>";
            break;
        case 4:
            return "<span class='badge badge-danger badge-pill'>致命</span>";
            break;
        default:
            return "<span class='badge badge-danger badge-pill'>未知</span>";
    }
}