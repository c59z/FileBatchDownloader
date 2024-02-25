<template>
  <div class="main">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="grid-content bg-purple main-menu">
          <!--点击后弹出选择文件列表窗口-->
          <el-button
              type="success"
              round
              @click="openTree()"
          >
            下载
          </el-button>
        </div>
      </el-col>
      <el-col :span="24">
        <div class="grid-content bg-purple main-list">
          <!-- 显示文件路径 -->
          <div>
            <span style="font-size: 18px">FilePath : </span>
            <!--根目录-->
            <span  
              class="myPath"
              style="cursor: pointer;"
              @click="getCurrentPathFiles('/')"
            > / </span>
              
            <span 
              class="myPath"
              style="cursor: pointer;" v-for="(folder, index) in currentPath"
              :key="index"
              @click="getFilesBySpan(index)"
              >
        {{  folder }}
            </span>
            <!-- <span v-if="index !== currentPath.length - 1"> / </span> -->
          </div>

          <!--文件列表-->
          <div>
            <ul style="padding: 0">
              <li
                  class="my-li"
                  v-for="(item, index) in fileList"
                  :key="index"
                  @click="handleItemClick(item)"
                  :class="{'my-li-folder' : item.type==='folder'}"
              >
                {{ item.name }}
              </li>
            </ul>
          </div>

        </div>
      </el-col>
    </el-row>
    <transition name="el-zoom-in-bottom">
    <div v-show="showProgress" class="downloadProgress">
      <div class="downloadProgressNative">
        <div class="downloadTitle">
          {{ this.downloadMarkings }}
        </div>
        <div @click="showProgress = !showProgress" class="closeBtn">
          <i class="el-icon-close"></i>
        </div>
      </div>
      <div class="downloadProgressList">
        <!-- 这里需要使用v-for循环生成列表 -->
        <div 
          v-for="(item, index) in this.downloadingList"
          class="downloadProgressLi"
        >
          <el-row>
            <el-col :span="12">
              <div class="downloadingText">
                {{ extractFileName(item.fileName) }}
              </div>
            </el-col>
            <el-col :span="12">
              <div class="ProgressStyle">
                <el-progress :show-text="false" :percentage="item.progress" :color="customColor"></el-progress>
              </div>
            </el-col>
          </el-row>
        </div>
        
      </div>
    </div>
  </transition>

  <div v-show="showProgress === false && this.downloadingList.length>0" @click="showProgress = !showProgress" class="showBtn">
      <i class="el-icon-download"></i>
  </div>

    <el-dialog title="创建下载任务" :visible.sync="dialogTableVisible">
      <div class="myDialogTreeDiv">
        <el-tree
            ref="tree"
            :data="treeData"
            show-checkbox
            node-key="id"
            :default-expand-all="true"
            :default-checked-keys="[]"
            :props="defaultProps">
        </el-tree>
      </div>
      <div>
        <el-button type="success" round @click="downloadFiles">下载</el-button>
        <el-button type="info" round @click="dialogTableVisible = false">取消</el-button>
      </div>
    </el-dialog>



  </div>
</template>

<script>
// import request from '../utils/request.js'
import axios from "axios";
import JSZip from "jszip";
import FileSaver from "file-saver";

