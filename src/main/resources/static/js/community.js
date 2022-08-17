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
    el:"#container",
    data:{
        pageInfo: 'null',
        topics: 'null',
        serverTime:'',
        commId:'',
        optionCounter:0,
    },
    methods:{
        "joinComm":function (e){
            let commId = $('#commId').text();
            axios({
                url:'/joinComm/'+ commId,
                method: 'get'
            }).then(function (response){
                let msg = response.data;
                if(msg === "join successfully"){
                    alert("Join Successfully!")
                }else if(msg === "join failed"){
                    alert("Join Failed! You have already joined this community!")
                }
            }).catch(function (err){
                console.log(err);
            })
        },
        "leaveComm":function (e){
            let commId = $('#commId').text();
            axios({
                url:'/leaveComm/'+commId,
                method:'get'
            }).then(function (response){
                let msg = response.data;
                if(msg === "leave successfully"){
                    alert("Leave Successfully!");
                }else if(msg === "leave failed"){
                    alert("Leave Failed!");
                }
            }).catch(function (err){
                console.log(err);
            })
        },
        "getTopicsByCommPN":function (commId, pageNum){
            axios({
                method:"get",
                url:"/topics/"+commId+"/"+pageNum,
            }).then(
                function (response){
                    getTopicList(response)
                }
            ).catch(function (error){
                console.log(error);
            })

        },
        "createOption":function (){
            this.optionCounter+=1;
            var optionNo = this.optionCounter;
            // get option group div
            let elementById = document.getElementById('optionGroup');
            // create a text area el for option content
            var htmlTextAreaElement = document.createElement("textarea");
            htmlTextAreaElement.className="form-control option-text-area";
            htmlTextAreaElement.name="option"+optionNo;
            htmlTextAreaElement.form=$("#createTopicForm");
            htmlTextAreaElement.placeholder="Enter your option content";
            // create an input el as the title of option
            let htmlInputElement = document.createElement("input");
            htmlInputElement.className="form-control optionTitles";
            htmlInputElement.name="optionTitle"+optionNo;
            htmlInputElement.placeholder="Enter Option Title";
            //htmlInputElement.value="Option"+optionNo; // set default value of title

            // create a span el
            let htmlSpanElement = document.createElement("span");
            htmlSpanElement.className="input-group-text";
            htmlSpanElement.textContent="Option "+optionNo;
            // create a btn el
            let htmlBtnElement = document.createElement("button");
            htmlBtnElement.type="button";
            htmlBtnElement.className="btn-close btn-option-del";
            htmlBtnElement.addEventListener("click", function (e){
                let target = e.target;
                let span = target.parentNode;
                let textarea = span.nextSibling;
                let div = span.parentNode;
                div.removeChild(span);
                div.removeChild(textarea);
                //console.log("del btn is pressed!")
            });
            // add btn as span's child
            htmlSpanElement.appendChild(htmlBtnElement);
            // create a div el
            let htmlDivElement = document.createElement("div");
            // add span, input, and textarea as div's children
            htmlDivElement.appendChild(htmlSpanElement);
            htmlDivElement.appendChild(htmlInputElement);
            htmlDivElement.appendChild(htmlTextAreaElement);
            // add div as option group's child
            elementById.appendChild(htmlDivElement);

        },
        "resetOptionCounter":function (){
            this.optionCounter=0;
        },
        "check":function (e){
            let target = e.target;
            //target.setAttribute("checked","true");

            if(target.getAttribute("id")=="customisePrep"){
                $(".prepTime").removeAttr("readonly");
            }else{
                $(".durationTime").removeAttr("readonly");
            }
        },
        "uncheckCus":function (e){
            let target = e.target;
            if(target.getAttribute("name") == "voteStartsIn"){
                $(".prepTime").attr("readonly", "true");
                $("#customisePrep").removeAttr("checked");
            }else{
                $(".durationTime").attr("readonly", "true");
            }
            //$("#customiseDuration").removeAttr("checked");
        },
        "validateCustomisedTime":function (e){
            let target = e.target;
            let value = Number(target.value);
            let classes = target.className;
            // to record time division the target element belongs to
            let div;
            if(classes.includes("prep")){
                div = "#customisedPrepMsg";
            }else if(classes.includes("duration")){
                div="#customisedDurationMsg";
            }
            //console.log(div);
            if(value==NaN || !Number.isInteger(value)){
                $(div).text("input must be an integer!");
            }else{
                $(div).text("");
                if(classes.includes("customisedDays")){
                    if(value<0){
                        $(div).text("day must larger than or equal to 0")
                    }else{
                        $(div).text("");
                    }
                }else if(classes.includes("customisedHours")){
                    if(value < 0 || 23 < value){
                        $(div).text("hour must between (0, 24)");
                    }else{
                        $(div).text("");
                    }
                }else if(classes.includes("customisedMinutes")){
                    if(value < 0 || 59 < value){
                        $(div).text("minute must between (0, 59)");
                    }else{
                        $(div).text("");
                    }
                }else if(classes.includes("customisedSeconds")){
                    if(value < 0 || 59 < value){
                        $(div).text("second must between (0, 59)");
                    }else{
                        $(div).text("");
                    }
                }
            }
        },
        "validateBeforeSubmit":function (e){
            //reset validation msg
            $("#validationMsg").text("");
            //validate title and content
            let title = $("#Title").val();
            if(title.length==0){
                $("#validationMsg").text("title cannot be null!");
                e.preventDefault();
                return;
            }
            let content = $("#Content").val();
            if(content.length==0){
                $("#validationMsg").text("content cannot be null!");
                e.preventDefault();
                return;
            }

            /*// validate preparation time format
            let $customisePrep = $("#customisePrep");
            let customisePrepChecked = $customisePrep[0].checked;
            // if user customises preparation time:
            if(customisePrepChecked){
                let days = Number($("#startInDays").val());
                let hours = Number($("#startInHours").val());
                let min = Number($("#startInMinutes").val());
                let sec = Number($("#startInSeconds").val());

                if(days==NaN || days< 0
                    ||hours==NaN|| hours<0 || 23<hours
                    ||min==NaN||min<0||59<min
                    ||sec==NaN||sec<0||59<sec){
                    $("#validationMsg").text("Cannot publish topic! Some input is wrong!")
                    e.preventDefault();
                    return;
                }
                //$("#createTopicForm").submit();
                console.log(days+hours+min+sec);
            }else{ // else, user must choose a pre-set preparation time (check one radio)
                let $voteStartsIn = $(".voteStartsIn");
                let numOfChecked=0;
                for(var i = 0; i<$voteStartsIn.length; i++){
                    if($voteStartsIn[i].checked){ // if one of radios is checked
                        numOfChecked++;
                    }
                }
                if(numOfChecked==0){ // if user checks no pre-set time, error
                    $("#validationMsg").text("Cannot publish topic! vote start time is wrong!")
                    e.preventDefault();
                    return;
                }
            }

            // validate duration time format
            let $customiseDuration = $("#customiseDuration");
            let customiseDurationChecked = $customiseDuration[0].checked;
            if(customiseDurationChecked){
                let days = Number($("#durationDays").val());
                let hours = Number($("#durationHours").val());
                let min = Number($("#durationMinutes").val());
                let sec = Number($("#durationSeconds").val());

                if(days==NaN || days< 0
                ||hours==NaN|| hours<0 || 23<hours
                ||min==NaN||min<0||59<min
                ||sec==NaN||sec<0||59<sec){
                    $("#validationMsg").text("Cannot publish topic! Some input is wrong!")
                    e.preventDefault();
                }

            }else{
                let $durationTime = $(".votingDuration");
                let numOfChecked=0;
                for(var i = 0; i<$durationTime.length; i++){
                    if($durationTime[i].checked){ // if one of radios is checked
                        numOfChecked++;
                    }
                }
                if(numOfChecked==0){
                    $("#validationMsg").text("Cannot publish topic! vote end time is wrong!")
                    e.preventDefault();
                    return;
                }
            }*/

            // validate option title: option titles cannot be null or repeated
            let $optionTitles = $(".optionTitles");
            let length = $optionTitles.length;
            for(var i=0; i<length; i++){
                let optionTitleA = $optionTitles.get(i).value;
                // see if it is null
                if(optionTitleA === "" || optionTitleA == null){
                    $("#validationMsg").text("Option Title Cannot Be Null!")
                    e.preventDefault();
                    return;
                }

                // see if there are other titles which are the same as current title
                for(var j=i+1; j<length; j++){
                    let optionTitleB = $optionTitles.get(j).value;
                    if(optionTitleA === optionTitleB){
                        $("#validationMsg").text("Option Title Cannot Be Repetitive!")
                        e.preventDefault();
                        return;
                    }
                }
            }
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
    mounted(){
        let pathname = window.location.pathname;
        let commId = pathname.substring(pathname.length-1, pathname.length);
        this.commId = commId;
        //console.log(commId);
        axios.get('/topics/'+commId+'/'+1)
            .then(function (response){
                getTopicList(response);
            })
            .catch(function (error){
                console.log(error);
            })
    }
})

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
        // get current (device) time again
        var currentTimeMS2 = new Date().getTime();
        // get server time
        // getServerTime要发送ajax请求，因此是一个异步任务
        await getServerTime();
        var serverTimeMS = new Date(vue.serverTime).getTime();
        // calculate the deviation
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






