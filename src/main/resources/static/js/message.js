// 或许以后会有用
// var msg = $('#msg').val();
// stompClient.send("/web/send", {}, msg);

window.location.hash = "#go";

var reconnect_times = 0;

// 获取历史消息
function history_chat() {
    $("#chat_content").html("");
    $.ajax({
        url: '../message/receive',
        type: 'GET',
        async: false,
        success: function (data) {
            var history = data;
            for (var i = 0; i < history.length; i++) {
                printMessage(data[i], "receive");
                scrollToBottom();
            }
        }
    })
}

// 开启连接
function connect() {
    // 建立连接对象（还未发起连接）
    var socket = new SockJS('../stomp');
    // 获取STOMP子协议的客户端对象
    var stompClient = Stomp.over(socket);
    // 向服务器发起WebSocket连接并发送CONNECT帧
    stompClient.connect({},
        function connectCallback() {
            // 连接成功时（服务器响应 CONNECTED 帧）的回调方法
            printMessage("已加入聊天", "notification");
            // 订阅公共频道
            stompClient.subscribe('/channel/public', function (response) {
                printMessage(response.body);
            });
        },
        function errorCallBack() {
            // 连接失败时（服务器响应 ERROR 帧）的回调方法
            printMessage("连接丢失", "notification");
            reconnect();
        }
    );
}

// 重连10次
function reconnect() {
    if (reconnect_times > 9) {
        printMessage("重试失败次数太多，请稍后再试。", "notification");
        return false;
    }
    reconnect_times++;
    printMessage("第" + reconnect_times + "次自动重连...", "notification");
    var socket = new SockJS('../stomp');
    var stompClient = Stomp.over(socket);
    stompClient.connect({},
        function connectCallback() {
            reconnect_times = 1;
            // 连接成功时（服务器响应 CONNECTED 帧）的回调方法
            printMessage("已加入聊天", "notification");
            // 订阅公共频道
            stompClient.subscribe('/channel/public', function (response) {
                printMessage(response.body);
            });
        },
        function errorCallBack() {
            // 连接失败时（服务器响应 ERROR 帧）的回调方法
            printMessage("第" + reconnect_times + "次重连失败", "notification");
            setTimeout(function () {
                history_chat();
                reconnect();
            }, 5000)
        }
    );
    return false;

}

//
// // 断开连接
// function disconnect() {
//     if (stompClient != null) {
//         stompClient.disconnect();
//     }
//     setConnected(false);
// }

// 发送按钮点击
$("#send_button").click(function () {
    send();
    return false;
})

// 发送消息
function send() {
    if (checkNullStr($("#message_content").val())) {
        alert("不能为空！")
        return false;
    }
    if (reconnect_times > 0) return false;
    $.ajax({
        url: '../message/send?to=public',
        type: 'POST',
        async: false,
        data: $("#send_message_form").serialize(),
        success: function (data) {
            var status = data.status;
            if (status == 0) {
                alert("发布成功！");
                $('#message_content').val("");
            } else if (data.status == 1) {
                alert("你太快了，慢点");
            } else {
                alert("发布失败！");
            }
        }
    })
    return false;
}

// 打印接收到的消息
function printMessage(message, type) {
    switch (type) {
        case "notification":
            $("#chat_content").append("<p class=\"chat-notification\">" + message + "</p>");
            break;
        case "receive":
            if (message.u_name != null)
                $("#chat_content").append(
                    "<p class='chat-user-name'>" + message.u_name + "</p>" +
                    "<p class=\"chat-receive\">" + message.send_time + "：" + message.content + "</p>");
            else
                $("#chat_content").append(
                    "<p class='chat-user-name'>" + message.ip + "</p>" +
                    "<p class=\"chat-receive\">" + message.send_time + "：" + message.content + "</p>");
            break
        default:
            $("#chat_content").append(message);
    }
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
            } else {
                $("#user_ip").html("您将以" + data.ip + "作为标识参与聊天");
            }
        }
    })
}

// 获取历史消息
history_chat();
// 连接
connect();