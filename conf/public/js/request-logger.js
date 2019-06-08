XMLHttpRequest.prototype.realSend = XMLHttpRequest.prototype.send;
XMLHttpRequest.prototype.send = function (value) {
    this.addEventListener("load", function () {
        console.log("Requested URL:", this.responseURL, value);
    }, false);
    this.realSend(value);
};