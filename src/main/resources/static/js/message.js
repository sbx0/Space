// 或许以后会有用
// var msg = $('#msg').val();
// stompClient.send("/web/send", {}, msg);

// 获取历史消息
function history_chat() {
    $.ajax({
        url: '../message/receive',
        type: 'GET',
        async: false,
        success: function (data) {
            var history = data;
            for (var i = 0; i < history.length; i++) {
                if (data[i].u_name != null)
                    $("#chat_content").append("<p class='chat-user-name'>" + data[i].u_name + "</p><p class=\"chat-receive\">" + data[i].content + "</p>");
                else
                    $("#chat_content").append("<p>" + data[i].ip + "</p><p class=\"chat-receive\">" + data[i].content + "</p>");
                scrollToBottom();
            }
        }
    })
}

// 开启连接
function connect() {
    $("#chat_content").append("<p class=\"chat-notification\">已加入聊天</p>");
    scrollToBottom();
    var socket = new SockJS('../stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        // 进入公共频道
        stompClient.subscribe('/channel/public', function (response) {
            printMessage(response.body);
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

// 发送按钮点击
$("#send_button").click(function () {
    send();
    return false;
})

// 发送消息
function send() {
    $.ajax({
        url: '../message/send?to=public',
        type: 'POST',
        data: $("#send_message_form").serialize(),
        success: function (data) {
            var status = data.status;
            if (status == 0) {
                alert("发布成功！");
                $('#message_content').val("");
            } else if (data.status == 1) {
                alert("6秒内只能评论一次");
            } else {
                alert("发布失败！");
            }
        }
    })
    return false;
}

// 打印接收到的消息
function printMessage(message) {
    $("#chat_content").append(message);
    scrollToBottom();
}

// 滚到底部
function scrollToBottom() {
    $('#chat_content').scrollTop($('#chat_content')[0].scrollHeight);
}

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

// 获取历史消息
history_chat();
// 连接
connect();