export default {
  name: 'Mains',
  components: {

  },
  data() {
    return {
      currentPath: [], // 存储当前文件路径的数组
      fileList: [ // 模拟文件列表数据
        {name: 'Folder1', type: 'folder'},
        {name: 'Folder2', type: 'folder'},
        {name: 'File1.txt', type: 'file'},
      ],
      // 选择下载任务窗口可见性
      dialogTableVisible: false,

      // 树相关
      treeData: [{
        id: 1,
        label: '根目录',
        children: [{
          id: 2,
          label: 'anmi',
          children: [
              {
                id: 3,
                label: 'wallhaven-831pe1.png'
              },
          ]
        },
          {
            id:4,
            label: "hiten",
            children: [
              {
                id: 5,
                label: '(C101) [HitenKei (Hiten)] deLIGHTful (オリジナル)'
              },
            ]
          }
        ],
      },
      ],
      defaultProps: {
        children: 'children',
        label: 'label'
      },

      // 文件路径请求
      requestDto: {
        path: "",
      },

      // 下载进度条窗口
      showProgress:false,
      // 下载进度
      progress: 0,
      // 进度条颜色
      customColor: '#E91E63',
      // socket request
      sockteReq:{
        "Action":"",
        "Message":"",
        "downloadList":[]
      },
      downloadingList:[
        // {
        //   "fileName":"file1",
        //   "progress":0
        // },
        // {
        //   "fileName":"file2",
        //   "progress":0
        // },
        // {
        //   "fileName":"file3",
        //   "progress":0
        // },
    ],
    downloadMarkings: "Downloading",
      // 单个文件
      localFileChunk:"",
      // 
      localFileDataList:[
        {
          data:"",
          fileName:"",
        },
      ],
    };
  },
  methods: {
    // 获取指定路径下的文件列表
    getCurrentPathFiles(path) {
      if(path === "/"){
        this.currentPath = []
      }
      var requestUrl = "http://localhost:7777/File/getCurrentFiles"
      this.requestDto.path = path
      axios.post(requestUrl,this.requestDto).then(res => {
        var resData = res.data
        console.log(resData);
        this.fileList = resData.data
      })
    },
    // 点击文件列表
    handleItemClick(item) {
      if (item.type === 'folder') {
        // 点击了文件夹，更新文件路径数组
        this.currentPath.push(item.name);
        //通过后端接口获取文件夹下的文件列表并更新 fileList
        var targetPath = "/"+this.currentPath.join("/")
        this.getCurrentPathFiles(targetPath);
      } else {
        // 点击了文件
        console.log(`点击文件：${item.name}`);
      }
    },
    // 点击路径中的某个文件夹，实现路径跳转
    getFilesBySpan(index){
      this.currentPath = this.currentPath.slice(0, index + 1);
      var targetPath = "/"+this.currentPath.join("/")
        this.getCurrentPathFiles(targetPath);
    },
    // 获取树
    async openTree(){
      this.dialogTableVisible = true
      var requestUrl = "http://localhost:7777/File/getFileTree"
      await axios.get(requestUrl).then(res => {
        var resData = res.data
        console.log(resData);
        this.treeData = resData.data
      })
    },
    progressInit(){
      this.downloadMarkings = "Downloading"
      this.showProgress = true
    },
    extractFileName(filename) {
      // 使用split方法将字符串按斜杠分割成数组，然后取最后一个元素
      const parts = filename.split('/');
      console.log(parts[parts.length - 1]);
      return parts[parts.length - 1];
    },
    // 批量下载文件
    async downloadFiles(){
      const socket = new WebSocket('ws://localhost:7777/DownloadFile');
      this.dialogTableVisible = false

      // 获取所有被选中的文件key
      this.sockteReq.downloadList = this.$refs.tree.getCheckedKeys()
      if(this.sockteReq.downloadList.length === 0){
        this.$message({
          showClose: true,
          message: '请选择要下载的文件~'
        });
        return;
      }

      // 打开socket连接
      socket.onopen = () => {
        console.log('WebSocket 连接成功');
        this.sockteReq.Action = "Download"
        this.sockteReq.Message = "start download"
        this.progressInit()
        // this.downloadingList = this.sockteReq.downloadList

        // 发送下载请求，后端开始准备文件
        socket.send(JSON.stringify(this.sockteReq));
        // this.sockteReq.downloadList.forEach
        this.sockteReq = {}
      };

      socket.onmessage = async (event) => {
        const data = JSON.parse(event.data);
        console.log(data);
        if(data.Action==="Downloading"){
          var currentFile = data.fileChunk.fileName.replace(/\\/g, '/');
          console.log(currentFile);
          // 下载中...
          console.log("下载文件: "+currentFile);
          console.log("已下载:"+this.progress+"%");
          // this.progress = parseInt(data.progress);
          this.localFileChunk += data.fileChunk.data

          // 更新下载进度
          for(var i=0;i<this.downloadingList.length;i++){
            if(this.downloadingList[i].fileName === currentFile){
              // console.log("正在修改 "+currentFile+" 的下载进度");
              this.downloadingList[i].progress = parseInt(data.progress);
            }
          }

          // this.sockteReq.Action = "Download"
          // this.sockteReq.Message = "NextChunk"
          // // 发送下载请求，后端开始准备文件
          // socket.send(JSON.stringify(this.sockteReq));
          // this.sockteReq = {}

          if(data.fileChunk.chunkIndex === data.fileChunk.totalChunks - 1){
            console.log("已经获取了当前文件的所有块，开始处理中...");

            // todo 把当前文件块转为原始的字节数组
            const base64Data = this.localFileChunk;
            // console.log("base64Data:", base64Data);
            // 使用 atob 进行解码
            const decodedData = atob(base64Data);

            // 将解码后的字符串转为字节数组
            const byteArray = new Uint8Array(decodedData.length);
            for (let i = 0; i < decodedData.length; i++) {
                byteArray[i] = decodedData.charCodeAt(i);
            }

            
            // 传输完毕
            this.localFileDataList.push({data:byteArray,fileName:data.fileChunk.fileName})

            this.localFileChunk = "" //清空本地的文件块缓存，等待下一个文件的传输

          }
          // 把所有文件下载完成后打包成zip文件
        }else if(data.Action==="Finish"){
          // 下载完成
          this.downloadMarkings = "Finished"
          socket.close();
          var zip = new JSZip();
          var files = [];          

          for(const localFileData of this.localFileDataList){
            files.push(localFileData)
          }

          for (const file of files) {
            zip.file(file.fileName, file.data);
          }
          var zipBlob = await zip.generateAsync({ type: "blob" });
          FileSaver.saveAs(zipBlob, "files.zip");

          // 清除缓存
          this.localFileDataList = []
          files = undefined
          zip = undefined
          this.zipBlob = undefined
          
        }else if(data.Action === "DownloadInfo"){
          // console.log("收到下载信息:\n"+data);
          var tempList = Array.from(data.downloadList)
          this.downloadingList = tempList.map(filename => {
            return {
              fileName: filename,
              progress: 0
            }
          })
        }
        
      };

      // 关闭socket连接
      socket.onclose = () => {
        console.log('WebSocket 连接关闭');
      };

    }

  },
  mounted() {
    this.getCurrentPathFiles("/")
    
  }
}
</script>

