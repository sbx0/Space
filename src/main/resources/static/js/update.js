// 配置Markdown编辑器
var editor = editormd("editor", {
    width: "100%",
    height: 640,
    path: "../lib/",
    emoji: true,
    watch: false,
});

// 添加密码
function addPassword() {
    $.ajax({
        url: '../article/addPassword',
        data: $("#add_password_form").serialize(),
        type: 'POST',
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                location.replace(location.href);
            }
            alert(statusCodeToAlert(status));
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 移除密码
function removePassword() {
    var id = $("#id").val()
    $.ajax({
        url: '../article/removePassword?id=' + id,
        type: 'GET',
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                location.replace(location.href);
            }
            alert(statusCodeToAlert(status));
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 设置置顶
function setTop() {
    var id = $("#id").val()
    $.ajax({
        url: '../article/setTop?id=' + id,
        type: 'GET',
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                location.replace(location.href);
            }
            alert(statusCodeToAlert(status));
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 移除置顶
function removeTop() {
    var id = $("#id").val()
    $.ajax({
        url: '../article/removeTop?id=' + id,
        type: 'GET',
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
                location.replace(location.href);
            }
            alert(statusCodeToAlert(status));
        },
        error: function () {
            alert("网络异常")
        }
    })
}

// 自动登陆
if (login()) {
    $.ajax({
        url: '../user/info',
        type: 'GET',
        success: function (json) {
            if (statusCodeToBool(status)) {
                blog_header.login = json.username;
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
} else {
    location.replace("../login.html")
}

// 发布文章
function post() {
    var id = $("#id").val()
    $("#content").val(editor.getMarkdown());
    // 判断标题是否存在
    if (checkNullStr($("#title").val())) {
        alert("请输入文章标题");
        return false;
    }
    // 判断博文内容是否存在
    if (checkNullStr($("#content").val())) {
        alert("请输入文章内容");
        return false;
    }
    $.ajax({
        type: "post",
        url: "../article/post",
        data: $("#article-form").serialize(),
        success: function (json) {
            if (statusCodeToBool(json.status)) {
                alert("发布成功！");
                location.replace("../article/" + id);
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
}