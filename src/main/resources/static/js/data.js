var data = [];

var chart = new G2.Chart({
    id: 'c1',
    forceFit: true,
    height: 450,
});

G2.track(false);

$.ajax({
    url: "../log/data/views",
    type: "GET",
    async: true,
    success: function (json) {
        chart.source(json, {
            time: {
                alias: '日期',
                range: [0, 1]
            },
            number: {
                alias: '访问数',
            }
        });
        chart.line().position('time*number').size(3);
        chart.render();
    },
    error: function () {
        alert("网络异常")
    }
});

// 自动登陆
if (login()) {
    $.ajax({
        url: 'user/info',
        type: 'GET',
        success: function (json) {
            if (statusCodeToBool(json.status)) {
                blog_header.login = json.username;
            }
        },
        error: function () {
            alert("网络异常")
        }
    })
}