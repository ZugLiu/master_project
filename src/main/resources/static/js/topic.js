/**
 * set a countdown
 * @param ddl
 * @param el
 */
function setCountDown(distance){
    setInterval(()=>{
        displayClock(distance);
        distance -= 1000;
    }, 1000);
}

/**
 * convert time (in ms) to YYYY MM DD HH:MM:SS
 * and show it on #el
 * @param time
 * @param el
 */
function displayClock(time){
    var d,h,m,s;
    if(time < 0){
        $("#voteTime").text("countdown is over!");
        return;
    }else{
        d = Math.floor(time/1000/60/60/24);
        h = Math.floor(time/1000/60/60%24);
        m = Math.floor(time/1000/60%60);
        s = Math.floor(time/1000%60);
    }
    $("#voteTime").text(d+" days "+h+" hours "+m+" minutes "+s + " seconds");
}

/**
 * 获取当前topic下所有option的投票数，
 * 用于制作投票结果图
 */
function getNumOfVotes(topicId){
    axios({
        method:'get',
        url:'/getOptionsByTopic/'+topicId,
    }).then(function (response){
        vue.options = response.data;
        console.log(vue.options);
        drawBarChart(vue.options);
        drawPieChart(vue.options);
    }).catch(function (error){
        console.log(error);
    })
}

var vue = new Vue({
    el:"#vue",
    data:{
        options:'',
        optionCounter:0,
    },
    methods:{
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
        /**
         * 切换显示柱状图
         * @param e
         */
        "toggleBarChart":function (e){
            $('#container').toggle();
        },
        /**
         * 切换显示饼状图
         * @param e
         */
        "togglePieChart":function (e){
            $('#pieChartContainer').toggle();
        },
        /**
         * 用户创建投票选项
         * @param e
         */
        "userCreateOptions":function (e){
            this.optionCounter += 1;
            let form = $('#user-create-options');
            // create a div
            let $newOption = $('<div class="mb-3" >\n' +
                '                        <span class="input-group-text">Title<button type="button" class="btn-close btn-option-del"></button></span>\n' +
                '                        <input type="text" class="form-control">\n' +
                '                        <span class="input-group-text">Option Content</span>\n' +
                '                        <textarea class="form-control" rows="3"></textarea>\n' +
                '                    </div>');

            //console.log($newOption.children());

            // get all children of that div
            let children = $newOption.children();

            // get the delete button from children and add an event listener
            let btn = children.eq(0).children();
            console.log(btn);
            btn.click(function (){
                $newOption.remove();
                vue.optionCounter-=1;
            })

            // set option title's name
            let title = children.eq(1);
            title.attr('name', 'optionTitle'+this.optionCounter);

            // set option content's name
            let content = children.eq(3);
            content.attr('name', 'optionContent'+this.optionCounter);
            //console.log(title);
            //console.log(content);

            //add the div to the form
            form.append($newOption);
        },
        /**
         * 用户打开评论回复框
         * @param e
         */
        "replyToComment":function (e){
            let comment_btn_id = e.target.id;
            let idNum = comment_btn_id.replace(/[^0-9]/ig, "");
            $("#reply_form_to_"+idNum).toggle();
        }
    },
    /**
     * vue挂载后，获取当前topic的所有options，
     * 检查当前用户对哪些option已经投过票，对投过票的option进行高亮
     *
     * 之后判断当前时刻是否可以投票，根据结果显示不同的提示信息以及结果展示
     */
    mounted:function (){
        // 获知当前用户已经对此topic下哪些option投过票，投过票的option要高亮
        axios({
            url:'/getUserVotedOption',
            method: 'GET'
        }).then(function (response){
            let data = response.data;
            for(let i = 0; i<data.length; i++){
                let userVotedOptionId = data[i].id;
                $("#vote"+userVotedOptionId).addClass("active");
            }
        }).catch(function (error){
            console.log(error);
        })


        //判断时间
        const voteStartTime = $("#vst").text();
        const voteEndTime = $("#vet").text();
        let vStartTime = new Date(voteStartTime);
        let vEndTime = new Date(voteEndTime);

        let currTime = new Date();

        if(currTime<vStartTime){
            console.log("vote does not start")
            $("#voteTitle").text("Vote does not started yet. You can add options now. Vote starts in:")
            // set a countdown to vote start time
            let distance = vStartTime.getTime() - currTime.getTime();
            setCountDown(distance);

            // let user create options part show
            $("#create-options").removeAttr("hidden");
        }
        else if(vStartTime < currTime && currTime < vEndTime){
            console.log("vote is now open")
            $("#voteTitle").text("Vote opens now! Come and Vote! Time left:")
            // set a countdown to vote end time
            let distance = vEndTime.getTime() - currTime.getTime();
            setCountDown(distance);

        }else if(vEndTime < currTime){
            $("#voteTitle").text("Vote ended. See the result and obey!")
            $("#btns").removeAttr("hidden");
            let topicId = $("#topicId").text();
            getNumOfVotes(topicId);
        }
    }

})

