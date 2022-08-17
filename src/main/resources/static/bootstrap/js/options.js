// post__comment class initially hide
$(".post__comment").hide();

$(".button--approve").click(function () {
    $(this).addClass("active");
});

$(".button--deny").click(function (){
    $(this).prev().removeClass("active");
})

/*$(".comment").click(function () {
    //$(this).parent().parent().toggleClass("post--commenting");
    let id = this.id; // handler 中的this指向当前DOM元素
    let idNum = id.replace(/[^0-9]/ig,""); //去掉id中的非数字字符
    $("#postComment"+idNum).toggle();
});*/

/*$(".button--flag").click(function () {
    $(this).parent().parent().toggleClass("post--commenting");
});*/


$(".button--confirm").click(function () {
    //$(this).parent().parent().parent().parent().parent().toggleClass("post--commenting");
    let id = this.id; // handler 中的this指向当前DOM元素
    let idNum = id.replace(/[^0-9]/ig,""); //去掉id中的非数字字符
    $("#postComment"+idNum).toggle();
});

$(".button.cancel").click(function () {
    //$(this).parent().parent().parent().parent().parent().toggleClass("post--commenting");
    let id = this.id; // handler 中的this指向当前DOM元素
    let idNum = id.replace(/[^0-9]/ig,""); //去掉id中的非数字字符
    $("#postComment"+idNum).toggle();
});