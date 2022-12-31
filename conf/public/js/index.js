var soket = new SockJS("/ws");

function refreshUI(data) {

    morphdom(document.documentElement, data, morphdomOptions);

}

soket.onmessage = function (e) {
    refreshUI(e.data);
};

soket.onerror = soket.onclose = function (e) {
    $(".row.connection-closed").show();
};