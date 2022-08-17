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

$('#imgReader').on("change", function (){
    loadingImg(event);
})

let CROPPER = null;

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
                aspectRatio: 419.35/225,
                viewMode: 0,
                minContainerWidth:500,
                minContainerHeight:500,
                dragMode:'move',
                preview:[ document.querySelector('.previewBox')]
            })
        }
    }
}

function getData(){
    CROPPER.getCroppedCanvas({
        maxWidth: 4096,
        maxHeight: 4096,
        fillColor: '#fff',
        imageSmoothingEnabled: true,
        imageSmoothingQuality: 'high',
    }).toBlob(blob => {
        //console.log(blob);
        let form = document.querySelector('#create_comm_form');
        let formData = new FormData(form);
        formData.append('cHeaderImg', blob, uuid(16, 16)+'.png')
        console.log(formData.get('cCreator'));
        console.log(formData.get('cName'));
        console.log(formData.get('cAbout'));
        console.log(formData.get('cRules'));
        console.log(formData.get('cHeaderImg'));
        axios({
            url:'/createComm',
            method: 'post',
            headers:{'Content-Type':'multipart/form-data'},
            data:formData
        }).then((response)=>{
            console.log(response)
            let resp = response.data;
            let split = resp.split(';');
            let status = split[0];
            console.log(status);
            let newlyCreatedId = split[1];
            console.log(newlyCreatedId);

            if(status == "200"){
                alert("Community Created Successfully!")
                window.location.replace("/community/"+newlyCreatedId);
            }else{
                alert("Something goes wrong when creating the community. You can do it again!");
            }
        }).catch((err)=>{
            console.log(err)
        })
    })
}

function uploadImg(){
    document.querySelector('#imgReader').click();
    $('.box').show();
    if(CROPPER){
        CROPPER.destroy(); //上传图片时，销毁之前生成的cropper对象
    }
}

$('#cropImgBtn').click(function (){
    $(".previewBox").removeAttr('hidden');
    $(".previewText").removeAttr('hidden');
    $("#changeCropImgBtn").removeAttr('hidden');
    uploadImg();
})

$('#changeCropImgBtn').click(function (){
    $(".previewBox").removeAttr('hidden');
    $(".previewText").removeAttr('hidden');
    uploadImg();
})

/**
 * 提交表单
 */
$('#submitBtn').click(function (e){
    getData();
})

/**
 * 用户确定裁剪效果后，点击确定
 * 把裁剪结果放在预览框里
 * 隐藏cropper组件。
 * 此时就不能再裁剪了
 */
$('#confirm_cropper').click(function (){
    /*CROPPER.getCroppedCanvas({
        maxWidth: 4096,
        maxHeight: 4096,
        fillColor: '#fff',
        imageSmoothingEnabled: true,
        imageSmoothingQuality: 'high',
    }).toBlob(function (blob){
        $('#previewBox').remove('#previewBox img'); //把之前的图先删掉

        let imgURL = window.URL.createObjectURL(blob);
        $('#previewBox').append('<img>');
        $('#previewBox img').attr('src', imgURL)
            .attr('width', 200)
            .attr('height', 107)
    });*/
    $('.box').hide();
})
