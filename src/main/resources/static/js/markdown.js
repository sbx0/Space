var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
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
    },
    created: function () {

    },
});

var editor = editormd("editor", {
    width: "100%",
    height: 640,
    path: "../lib/",
    emoji: true,
    watch: false,
});

// 发布文章
function post() {
    // 判断标题是否存在
    if (checkNullStr($("#title").val())) {
        alert("请输入文章标题");
        return false;
    }
    $("#content").val(editor.getMarkdown());
    // 判断博文内容是否存在
    if (checkNullStr($("#content").val())) {
        alert("请输入文章内容");
        return false;
    }
    $.ajax({
        type: "post",
        url: "article/post",
        data: $("#article-form").serialize(),
        success: function (json) {
            if (statusCodeToBool(json.status)) {
                alert("发布成功");
                location.replace("../index.html");
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
}