var editor = editormd("editor", {
    width: "100%",
    height: 640,
    path: "../lib/",
    emoji: true,
});

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
} else {
    location.replace("../login.html")
}

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
        success: function (data) {
            if (data.status == 0) {
                alert("发布成功");
                location.replace("../index.html");
            }
        }
    })
}