/* vote result display*/
function drawBarChart(data){
    // 构建原始数据数组
    let original = data.map(function (s){
        let title = s.title;
        let votes = s.votes;
        return [title, votes];
    });
    let length = original.length;
    // 检查总票数是否为0
    let sum = 0;
    for(var i=0;i<length; i++){
        sum += original[i][1];
    }
    if(sum === 0){
        // 如果总票数为0，则没有必要展示结果，直接提示
        d3.select("#container svg")
            .append('text')
            .attr('x', 500)
            .attr('y', 300)
            .attr('text-anchor', 'middle')
            .style('font-size', 50)
            .style('fill', 'red')
            .text('No Votes To Any Options!');
        return;
    }

    const svg = d3.select('svg');
    const svgContainer = d3.select('#container');

    const margin = 80;
    const width = 1000 - 2 * margin;
    const height = 600 - 2 * margin;

    const chart = svg.append('g')
        .attr('transform', `translate(${margin}, ${margin})`);

    const xScale = d3.scaleBand()
        .range([0, width])
        .domain(data.map((s) => s.title))
        .padding(0.4)

    const yScale = d3.scaleLinear()
        .range([height, 0])
        .domain([0, d3.max(data, s=>s.votes)]);

// vertical grid lines
// const makeXLines = () => d3.axisBottom()
//   .scale(xScale)

    const makeYLines = () => d3.axisLeft()
        .scale(yScale)

    chart.append('g')
        .attr('transform', `translate(0, ${height})`)
        .call(d3.axisBottom(xScale));

    chart.append('g')
        .call(d3.axisLeft(yScale));

//vertical grid lines
    /*     chart.append('g')
          .attr('class', 'grid')
          .attr('transform', `translate(0, ${height})`)
          .call(makeXLines()
            .tickSize(-height, 0, 0)
            .tickFormat('')
          ) */

    // horizontal grid lines
    chart.append('g')
        .attr('class', 'grid')
        .call(makeYLines()
            .tickSize(-width, 0, 0)
            .tickFormat('')
        )

    const barGroups = chart.selectAll()
        .data(data)
        .enter()
        .append('g')

    barGroups
        .append('rect')
        .attr('class', 'bar')
        .attr('x', (g) => xScale(g.title))
        .attr('y', (g) => yScale(g.votes))
        .attr('height', (g) => height - yScale(g.votes))
        .attr('width', xScale.bandwidth())
        .on('mouseenter', function(actual, i) {
            d3.selectAll('.value')
                .attr('opacity', 0)

            d3.select(this)
                .transition()
                .duration(300)
                .attr('opacity', 0.6)
                .attr('x', (a) => xScale(a.title) - 5)
                .attr('width', xScale.bandwidth() + 10)

            const y = yScale(i.votes)

            line = chart.append('line')
                .attr('id', 'limit')
                .attr('x1', 0)
                .attr('y1', y)
                .attr('x2', width)
                .attr('y2', y)

            barGroups.append('text')
                .attr('class', 'divergence')
                .attr('x', (a) => xScale(a.title) + xScale.bandwidth() / 2)
                .attr('y', (a) => yScale(a.votes) + 30)
                .attr('fill', 'white')
                .attr('text-anchor', 'middle')
                .text((a, idx) => {
                    const divergence = (a.votes - i.votes).toFixed(1)

                    let text = ''
                    if (divergence > 0) text += '+'
                    text += `${divergence}`

                    return idx !== i ? text : '';
                })

        })
        .on('mouseleave', function() {
            d3.selectAll('.value')
                .attr('opacity', 1)

            d3.select(this)
                .transition()
                .duration(300)
                .attr('opacity', 1)
                .attr('x', (a) => xScale(a.title))
                .attr('width', xScale.bandwidth())

            chart.selectAll('#limit').remove()
            chart.selectAll('.divergence').remove()
        })

    barGroups
        .append('text')
        .attr('class', 'value')
        .attr('x', (a) => xScale(a.title) + xScale.bandwidth() / 2)
        .attr('y', (a) => yScale(a.votes) - 1)
        .attr('text-anchor', 'middle')
        .text((a) => `${a.votes}`)

    svg
        .append('text')
        .attr('class', 'label')
        .attr('x', -(height / 2) - margin)
        .attr('y', margin / 2.4)
        .attr('transform', 'rotate(-90)')
        .attr('text-anchor', 'middle')
        .text('Number of Votes')

    svg.append('text')
        .attr('class', 'label')
        .attr('x', width / 2 + margin)
        .attr('y', height + margin * 1.7)
        .attr('text-anchor', 'middle')
        .text('Options')

    svg.append('text')
        .attr('class', 'title')
        .attr('x', width / 2 + margin)
        .attr('y', 40)
        .attr('text-anchor', 'middle')
        .text('Vote Result')

    svg.append('text')
        .attr('class', 'source')
        .attr('x', width - margin / 2)
        .attr('y', height + margin * 1.7)
        .attr('text-anchor', 'start')
        .text('Source: Reason Forum, 2022')

}

