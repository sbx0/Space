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
            template: '<li class="nav-item"><a class="nav-link" :href="nav_bar.url" v-html="nav_bar.text"></a></li>',
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: '<a class="nav-link" :href="nav_scroller.url" v-html="nav_scroller.text"></a>',
        },
    },
    created: function () {

    },
});

var data = [];

// var chart = new G2.Chart({
//     id: 'c1',
//     forceFit: true,
//     height: 450,
// });
//
// G2.track(false);

GM.Global.pixelRatio = 2;
var Util = GM.Util;
var chart = new GM.Chart({
    id: 'c1'
});
var defs = {
    time: {
        type: 'timeCat',
        mask: 'yyyy-mm-dd',
        tickCount: 3,
        range: [0, 1]
    },
    number: {
        tickCount: 5,
        min: 0
    }
};
//配置刻度文字大小，供PC端显示用(移动端可以使用默认值20px)
chart.axis('number', {
    label: {
        fontSize: 14
    }
});
chart.axis('time', {
    label: {
        fontSize: 14
    }
});

$.ajax({
    url: "../log/data/views",
    type: "GET",
    async: true,
    success: function (json) {
        chart.source(json, defs);
        chart.line().position('time*number');
        chart.render();
        // chart.line().position('time*number').size(3);
        // chart.render();
    },
    error: function () {
        alert("网络异常")
    }
});