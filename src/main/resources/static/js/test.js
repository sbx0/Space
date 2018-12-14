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

var your = document.getElementById('your');
Sortable.create(your, {
    group: "game",
});

var your_war = document.getElementById('your_war');
Sortable.create(your_war, {
    group: "game",
});

var opponent_war = document.getElementById('opponent_war');
Sortable.create(opponent_war, {
    group: "game",
});

var opponent = document.getElementById('opponent');
Sortable.create(opponent, {
    group: "game",
});