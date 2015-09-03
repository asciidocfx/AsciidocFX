function isHtml(html) {
    var div = $("<div></div>");
    div.append(html);
    return div.find("div,span,p,br,b,strong,h1,h2,h2,h4,h5,h6,pre,code,table,section,img,a,sub,sup,del,u").length > 5;
}