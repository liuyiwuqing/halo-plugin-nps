<script lang="ts" setup>
import {Toast, VPageHeader} from "@halo-dev/components";
import apiClient from "@/utils/api-client";
import {onMounted, reactive, ref} from "vue";
import VPN from "~icons/mdi/vpn";
import codeImage from '@/assets/code_image.png'

const operationData = reactive({
  userExist: false,
  userInfo: {
    userName: "",
    userPassword: "",
    registrationCode: "",
  },
  registrationCodeUrl: "",
  registrationCodeBoxShow: false,
  formBoxShow: true,
});

const getNpsUser = async () => {
  const {data} = await apiClient.get(
    `/apis/nps.lywq.site/v1alpha1/plugins/PluginNps/getNpsUser`
  );
  if (data.status === 0) {
    operationData.userInfo = data.data;
    operationData.userExist = true;
  } else {
    operationData.userInfo.userName = data.data.userName;
    operationData.registrationCodeUrl = data.data.registrationCodeUrl;
    operationData.userExist = false;
    Toast.warning(data.msg);
  }
  
  let npsContainer = document.getElementById("nps-container");
  if (npsContainer) {
    npsContainer.style.display = "block";
  }
};

const loginOrRegister = async () => {
  if (operationData.userExist) {
    const {data} = await apiClient.post(
      `/apis/nps.lywq.site/v1alpha1/plugins/PluginNps/userLogin?userName=${operationData.userInfo.userName}&userPassword=${operationData.userInfo.userPassword}`
    );
    if (data.status === 0) {
      Toast.success(data.msg);
      window.open(data.data, "_blank");
    } else {
      operationData.userExist = false;
      Toast.warning(data.msg);
    }
  } else {
    if (operationData.userInfo.userPassword === "") {
      Toast.warning("请输入密码");
      return;
    } else if (operationData.userInfo.registrationCode === "") {
      Toast.warning("请输入注册码");
      operationData.formBoxShow = false;
      operationData.registrationCodeBoxShow = true;
      return;
    } else {
      const {data} = await apiClient.post(
        `/apis/nps.lywq.site/v1alpha1/plugins/PluginNps/userRegister?userName=${operationData.userInfo.userName}&userPassword=${operationData.userInfo.userPassword}&registrationCode=${operationData.userInfo.registrationCode}`
      );
      if (data.status === 0) {
        operationData.userInfo = data.data;
        operationData.userExist = true;
        Toast.success(data.msg);
        await loginOrRegister();
      } else {
        Toast.warning(data.msg);
      }
    }
  }
};

//初始化
onMounted(() => {
  getNpsUser();
});
</script>
<template>
  <VPageHeader title="nps管理">
    <template #icon>
      <VPN class="mr-2 self-center"/>
    </template>
  </VPageHeader>
  <div id="nps-container" class="nps-container" style="display: none">
    <div class="form-box" v-show="operationData.formBoxShow">
      <form class="form" @submit.prevent="loginOrRegister">
        <span class="title">NPS账号{{ operationData.userExist ? "登录" : "注册" }}</span>
        <span class="subtitle">NPS内网穿透服务快速登录</span>
        <div class="form-container">
          <h1>{{ operationData.userInfo.userName }}</h1>
          <input hidden v-model="operationData.userInfo.userName" type="text" class="input" placeholder="账号" disabled>
          <input v-show="!operationData.userExist" v-model="operationData.userInfo.userPassword" type="text"
                 class="input" placeholder="首次激活请输入密码">
          <input v-show="!operationData.userExist" v-model="operationData.userInfo.registrationCode" type="text"
                 class="input" placeholder="请输入注册码">
        </div>
        <button type="submit">{{ operationData.userExist ? "登录" : "注册" }}</button>
      </form>
      <div v-if="!operationData.userExist" class="form-section">
        <p>没有注册码? <a href="javascript:void(0);"
                          @click="operationData.formBoxShow = false; operationData.registrationCodeBoxShow = true;">点击获取</a>
        </p>
      </div>
    </div>

    <div v-show="operationData.registrationCodeBoxShow" class="registrationCode-box">
      <p class="cardHeading">微信扫一扫，获取注册码</p>
      <img
        :src="operationData.registrationCodeUrl"
        alt="扫码获取注册码"
        class="nps-image"
        v-on:error="operationData.registrationCodeUrl=codeImage"
      />
      <p class="cardPara">请使用手机微信扫一扫，根据提示获取注册码！</p>
      <div class="buttonContainer">
        <button class="closeBtn"
                @click="operationData.registrationCodeBoxShow = false; operationData.formBoxShow = true;">返回
        </button>
      </div>
    </div>
  </div>
</template>
<style>
.nps-container {
  width: 100%;
}

.nps-container .form-box {
  background-color: coral;
  margin-top: 10%;
  margin-left: auto;
  margin-right: auto;
  max-width: 300px;
  overflow: hidden;
  border-radius: 16px;
  color: #010101;
}

.nps-container .form {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 32px 24px 24px;
  gap: 16px;
  text-align: center;
}

/*Form text*/
.nps-container .title {
  font-weight: bold;
  font-size: 1.6rem;
}

.nps-container .subtitle {
  font-size: 1rem;
  color: #666;
}

/*Inputs box*/
.nps-container .form-container {
  overflow: hidden;
  border-radius: 8px;
  background-color: #fff;
  margin: 1rem 0 .5rem;
  width: 100%;
}

.nps-container .form-container > h1 {
  font-size: 1.2rem;
  font-weight: bold;
  color: burlywood;
}

.nps-container .input {
  text-align: center;
  background: none;
  border: 0;
  outline: 0;
  height: 40px;
  width: 100%;
  border-bottom: 1px solid #eee;
  font-size: .9rem;
  padding: 8px 15px;
}

.nps-container .form-section {
  padding: 16px;
  font-size: .85rem;
  background-color: #e0ecfb;
  box-shadow: rgb(0 0 0 / 8%) 0 -1px;
}

.nps-container .form-section a {
  font-weight: bold;
  color: #0066ff;
  transition: color .3s ease;
}

.nps-container .form-section a:hover {
  color: #005ce6;
  text-decoration: underline;
}

/*Button*/
.nps-container .form button {
  background-color: #0066ff;
  color: #fff;
  border: 0;
  border-radius: 24px;
  padding: 10px 16px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color .3s ease;
}

.nps-container .form button:hover {
  background-color: #005ce6;
}

.registrationCode-box {
  margin-top: 10%;
  margin-left: auto;
  margin-right: auto;
  width: 220px;
  height: 300px;
  background: rgb(245, 245, 245);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 35px;
  gap: 8px;
  box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.123);
  border-radius: 20px;
}

.registrationCode-box .cardHeading {
  color: black;
  font-weight: 600;
  font-size: 0.8em;
}

.registrationCode-box .cardPara {
  color: rgb(133, 133, 133);
  font-size: 0.6em;
  font-weight: 600;
  text-align: center;
}

.registrationCode-box .buttonContainer {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.registrationCode-box .closeBtn {
  width: 120px;
  height: 25px;
  background-color: rgb(168, 131, 255);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 0.7em;
  font-weight: 600;
  cursor: pointer;
}

.registrationCode-box .closeBtn:hover {
  background-color: rgb(153, 110, 255);
}
</style>
