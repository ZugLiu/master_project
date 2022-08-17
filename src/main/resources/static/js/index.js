async function getServerTime(){
    var serverTimeMS = 0;
    await axios.get('/getServerTime')
        .then(function (response){
            serverTimeMS = new Date(response.headers['date']).getTime(); //注意，请求头中的日期的key是全小写
            //console.log(serverTimeMS);
            vue.serverTime = serverTimeMS;
        })
        .catch(function (error){
            console.log(error);
        })
    //console.log(serverTimeMS);
    // 因为async方法修饰的函数会返回一个promise对象，我不知道怎么使用它，所以不设返回值了
    // 直接把后端传进来的参数保存到Vue对象中。
    //return serverTimeMS;
}

/**
 * distance : length of time before ddl
 * idOfSetttimeout: id of setTimeout()
 */
function countdown(distance, elementId) {
    // 这个其实也不能叫countdown了，只是转换distance的格式，并把它显示到前端页面。

    //定义变量 d,h,m,s保存倒计时的时间
    var d,h,m,s;
    if (distance>=0) {
        d = Math.floor(distance/1000/60/60/24);
        h = Math.floor(distance/1000/60/60%24);
        m = Math.floor(distance/1000/60%60);
        s = Math.floor(distance/1000%60);
    }else{
        return;
    }
    //将倒计时赋值到div中
    // document.getElementById(elementId).innerHTML = ;
    $("#"+elementId).text(d+" days "+h+" hours "+m+" minutes "+s + " seconds");
}


var vue = new Vue({
    el:'#indexBody',
    data:{
        pageInfo: 'null',
        topics: 'null',
        serverTime: '',
        communities:'',
        allComm:'',
        communityHeader:'',
    },
    methods:{
        "getTopicsByPageNum":function (pageNum){
            axios({
                "method":"get",
                "url":"/topics/"+pageNum
            }).then(
                function (response){
                    getTopicList(response);
                }
            ).catch(
                function (error){
                    console.log(error)
                }
            )
        },
        "getOptionsByTopic":function (topicId){
            axios({
                url:'/getOptionsByTopic/'+topicId,
                method:'GET',
            }).then(function (response){
                // 获取当前topic的所有options，把它挂载到对应的vue的属性中
                let options = response.data;
                console.log(options);
                let length = vue.topics.length;
                for(var i=0; i<length; i++){
                    if(vue.topics[i].id == topicId){
                        vue.topics[i].options = options;
                    }
                }

                // 获知当前用户已经对此topic下哪些option投过票，投过票的option要高亮
                axios({
                    url:'/getUserVotedOptionFrontend',
                    method: 'POST',
                    data: options,
                }).then(function (response){
                    let data = response.data;
                    for(let i = 0; i<data.length; i++){
                        let userVotedOptionId = data[i].id;
                        $("#vote"+userVotedOptionId).addClass("active");
                    }
                }).catch(function (error){
                    console.log(error);
                })
            }).catch(function (error){
                console.log(error);
            })
        },
        /**
         * 用户给某个option投票
         * @param e
         */
        "vote":function (e){
            let target = e.currentTarget;
            //highlight current target
            target.classList.add("active");

            // get the previous element (#vote_form)
            let prev = target.previousElementSibling;
            // get form id
            let formId = prev.getAttribute("id");
            // get correspond option id
            let optionId = formId.replace(/[^0-9]/ig, "");
            // get vote count change value and set it to 1
            let $voteCountChange = $("#"+formId+"> input[name='voteCountChange']");
            $voteCountChange.val(1);
            // get form element
            let form = document.getElementById(formId);
            // create a FormData object of the form element
            let formData = new FormData(form);
            // submit form date via axios
            axios({
                url:"/option",
                method:"POST",
                headers:{"Content-Type": "multipart/form-data"},
                data: formData
            }).then(function (response){
                // show update responsive msg on page
                $("#updateMsg"+optionId).text(response.data);
            }).catch(function (error){
                console.log(error);
            })
        },
        /**
         * 用户取消对某个option的投票
         * @param e
         */
        "cancelVote":function (e){
            let target = e.currentTarget;
            // de-highlight vote button
            let voteBtn = target.previousElementSibling;
            voteBtn.classList.remove("active");

            // get form id
            let form = target.previousElementSibling.previousElementSibling;
            let formId = form.getAttribute("id");
            // get option id
            let optionId = formId.replace(/[^0-9]/ig, "");
            let $voteCountChange = $("#"+formId+"> input[name='voteCountChange']");
            $voteCountChange.val(-1);
            let formEl = document.getElementById(formId);
            let formData = new FormData(formEl);
            axios({
                url:"/option",
                method:"POST",
                headers:{"Content-Type": "multipart/form-data"},
                data: formData
            }).then(function (response){
                $("#updateMsg"+optionId).text(response.data);
            }).catch(function (error){
                console.log(error);
            })
        },
    },
    beforeMount(){
        axios.get('/topics')
            .then(function (response){
                getTopicList(response);
                getCommunityList();
            })
            .catch(function (error){
                console.log(error)
            });
    }
}
)

