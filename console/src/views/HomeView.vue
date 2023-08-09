<script lang="ts" setup>
import {Toast, VPageHeader} from "@halo-dev/components";
import apiClient from "@/utils/api-client";
import {reactive} from "vue";
import codeImage from '@/assets/code_image.png'

const operationData = reactive({
  userExist: false,
  userInfo: {
    userName: "",
    userPassword: "",
    registrationCode: "",
  },
  isImageVisible: false,
  registrationCodeUrl: "",
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
};

getNpsUser();
</script>
<template>
  <VPageHeader title="nps管理">
    <template #icon></template>
  </VPageHeader>
  <div class="nps-container">
    <div class="nps-form-group">
      <div class="nps-title">
        NPS账号{{ operationData.userExist ? "登录" : "注册" }}
      </div>
      <form @submit.prevent="loginOrRegister">
        <div>
          <label for="loginUserName">账号：</label>
          <input
            id="loginUserName"
            v-model="operationData.userInfo.userName"
            type="text"
            disabled
          />
        </div>
        <div v-show="!operationData.userExist">
          <label for="loginUserPassword">密码：</label>
          <input
            id="loginUserPassword"
            v-model="operationData.userInfo.userPassword"
            type="text"
            placeholder="首次激活请输入密码"
          />
          <label for="loginUserPassword"
          >注册码：<a
            style="color: #4461f3"
            title="点击获取注册码"
            @click="operationData.isImageVisible = true"
          >获取注册码</a
          ></label
          >
          <input
            id="registrationCode"
            v-model="operationData.userInfo.registrationCode"
            type="text"
            placeholder="请输入注册码"
          />
        </div>
        <button class="nps-button" type="submit">
          {{ operationData.userExist ? "登录" : "注册" }}
        </button>
      </form>
    </div>

    <div v-if="operationData.isImageVisible" class="nps-image-container">
      <span></span> <span></span> <span></span> <span></span>
      <div class="nps-image-container-inner">
        <h2>微信扫一扫，获取注册码</h2>
        <div class="nps-image-content">
          <img
            :src="operationData.registrationCodeUrl"
            alt="扫码获取注册码"
            class="nps-image"
            v-on:error="operationData.registrationCodeUrl=codeImage"
          />
          <button class="nps-btn" @click="operationData.isImageVisible = false">
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<style>
.nps-container {
  width: 100%;
  padding-right: 0.5rem;
  padding-left: 0.5rem;
  padding-top: 10rem;
}

.nps-title {
  font-size: 30px;
  font-weight: bold;
  text-align: center;
  line-height: 80px;
}

.nps-form-group {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
}

.nps-form-group label {
  display: block;
  margin-bottom: 5px;
}

.nps-form-group input[type="text"],
.nps-form-group input[type="password"] {
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
  width: 100%;
}

.nps-form-group button {
  padding: 10px;
  background: #007bff;
  border: none;
  color: #fff;
  cursor: pointer;
  border-radius: 5px;
  width: 100%;
}

.nps-form-group button:hover {
  background: #0069d9;
}

.nps-button {
  margin-top: 20px;
  margin-bottom: 5px;
}

.nps-image-container {
  position: relative;
  padding: 130px 0;
  width: 320px;
  height: 480px;
  background: #0c0116;
  overflow: hidden;
  box-shadow: 0 0 10px #747772;
  border-radius: 5px;
  margin: -500px auto 50px;
}

.nps-image-container-inner {
  position: absolute;
  height: 98%;
  width: 98%;
  top: 50%;
  left: 50%;
  background: #0c0116;
  transform: translate(-50%, -50%);
}

.nps-image-content {
  height: 100%;
  width: 100%;
  padding: 25px;
}

.nps-image-content .nps-image {
  width: 100% !important;
  height: auto !important;
}

.nps-image-container-inner h2 {
  font-size: 25px;
  color: #d7a3d7;
  text-align: center;
  padding-top: 35px;
}

.nps-btn {
  cursor: pointer;
  color: white;
  margin-top: 40px;
  width: 100%;
  padding: 12px;
  outline: none;
  background: #800080;
  border: none;
  font-size: 18px;
  border-radius: 10px;
  transition: 0.4s;
}

.nps-btn:hover {
  background: #c907c9;
}

.nps-image-container span {
  position: absolute;
  height: 50%;
  width: 50%;
}

.nps-image-container span:nth-child(1) {
  background: #ffda05;
  top: 0;
  left: -48%;
  animation: 5s span1 infinite linear;
  animation-delay: 1s;
}

.nps-image-container span:nth-child(2) {
  background: #00a800;
  bottom: 0;
  right: -48%;
  animation: 5s span2 infinite linear;
}

.nps-image-container span:nth-child(3) {
  background: #800080;
  right: -48%;
  top: 0px;
  animation: 5s span3 infinite linear;
}

.nps-image-container span:nth-child(4) {
  background: #ff0000;
  bottom: 0;
  right: -48%;
  animation: 5s span4 infinite linear;
  animation-delay: 1s;
}

@keyframes span1 {
  0% {
    top: -48%;
    left: -48%;
  }
  25% {
    top: -48%;
    left: 98%;
  }
  50% {
    top: 98%;
    left: 98%;
  }
  75% {
    top: 98%;
    left: -48%;
  }
  100% {
    top: -48%;
    left: -48%;
  }
}

@keyframes span2 {
  0% {
    bottom: -48%;
    right: -48%;
  }
  25% {
    bottom: -48%;
    right: 98%;
  }
  50% {
    bottom: 98%;
    right: 98%;
  }
  75% {
    bottom: 98%;
    right: -48%;
  }
  100% {
    bottom: -48%;
    right: -48%;
  }
}

@keyframes span3 {
  0% {
    top: -48%;
    left: -48%;
  }
  25% {
    top: -48%;
    left: 98%;
  }
  50% {
    top: 98%;
    left: 98%;
  }
  75% {
    top: 98%;
    left: -48%;
  }
  100% {
    top: -48%;
    left: -48%;
  }
}

@keyframes span4 {
  0% {
    bottom: -48%;
    right: -48%;
  }
  25% {
    bottom: -48%;
    right: 98%;
  }
  50% {
    bottom: 98%;
    right: 98%;
  }
  75% {
    bottom: 98%;
    right: -48%;
  }
  100% {
    bottom: -48%;
    right: -48%;
  }
}
</style>
