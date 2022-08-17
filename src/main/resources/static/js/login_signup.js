// 点击图片切换验证码图片
$("#kaptcha").click(
    function (){
        // 只需要重新设置一下img的scr属性，就会向服务器再次发送/kaptcha请求
        // 然后就会获得一个新的验证码
        $(this).attr("src",
            "/kaptcha")
    }
);

// 验证注册信息是否有效
// 正则表达式
var emailReg = /^[a-zA-Z0-9_\.-]+@([a-zA-Z0-9-]+[\.]{1})+[a-zA-Z]+$/;
var pswReg = /^[a-zA-Z0-9_\-@#&*]{6,12}$/; //6-12位，每一位可以是大小写字母或者数字，或者特殊符号_, -, @, #, &, *
var userNameReg = /^[a-zA-Z_][a-zA-Z_\-\d]{5,9}$/ // 6-10位，开头必须是字母，之后每一位是字母，数字，下划线，或者-
// 初始状态 所有提示信息都隐藏
function resetPrompt(){
    $("#emailError").hide();
    $("#userNameError").hide();
    $("#pswFormatError").hide();
    $("#pswTwiceError").hide();
}
resetPrompt();
// 当输入框失去焦点，则立即检查输入内容是否符合正则表达式
// check email address
$("#floatingInputModal").blur(function (){
    let inputEmail = $("#floatingInputModal").val();
    if(!emailReg.test(inputEmail)){
        $("#emailError").show();
    }else{
        $("#emailError").hide();
    }
});
// check username
$("#floatingUsernameModal").blur(function (){
    let inputUsername = $("#floatingUsernameModal").val();
    if(!userNameReg.test(inputUsername)){
        $("#userNameError").show();
    }else{
        $("#userNameError").hide();
    }
});
// check password format
$("#floatingPasswordModal").blur(function (){
    let inputPsw = $("#floatingPasswordModal").val();
    if(!pswReg.test(inputPsw)){
        $("#pswFormatError").show();
    }else{
        $("#pswFormatError").hide();
    }
});
// check if two entered passwords are the same or not
$("#floatingPasswordAgainModal").blur(function (){
    let inputPswSecond = $("#floatingPasswordAgainModal").val();
    if(inputPswSecond!=$("#floatingPasswordModal").val()){
        $("#pswTwiceError").show();
    }else {
        $("#pswTwiceError").hide();
    }
});

// before submit, check above inputs again
$("#SignUpSubmitBtn").click(function (event){
    resetPrompt();

    let inputEmail = $("#floatingInputModal").val();
    let inputUsername = $("#floatingUsernameModal").val();
    let inputPsw = $("#floatingPasswordModal").val();
    let inputPswSecond = $("#floatingPasswordAgainModal").val();

    if(!emailReg.test(inputEmail)){
        $("#emailError").show();
        event.preventDefault(); //阻止默认行为。对于这个按钮来说，默认行为就是提交表单
    }
    if(!userNameReg.test(inputUsername)){
        $("#userNameError").show();
        event.preventDefault();
    }
    if(!pswReg.test(inputPsw)){
        $("#pswFormatError").show();
        event.preventDefault();
    }
    if(inputPswSecond != inputPsw){
        $("#pswTwiceError").show();
        event.preventDefault();
    }

});

/*--------------------------user avatar crop and upload-----------------------------*/
let CROPPER = null;

function uuid(len, radix) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    var uuid = [], i;
    radix = radix || chars.length;

    if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
        // rfc4122, version 4 form
        var r;

        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';

        // Fill in random data.  At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | Math.random()*16;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
    }

    return uuid.join('');
}

function loadingImg(eve){
    let reader = new FileReader();
    if(eve.target.files[0]){
        reader.readAsDataURL(eve.target.files[0]);
        reader.onload = (e) =>{
            let dataURL = reader.result;
            //console.log(dataURL);
            $('#cropImg').attr('src', dataURL);

            const img = document.getElementById("cropImg");
            console.log(img);
            CROPPER = new Cropper(img, {
                aspectRatio: 1/1,
                viewMode: 0,
                minContainerWidth:400,
                minContainerHeight:400,
                dragMode:'move',
                preview:[ document.querySelector('#previewBox')]
            })
        }
    }
}

$('#floatingAvatarModal').on('change', function (){
    loadingImg(event);
})

function uploadImg(){
    document.querySelector('#floatingAvatarModal').click();
    $('.box').show();
    if(CROPPER){
        CROPPER.destroy(); //上传图片时，销毁之前生成的cropper对象
    }
}

$('#cropImgBtn').click(function (){
    $('.previewBox').removeAttr('hidden');
    $('.previewText').removeAttr('hidden');
    $('#changeCropImgBtn').removeAttr('hidden');
    uploadImg()
})

$('#changeCropImgBtn').click(function (){
    $(".previewBox").removeAttr('hidden');
    $(".previewText").removeAttr('hidden');
    uploadImg();
})

$('#confirm_cropper').click(function (){
    $('.box').hide();
})

$('#SignUpSubmitBtn').click(function (){
    CROPPER.getCroppedCanvas({
        maxWidth: 4096,
        maxHeight: 4096,
        fillColor: '#fff',
        imageSmoothingEnabled: true,
        imageSmoothingQuality: 'high',
    }).toBlob(blob => {
        //console.log(blob);
        let form = document.querySelector('#sign_up_form');
        let formData = new FormData(form);
        formData.append('avatarImg', blob, uuid(16, 16)+'.png')
        console.log(formData.get('email'));
        console.log(formData.get('userName'));
        console.log(formData.get('password'));
        console.log(formData.get('avatarImg'));
        axios({
            url:'/signup',
            method: 'post',
            headers:{'Content-Type':'multipart/form-data'},
            data:formData
        }).then(function (resp){
            let data = resp.data;
            let split = data.split(';');
            let status = split[0];
            let detail = split[1];
            if(status == '500'){
                if(detail == "-1"){
                    alert('This username has been used by others!')
                }else if(detail == "-2"){
                    alert('Sign up failed!')
                }
            }else if(status == '200'){
                alert('Sign up successfully!');
                window.location.replace('/');
            }
        }).catch((err)=>{
            console.log(err)
        })
    })
})