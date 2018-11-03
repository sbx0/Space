// 自动登陆
if (login()) {
    $.ajax({
        url: '../user/info',
        type: 'GET',
        success: function (data) {
            if (data.status == 0) {
                blog_header.login = data.username;
                $("#user_name").val(data.username);
                $("#user_name").toggle();
            }
        }
    })
}

function history_chat() {
    $.ajax({
        url: '../message/receive',
        type: 'GET',
        success: function (data) {
            var history = data;
            for (var i = 0; i < history.length; i++) {
                if (data[i].u_name != null)
                    $("#chat-content").append("<p class=\"chat-receive\">" + data[i].u_name + ":" + data[i].content + "</p>");
                else
                    $("#chat-content").append("<p class=\"chat-receive\">" + data[i].ip + ":" + data[i].content + "</p>");
                scrollToBottom();
            }
        }
    })
}

history_chat();
connect();

// 开启连接
function connect() {
    var socket = new SockJS('../stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // 进入公共频道
        stompClient.subscribe('/channel/public', function (response) {
            showCallback(response.body);
        });
    });
}

// 断开连接
function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
}

$("#replayBtn").click(function () {
    replay();
    return false;
})

// 发送姓名
function replay() {
    // var msg = $('#msg').val();
    // stompClient.send("/web/send", {}, msg);
    $.ajax({
        url: '../message/send?to=public',
        type: 'POST',
        data: $("#replayForm").serialize(),
        success: function (data) {
            var status = data.status;
            if (status == 0) {
                alert("发布成功！");
                $('#msg').val("");
            } else if (data.status == 1) {
                alert("1分钟内只能评论一次");
            } else {
                alert("发布失败！");
            }
        }
    })
    return false;
}

// 显示请求
function showResponse(message) {
    $("#response").html(message);
}

// 返回
function showCallback(message) {
    $("#chat-content").append("<p class=\"chat-receive\">" + message + "</p>");
    $("#callback").html(message);
    scrollToBottom();
}

function scrollToBottom() {
    $('#chat-content').scrollTop($('#chat-content')[0].scrollHeight);
}