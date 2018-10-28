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
    success: function (d) {
        data = d;
        chart.source(data, {
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
})

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