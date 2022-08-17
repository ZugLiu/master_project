var vue = new Vue({
    el:'#vue',
    data:{
        'search_word':'',
        'community_result':'',
        'topic_result':'',
    },
    beforeMount(){
        this.search_word = $('#search_word').attr('value');
        console.log(this.search_word);
        /**
         * get all topics and comms list,
         * search in that list and get result.
         */
      axios({
          url:'allTopics',
          method:'get',
      }).then(function (response){
          var topics = response.data;
          console.log(topics);
          var topicIdx = new lunr(function (){
              this.ref('id');
              this.field('title');
              this.field('content');
              topics.forEach(function (doc){
                  this.add(doc)
              }, this);
          });
          let search = topicIdx.search(vue.search_word);
          // 这个搜索结果里面，有用的信息只有ref，要获取其他信息,
          // 就得从之前获取的所有信息中再检索
          // 这就显得很蠢，后面数据量大了以后会很慢
          // 说明lunr.js确实不适合站内搜索，顶多适合对当前文本搜索
          console.log(search);
          let length = topics.length;
          let search_length = search.length;
          let topic_result = [];
          for(var i=0; i<length; i++){
              let topicId = topics[i].id;
              for(var j=0; j<search_length; j++){
                  let ref = search[j].ref;
                  if(ref == topicId){
                      topic_result.push(topics[i]);
                  }
              }
          }

          vue.topic_result = topic_result;

      }).catch(function (error){
          console.log(error)
      });

      axios({
            url:'/allComm',
            method: 'get',
        }).then(function (response){
            var comms = response.data;
            console.log(comms);
            var commIdx = new lunr(function (){
                this.ref('id');
                this.field('cname');
                this.field('cabout');
                this.field('ccreator');
                comms.forEach(function (doc){
                    this.add(doc)
                }, this);
            })
          let search = commIdx.search(vue.search_word);
          console.log(search);
          let length = comms.length;
          let search_length = search.length;
          let comm_result = [];
          for(var i=0; i<length; i++){
              let commsId = comms[i].id;
              for(var j=0; j<search_length; j++){
                  let ref = search[j].ref;
                  if(ref == commsId){
                      comm_result.push(comms[i]);
                  }
              }
          }

          vue.community_result = comm_result;


      }).catch(function (error){
          console.log(error)
      })
    }
})