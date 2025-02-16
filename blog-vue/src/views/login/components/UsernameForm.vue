<script setup>
import { ref, onMounted } from 'vue'
import { getCaptcha } from '@/api/auth'
const prop = defineProps({
    loginType: {
        type: String,
        required: true
    }
})
const form = ref({
    username: '',
    password: '',
    captcha: ''
})
const formRules = ref({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        {
            pattern: /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]$/,
            message: '密码必须包含至少大写字母、数字和特殊符号',
            trigger: 'blur'
        },
        {
            min: 8,
            max: 15,
            message: '密码长度在 8 到 15 个字符',
            trigger: 'blur'
        }
    ],
    captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})
const captcha = ref('')
const captchaKey = ref('')
// 获取验证码
const fetchCaptcha = async () => {
    const response = await getCaptcha();
    // 设置令牌，登录的时候要用这个令牌去redis中查询验证码
    captchaKey.value = response.headers['x-captcha-key']
    captcha.value = URL.createObjectURL(response.data)

}

onMounted(() => {
    fetchCaptcha();
})
</script>

<template>
    <el-form :rules="formRules" v-show="prop.loginType === 'username'" :model="form" ref="formRef">
        <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" clearable />
        </el-form-item>
        <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password
                clearable />
        </el-form-item>
        <el-form-item prop="captcha">
            <el-input v-model="form.captcha" placeholder="请输入验证码" prefix-icon="Key" size="large" clearable
                style="width: 60%; margin-right: 2%;" />
            <el-image :src="captcha" style="height: 100%; width: 38%;" @click="fetchCaptcha()" />
        </el-form-item>
    </el-form>
</template>

<style lang='less' scoped></style>