function drawPieChart(data){
    // 构建原始数据数组
    let original = data.map(function (s){
        let title = s.title;
        let votes = s.votes;
        return [title, votes];
    });

    // 从参数中构建dataset：每个元素是一个数组，数组第0位是title，第1位是votes占总票数的百分比（保留1位小数）
    let l = original.length;
    let sum = 0;
    let dataset = [];
    // 第一次遍历：计算总票数；把每个option title放到dataset中
    for(var i=0; i<l; i++){
        sum += original[i][1];
        let temp = [];
        temp.push(original[i][0]);
        dataset.push(temp);
    }

    // 如果sum == 0，说明没有投票，打一行字，直接返回
    if(sum===0){
        d3.select("#pieChartContainer")
            .append('svg')
            .append('text')
            .attr('x', 500)
            .attr('y', 300)
            .attr('text-anchor', 'middle')
            .style('font-size', 50)
            .style('fill', 'red')
            .text('No Votes To Any Options!');
        return;
    }

    // 第二次遍历：计算每个option的占比，并把它放到dataset中
    for(var i=0; i<l; i++){
        let percent = (original[i][1] / sum)*100;
        percent.toFixed(1); // 保留一位小数
        dataset[i].push(percent);
    }

    console.log(dataset);


    // 图表的宽度和高度
    var width = 600;
    var height = 600;

    //var dataset = [['Chrome', 72.5], ['IE', 5.3], ['Firefox', 16.3], ['Safari', 3.5], ['Opera', 1.0], ['Others', 1.4]];

    var pie = d3.pie()
        .sort(null)
        .value(function(d){
            return d[1];
        });
    var piedata = pie(dataset);

    var outerRadius = width / 4;
    var innerRadius = 0;

    var arc = d3.arc()
        .outerRadius(outerRadius)
        .innerRadius(innerRadius);

    var colors = d3.schemeSet3;

    var svg = d3.select('#pieChartContainer')
        .append('svg')
        .attr('width', width)
        .attr('height', height);

    var arcs = svg.selectAll('g')
        .data(piedata)
        .enter()
        .append('g')
        .attr('transform', 'translate(500 ,' + height / 2 + ')');

    arcs.append('path')
        .attr('fill', function(d, i){
            return colors[i];
        })
        .attr('d', function(d){
            return arc(d);
        });

    arcs.append('text')
        .attr('transform', function(d, i){
            var x = arc.centroid(d)[0] * 2.8;
            var y = arc.centroid(d)[1] * 2.8;
            if(i === 4) {
                return 'translate(' + (x * 1.2) + ', ' + (y * 1.2) + ')';
            } else if(i === 3) {
                return 'translate(' + (x - 40) + ', ' + y + ')';
            } else if(i === 5) {
                return 'translate(' + (x + 40) + ', ' + y + ')';
            }
            return 'translate(' + x + ', ' + y + ')';
        })
        .attr('text-anchor', 'middle')
        .text(function(d){
            var percent = Number(d.value) / d3.sum(dataset, function(d){
                return d[1];
            }) * 100;
            return d.data[0] + ' ' + percent.toFixed(1) + '%';
        })

    arcs.append('line')
        .attr('stroke', 'black')
        .attr('x1', function(d){ return arc.centroid(d)[0] * 2; })
        .attr('y1', function(d){ return arc.centroid(d)[1] * 2; })
        .attr('x2', function(d, i){
            if(i === 4) {
                return arc.centroid(d)[0] * 3.2;
            }
            return arc.centroid(d)[0] * 2.5;
        })
        .attr('y2', function(d, i){
            if(i === 4) {
                return arc.centroid(d)[1] * 3.2;
            }
            return arc.centroid(d)[1] * 2.5;
        });
}
