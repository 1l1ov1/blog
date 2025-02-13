<script setup>
import { ref } from 'vue'
const prop = defineProps({
    loginType: {
        type: String,
        required: true
    }
})
const form = ref({
    phone: '',
    password: '',
    captcha: ''
})
const formRules = ref({
    phone: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        {
            pattern: /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
            message: '密码必须包含至少一个大写字母、一个数字和一个特殊符号',
            trigger: 'blur'
        }
    ],
    captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})
</script>

<template>
    <el-form :rules="formRules" v-show="loginType === 'phone'" :model="form" ref="formRef">
        <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" prefix-icon="Iphone" size="large" clearable />
        </el-form-item>
        <el-form-item prop="captcha">
            <el-input v-model="form.captcha" placeholder="请输入验证码" prefix-icon="Key" size="large" clearable
                style="width: 60%; margin-right: 5%;" />
            <el-button type="primary" size="large" style="width: 35%;">
                获取验证码
            </el-button>
        </el-form-item>
    </el-form>
</template>

<style lang='less' scoped></style>