<style scoped>

* {
  color: #fff;
}

.main {
  padding: 1rem;
  width: 50%;
  height: 45rem;
  display: inline-block;
  margin: 6rem auto;
  background-color: rgb(48, 48, 48);
  overflow: hidden;
}

.main-menu {
  float: left;
  margin-bottom: 1rem;
}

.main-list {
  padding: 0.5rem;
  text-align: left;
  background-color: rgb(66, 66, 66);
  border-radius: 1rem;
}

.my-li {
  padding: 0 0.2rem;
  border-radius: 0.2rem;
  list-style: none;
  margin: 0.5rem 0;
}

.my-li:hover {
  background-color: rgba(255, 255, 255,0.3) ;
}

.my-li-folder {
  cursor: pointer;
}

/* 重写 */
/deep/ .el-dialog {
  background: rgb(77,77,77);
}

/deep/ .el-dialog__title {
  color: white;
}
/deep/ .el-tree {
  border-radius: 1rem;
  border: 1px solid #999;
  background-color: rgba(0,0,0,0);
  color: white;
}
/deep/ .el-tree-node:focus>.el-tree-node__content {
  background-color: rgba(0,0,0,0);
}

/deep/ .el-tree-node__content {
  background-color: rgba(0,0,0,0) !important;
}

.myPath {
  margin: 0 8px;
  display: inline-block;
  padding: 2px 6px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 5px;
}

/deep/ .el-tree {
  margin-right: 10px;
  margin-bottom: 10px;
}

.myDialogTreeDiv {
  height: 50vh;
  overflow-y: scroll;
}

/* 垂直滚动条样式 */
/* 宽度 */
::-webkit-scrollbar {
  width: 10px;
  border-radius: 5xp;
}

/* 背景 */
::-webkit-scrollbar-track {
  background: linear-gradient(to bottom, #f5f5f5, #e8e8e8); 
}

/* 滑块 */
::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, #a1a1a1, #6b6b6b);
  border-radius: 5px; /* 圆角 */
}

/* 下载进度 */

.downloadProgress {
  position: fixed;
  width: 24rem;
  height: 30rem;
  top: calc(100vh - 30rem);
  left: calc(50% - 12rem) ;
  background-color: rgb(29, 29, 29);
  border-radius: 0.5rem;
}

.downloadProgressNative {
  /* background-color: skyblue; */
  height: 5rem;
  border-radius: 0.5rem;
}

.downloadProgressList {
  height: 25rem;
  overflow-y: scroll;
  /* border-radius: 0.5rem; */
  /* background-color: #fff; */
}

.downloadTitle , .closeBtn , .showBtn {
  position: relative;
  font-size: 24px;
  display: inline-block;
}

.showBtn {
  width: 59px;;
  height: 59px;
  border-radius: 30px;
  background-color: #000;
  position: fixed;
  right: 5rem;
  bottom: 5rem;
  background-color: rgb(156, 39, 176);
}

.downloadTitle {
  height: 36px;
  line-height: 24px;
  left: -4rem;
}

.closeBtn {
  padding-top: 1rem;
  width: 2.5rem;
  height: 2.5rem;
  right: -4rem;
}

.closeBtn:hover , .showBtn:hover{
  cursor: pointer;
}

/deep/ .el-icon-close {
  line-height: 2.5rem;
  font-size: 2.5rem;
}

.downloadProgressLi {
  padding: 1rem;
  text-align: left;
  border-top: 1px solid #6b6b6b ;
  border-bottom: 1px solid #6b6b6b ;
}

.downloadingText {
  /* height: 36px; */
  padding: 0 2px;
  font-size: 16px;
  line-height: 16px;
  /* -webkit-line-clamp: 2; 
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis; */
}

.ProgressStyle {
  height: 36px;
}

/deep/  .el-progress {
  padding-top: 15px;
}

/deep/ .el-icon-download {
  padding: 12px;
  font-size: 32px;
}

</style>