//获得某一页的topic列表。页码从url中传给后端。
//根据后端传来的topic列表来为每个topic设置倒计时器。
async function getTopicList(response) {
    vue.pageInfo = response.data;
    vue.topics = vue.pageInfo.list;
    console.log(vue.pageInfo);
    console.log(vue.topics);

    for (let i = 0; i < vue.topics.length; i++) {
        let topic = vue.topics[i];
        // 计算流逝时间
        var publishTime = topic.publishTime;
        var publishTimeMS = new Date(publishTime).getTime();
        var currentTimeMS1 = new Date().getTime(); // get current time first time
        var elapsedHours = Math.round((currentTimeMS1 - publishTimeMS) / 3600000);
        console.log(elapsedHours);
        //如果流逝时间超过24小时，则将单位变为天（四舍五入）
        if (elapsedHours > 24) {
            elapsedHours = Math.round(elapsedHours / 24);
            elapsedHours += ' days';
            topic.publishTime = elapsedHours;

        } else {
            elapsedHours += ' hours';
            topic.publishTime = elapsedHours;
        }

        // 计算距离投票开始的时间 格式 DD days HH hours MM min SS sec

        // get vote start time (ddl)
        var voteStartTimeMS = new Date(topic.voteStartTime).getTime();
        // get vote end time
        let voteEndTimeMS = new Date(topic.voteEndTime).getTime();
        // get server time
        // getServerTime要发送ajax请求，因此是一个异步任务
        await getServerTime();
        var serverTimeMS = new Date(vue.serverTime).getTime();
        // get current (device) time again
        var currentTimeMS2 = new Date().getTime();
        // calculate the deviation 计算服务器时间与设备时间的偏差
        var deviation = serverTimeMS - currentTimeMS2;


        let distance = null;
        // calculate distance between now and vote start time
        if(currentTimeMS2 < voteStartTimeMS){
            // vote does not start
            $('#prompt'+topic.id).text('vote STARTS IN: ')
            distance = voteStartTimeMS - currentTimeMS2 - deviation;
        }else if(voteStartTimeMS < currentTimeMS2 && currentTimeMS2 < voteEndTimeMS){
            // vote started but not ends
            $('#prompt'+topic.id).text('vote ENDS IN: ')
            distance = voteEndTimeMS - currentTimeMS2 - deviation;
        }else{
            $('#prompt'+topic.id).text('vote ENDED!')
            // vote ended
            continue;
        }
        // set timer
        /* setInterval和setTimeout是异步任务，主线程在发起异步任务后不会等待它执行完成后再继续，而是发起之后
        继续执行异步任务之后的同步代码。
        在这个情景下，这个for循环发起了四次setInterval。而当第一个setInterval中的countdown（在1s后）执行的时候，for循环已经结束了（猜测）。
        当回调函数执行countdown的时候，因为闭包的原因，distance和topic都可以从闭包中获取到，但是
        如果distance和topic是var类型：var声明的变量作用域是在函数内。
        如果distance和topic是let类型：let变量的作用域是在块内。
        * */
        setInterval(() => {
            countdown(distance, topic.id);
            distance -= 1000;
        }, 1000);

    }
}

//获得数据库中人数前10的Community
function getCommunityList(){
    axios.get("/community")
        .then(function (response){
            vue.communities = response.data;
            console.log(vue.communities);
            getImg();
        })
        .catch(function (error){
            console.log(error);
        })
}

//获取community的headerImg
function getImg(){
    var comms = vue.communities;
    for(let i = 0; i < comms.length; i++){
        let commId = comms[i].id;
        let imgPath = comms[i].cheaderImg;
        let split = imgPath.split('/');
        let imgName = split[split.length-1];
        axios.get('/img/'+imgName, {
            responseType: 'blob'
        })
            .then(function (response){
                const blob = new Blob([response.data],{
                    type:'application/jpeg;charset=utf-8',
                });
                const url = window.URL.createObjectURL(blob);
                // 获得到图片的url之后立即把它赋给相应img标签的url
                var img = $('#commHeaderImg'+commId);
                img.attr("src", url);
                console.log('img for community '+commId+' loaded successfully!');
                //图片加载完成后释放对象URL，以提升性能
                img.ready(function (){
                    window.URL.revokeObjectURL(url);
                    console.log('url for community '+commId+' revoked successfully!')
                })
            })
            .catch(function (error){
                console.log(error);
            })
    }
}

