var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        bug_my_data: [],
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
                '<a :href="bug.id" class="list-group-item d-flex justify-content-between align-items-center">' +
                '   {{bug.name}}' +
                '   <span class="badge badge-primary badge-pill">{{bug.grade}}</span>' +
                '</a>',
        },
    },
    created: function () {

    },
});

var ua = navigator.userAgent.toLowerCase();
$("#bug_environment").val(ua);

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
})