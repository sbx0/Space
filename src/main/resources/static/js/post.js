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
}

// 发布文章
function post() {
    var html = editor.txt.html();
    $("#content").val(html);
    $.ajax({
        type: "post",
        url: "article/post",
        data: $("#article-form").serialize(),
        success: function (data) {
            if (data.status == 0) {
                alert("发布成功！")
            }
        }
    })
}

// 实例化编辑器
var E = window.wangEditor;
var editor = new E('#editor');
editor.customConfig.menus = [
    'head',  // 标题
    'bold',  // 粗体
    'italic',  // 斜体
    'link',  // 插入链接
    'list',  // 列表
    'justify',  // 对齐方式
    'image',  // 插入图片
    'code',  // 插入代码
    'video', // 视频
    'undo',  // 撤销
    'redo'  // 重复
];
editor.customConfig.zIndex = 1;
editor.create();
editor.txt.html($("#content").val());